package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.common.utils.Methods;
import io.codeleaf.common.utils.Types;
import io.codeleaf.sec.SecurityContext;
import io.codeleaf.sec.annotation.Authentication;
import io.codeleaf.sec.annotation.Authentications;
import io.codeleaf.sec.impl.DefaultSecurityContext;
import io.codeleaf.sec.impl.ThreadLocalSecurityProfileManager;
import io.codeleaf.sec.jaxrs.config.JaxrsZone;
import io.codeleaf.sec.jaxrs.spi.Authenticate;
import io.codeleaf.sec.jaxrs.spi.JaxrsHandshakeState;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;
import io.codeleaf.sec.profile.AuthenticationPolicy;
import io.codeleaf.sec.profile.SecurityProfile;
import io.codeleaf.sec.spi.AuthorizationLoader;
import io.codeleaf.sec.spi.SecurityContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public final class JaxrsZoneHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxrsZoneHandler.class);
    private static final Response UNAUTHORIZED = Response.status(Response.Status.UNAUTHORIZED).build();
    private static final Response SERVER_ERROR = Response.serverError().build();

    private final SecurityContextManager securityContextManager;
    private final SecurityProfile securityProfile;
    private final JaxrsHandshakeStateHandler handshakeStateHandler;

    private final CorsFilter corsFilter = new CorsFilter();
    private final Set<Object> filters = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            corsFilter, new PreMatchingFilter(), new PreResourceFilter(), new PostResourceFilter()
    )));

    public JaxrsZoneHandler(SecurityContextManager securityContextManager, SecurityProfile securityProfile, JaxrsHandshakeStateHandler handshakeStateHandler) {
        this.securityContextManager = securityContextManager;
        this.securityProfile = securityProfile;
        this.handshakeStateHandler = handshakeStateHandler;
        // TODO: set allowed Origins on CorsFilter, after this is available in AuthenticationConfiguration
    }

    public Set<Object> getFilters() {
        return filters;
    }

    @PreMatching
    public final class PreMatchingFilter implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) {
            ThreadLocalSecurityProfileManager.setSecurityProfile(securityProfile);
            URI requestUri = requestContext.getUriInfo().getRequestUri();
            LOGGER.debug("Processing request for endpoint: " + requestContext.getMethod() + " " + requestUri);
            JaxrsHandshakeSessionManager.get().setRequestContext(requestContext);
            if (handshakeStateHandler.isHandshakePath(requestContext.getUriInfo())) {
                setTrue(requestContext, "handshakeResource");
                requestContext.setProperty("authenticator", handshakeStateHandler.getHandshakeAuthenticatorName(requestContext.getUriInfo()));
            }
        }
    }

    public final class PreResourceFilter implements ContainerRequestFilter {

        @Context
        private ResourceInfo resourceInfo;

        @Override
        public void filter(ContainerRequestContext requestContext) {
            try {
                setTrue(requestContext, "matched");
                LOGGER.debug("Resource matched: " + resourceInfo.getResourceClass().getCanonicalName() + "#" + resourceInfo.getResourceMethod().getName());
                JaxrsHandshakeState state = handshakeStateHandler.extractHandshakeState(requestContext);
                LOGGER.debug("Extracted handshake state: " + (state == null ? "none" : state.getUri() + " " + state.getAuthenticatorNames()));
                if (isTrue(requestContext, "handshakeResource")) {
                    String authenticatorName = (String) requestContext.getProperty("authenticator");
                    if (!securityProfile.getRegistry().contains(authenticatorName, JaxrsRequestAuthenticator.class)) {
                        throw new SecurityException();
                    }
                    JaxrsRequestAuthenticator authenticator = securityProfile.getRegistry().lookup(authenticatorName, JaxrsRequestAuthenticator.class);
                    LOGGER.debug("Calling setHandshakeState() on " + authenticator.getClass().getCanonicalName());
                    state = authenticator.setHandshakeState(requestContext, resourceInfo, state);
                    if (state == null) {
                        setTrue(requestContext, "aborted");
                        requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                        throw new SecurityException("No state set by authenticator!");
                    }
                    setHandshakeState(requestContext, state);
                } else {
                    setHandshakeState(requestContext, state == null ? new JaxrsHandshakeState(requestContext.getUriInfo().getRequestUri()) : state);
                }
                Authentication authentication = Authentications.getAuthentication(resourceInfo.getResourceMethod(), resourceInfo.getResourceClass());
                JaxrsZone zone = JaxrsZones.getZone(requestContext.getUriInfo().getPath(), securityProfile);
                LOGGER.debug("Configuration: "
                        + "authentication = " + (authentication == null ? "none" : authentication.authenticator() + ":" + authentication.value())
                        + ", zone = " + (zone == null ? "none" : zone.getName()));
                String authenticatorName = determineAuthenticatorName(authentication, zone);
                AuthenticationPolicy policy = determinePolicy(authentication, zone, isTrue(requestContext, "handshakeResource") ? AuthenticationPolicy.NONE : AuthenticationPolicy.OPTIONAL);
                if (authenticatorName == null) {
                    if (policy == AuthenticationPolicy.REQUIRED) {
                        LOGGER.error("Policy is REQUIRED, but no authenticator set; aborting request");
                        setTrue(requestContext, "aborted");
                        requestContext.abortWith(UNAUTHORIZED);
                    } else {
                        if (policy == AuthenticationPolicy.OPTIONAL) {
                            LOGGER.warn("Policy is OPTIONAL, but no authenticator set; continuing request without authentication");
                        }
                        securityContextManager.setSecurityContext(requestContext, new DefaultSecurityContext(null, Collections.emptySet()));
                    }
                } else {
                    setExecutors(requestContext, authenticatorName);
                    SecurityContext securityContext = handleAuthentication(requestContext, policy);
                    if (securityContext != null) {
                        handleAuthorizations(zone, securityContext);
                    }
                }
                if (isTrue(requestContext, "handshakeResource")) {
                    setExecutors(requestContext, state.getFirstAuthenticatorName());
                    JaxrsHandshakeSessionManager.get().setExecutor(getCurrentExecutor(requestContext));
                }
            } catch (SecurityException cause) {
                if (!isTrue(requestContext, "aborted")) {
                    setTrue(requestContext, "aborted");
                    requestContext.abortWith(SERVER_ERROR);
                }
            } catch (IOException cause) {
                if (!isTrue(requestContext, "aborted")) {
                    setTrue(requestContext, "aborted");
                    requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                }
            }
        }

        private void handleAuthorizations(JaxrsZone zone, SecurityContext securityContext) throws SecurityException {
            for (String loaderName : zone.getAuthorizationLoaders()) {
                if (!securityProfile.getRegistry().contains(loaderName, AuthorizationLoader.class)) {
                    throw new SecurityException("No authorization loader found with name: " + loaderName);
                }
                AuthorizationLoader loader = securityProfile.getRegistry().lookup(loaderName, AuthorizationLoader.class);
                securityContext.getAuthorizations().addAll(loader.loadAuthorizations(securityContext.getAuthentication()));
            }
        }
    }

    public final class PostResourceFilter implements ContainerResponseFilter {

        @Context
        private ResourceInfo resourceInfo;

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
            try {
                if (!isTrue(requestContext, "matched")) {
                    LOGGER.warn("Endpoint not matched: " + requestContext.getRequest().getMethod() + " " + requestContext.getUriInfo().getAbsolutePath());
                } else if (isTrue(requestContext, "handshakeResource")) {
                    postHandshakeCall(requestContext, responseContext, resourceInfo);
                } else {
                    postServiceCall(requestContext, responseContext);
                }
            } finally {
                JaxrsHandshakeSessionManager.get().clear();
            }
        }
    }

    private void setExecutors(ContainerRequestContext requestContext, String authenticatorName) {
        JaxrsRequestAuthenticatorExecutor root = new RootRequestAuthenticatorExecutor(securityContextManager, handshakeStateHandler);
        JaxrsRequestAuthenticatorExecutor current = root;
        Map<String, JaxrsRequestAuthenticatorExecutor> executorIndex = new HashMap<>();
        while (authenticatorName != null) {
            current.setOnFailure(authenticatorName, securityProfile.getRegistry().lookup(authenticatorName, JaxrsRequestAuthenticator.class));
            current = current.getOnFailure();
            executorIndex.put(authenticatorName, current);
            authenticatorName = securityProfile.getAuthenticatorNodes().get(authenticatorName).getOnFailure();
        }
        requestContext.setProperty("executorRoot", root);
        requestContext.setProperty("executorIndex", executorIndex);
    }

    private RootRequestAuthenticatorExecutor getRootExecutor(ContainerRequestContext requestContext) {
        return (RootRequestAuthenticatorExecutor) requestContext.getProperty("executorRoot");
    }

    private JaxrsRequestAuthenticatorExecutor getCurrentExecutor(ContainerRequestContext requestContext) {
        JaxrsRequestAuthenticatorExecutor executor;
        JaxrsHandshakeState state = getHandshakeState(requestContext);
        Map<String, JaxrsRequestAuthenticatorExecutor> executorIndex = Types.cast(requestContext.getProperty("executorIndex"));
        if (executorIndex.isEmpty()) {
            if (!state.getAuthenticatorNames().isEmpty()) {
                throw new IllegalArgumentException("Invalid amount of authenticator names found!");
            }
            executor = ((JaxrsRequestAuthenticatorExecutor) requestContext.getProperty("executorRoot"));
        } else {
            String currentAuthenticatorName = state.getLastAuthenticatorName();
            if (currentAuthenticatorName == null) {
                executor = ((JaxrsRequestAuthenticatorExecutor) requestContext.getProperty("executorRoot"));
            } else {
                executor = executorIndex.get(currentAuthenticatorName);
            }
            if (executor == null) {
                throw new IllegalArgumentException("Invalid authenticator name in authentication handshake!");
            }
        }
        LOGGER.debug("Current executor: " + executor.getAuthenticatorName());
        return executor;
    }

    private static AuthenticationPolicy determinePolicy(Authentication authentication, JaxrsZone zone, AuthenticationPolicy defaultPolicy) {
        return authentication != null
                ? authentication.value()
                : zone != null
                ? zone.getAuthenticationPolicy()
                : defaultPolicy;
    }

    private static String determineAuthenticatorName(Authentication authentication, JaxrsZone zone) {
        return authentication != null && !authentication.authenticator().isEmpty()
                ? authentication.authenticator()
                : zone != null && zone.getAuthenticatorName() != null && !zone.getAuthenticatorName().isEmpty()
                ? zone.getAuthenticatorName()
                : null;
    }

    private SecurityContext handleAuthentication(ContainerRequestContext requestContext, AuthenticationPolicy policy) {
        try {
            if (policy == AuthenticationPolicy.NONE) {
                LOGGER.debug("Policy is NONE; skipping authentication");
                return null;
            }
            if (policy != AuthenticationPolicy.REQUIRED && policy != AuthenticationPolicy.OPTIONAL) {
                LOGGER.error("Unknown policy: " + policy);
                throw new IllegalStateException();
            }
            Response response = getCurrentExecutor(requestContext).authenticate(requestContext);
            if (response != null) {
                setTrue(requestContext, "performHandshake");
                setTrue(requestContext, "aborted");
                requestContext.abortWith(response);
                return null;
            } else {
                SecurityContext securityContext = SecurityContext.get(requestContext);
                boolean authenticated = securityContext.isAuthenticated();
                LOGGER.debug("Handshake completed");
                LOGGER.debug("Policy is " + policy + ", we are " + (!authenticated ? "NOT " : "") + "authenticated");
                if (policy == AuthenticationPolicy.REQUIRED && !authenticated) {
                    LOGGER.warn("Policy is REQUIRED, we are NOT authenticated; aborting request");
                    setTrue(requestContext, "aborted");
                    requestContext.abortWith(UNAUTHORIZED);
                }
                return securityContext;
            }
        } catch (IllegalStateException | IllegalArgumentException | SecurityException cause) {
            setTrue(requestContext, "aborted");
            requestContext.abortWith(SERVER_ERROR);
            return null;
        }
    }

    private void postHandshakeCall(ContainerRequestContext requestContext, ContainerResponseContext responseContext, ResourceInfo resourceInfo) {
        try {
            if (isTrue(requestContext, "aborted")) {
                LOGGER.debug("Aborting request with " + responseContext.getStatus() + " for: " + requestContext.getUriInfo().getRequestUri());
                return;
            }
            if (Methods.hasAnnotation(resourceInfo.getResourceMethod(), Authenticate.class)) {
                setTrue(requestContext, "@Authenticate");
                Object entity = responseContext.getEntity();
                Response response;
                if (entity == null) {
                    response = getCurrentExecutor(requestContext).getOnFailure().authenticate(requestContext);
                } else if (entity instanceof io.codeleaf.sec.Authentication) {
                    List<String> authenticatorNames = getHandshakeState(requestContext).getAuthenticatorNames();
                    authenticatorNames.remove(authenticatorNames.size() - 1);
                    io.codeleaf.sec.Authentication authentication = (io.codeleaf.sec.Authentication) entity;
                    response = getCurrentExecutor(requestContext).onFailureCompleted(requestContext, authentication);
                } else {
                    LOGGER.error("Invalid return type from @Authenticate resource: " + entity.getClass());
                    response = Response.serverError().build();
                }
                if (response == null) {
                    JaxrsHandshakeState state = getHandshakeState(requestContext);
                    if (!handshakeStateHandler.isHandshakePath(state.getUri())) {
                        LOGGER.debug("Sending redirect to service...");
                        response = Response.seeOther(state.getUri()).build();
                    } else {
                        LOGGER.debug("Sending no content...");
                        response = Response.noContent().build();
                    }
                } else {
                    LOGGER.debug("Sending authenticator response...");
                }
                replaceResponse(response, responseContext);
            } else {
                LOGGER.debug("Sending authenticator response...");
            }
        } catch (SecurityException cause) {
            replaceResponse(SERVER_ERROR, responseContext);
        }
    }

    private void postServiceCall(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (isTrue(requestContext, "aborted")) {
            if (isTrue(requestContext, "performHandshake")) {
                LOGGER.debug("Sending handshake response for: " + requestContext.getUriInfo().getRequestUri());
                if (responseContext.getStatusInfo().getFamily() == Response.Status.Family.REDIRECTION) {
                    LOGGER.debug("Redirecting to: " + responseContext.getHeaderString("Location"));
                }
            } else {
                LOGGER.debug("Aborting request with " + responseContext.getStatus() + " for: " + requestContext.getUriInfo().getRequestUri());
            }
        } else {
            getRootExecutor(requestContext).onServiceCompleted(requestContext, responseContext);
            LOGGER.debug("Processing finished for: " + requestContext.getUriInfo().getRequestUri());
        }
    }

    private void replaceResponse(Response response, ContainerResponseContext responseContext) {
        responseContext.setStatus(response.getStatus());
        responseContext.getHeaders().clear();
        for (Map.Entry<String, List<Object>> header : response.getHeaders().entrySet()) {
            for (Object value : header.getValue()) {
                responseContext.getHeaders().add(header.getKey(), value);
            }
        }
        responseContext.setEntity(response.getEntity());
    }

    public static void setTrue(ContainerRequestContext requestContext, String propertyName) {
        requestContext.setProperty(propertyName, Boolean.TRUE);
    }

    public static boolean isTrue(ContainerRequestContext requestContext, String propertyName) {
        return Boolean.TRUE.equals(requestContext.getProperty(propertyName));
    }

    public static void setHandshakeState(ContainerRequestContext requestContext, JaxrsHandshakeState state) {
        state = state == null ? new JaxrsHandshakeState(requestContext.getUriInfo().getRequestUri()) : state;
        JaxrsHandshakeSessionManager.get().setState(state);
        requestContext.setProperty("handshakeState", state);
        LOGGER.debug("Handshake state set: " + state.getUri() + " " + state.getAuthenticatorNames());
    }

    public static JaxrsHandshakeState getHandshakeState(ContainerRequestContext requestContext) {
        JaxrsHandshakeState state = (JaxrsHandshakeState) requestContext.getProperty("handshakeState");
        if (state == null) {
            throw new IllegalStateException("No handshake state set!");
        }
        return state;
    }
}
