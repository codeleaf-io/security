package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.impl.DefaultSecurityContext;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;
import io.codeleaf.sec.spi.SecurityContextManager;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.LinkedHashSet;

public final class RootRequestAuthenticatorExecutor extends JaxrsRequestAuthenticatorExecutor {

    private final SecurityContextManager securityContextManager;

    public RootRequestAuthenticatorExecutor(SecurityContextManager securityContextManager, JaxrsHandshakeStateHandler handshakeStateHandler) {
        super("root", new RootAuthenticator(), handshakeStateHandler, null);
        this.securityContextManager = securityContextManager;
    }

    public Response authenticate(ContainerRequestContext requestContext) throws SecurityException {
        return getOnFailure() == null ? null : getOnFailure().authenticate(requestContext);
    }

    public Response onFailureCompleted(ContainerRequestContext requestContext, Authentication authentication) {
        Response response = getHandshakeStateHandler().clearHandshakeState(requestContext);
        if (response == null) {
            securityContextManager.setSecurityContext(requestContext, new DefaultSecurityContext(authentication, new LinkedHashSet<>()));
            if (!JaxrsZoneHandler.isTrue(requestContext, "@Authenticate")) {
                requestContext.setSecurityContext(createSecurityContext(authentication, getOnFailure().getAuthenticator()));
            }
        }
        return response;
    }

    @Override
    public void onServiceCompleted(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        if (getOnFailure() != null) {
            getOnFailure().onServiceCompleted(containerRequestContext, containerResponseContext);
        }
        securityContextManager.clearSecurityContext(containerRequestContext);
    }

    @Override
    public JaxrsRequestAuthenticatorExecutor getParentExecutor() {
        return this;
    }

    private SecurityContext createSecurityContext(Authentication authentication, JaxrsRequestAuthenticator authenticator) {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return authentication.getPrincipal();
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return authentication.isSecure();
            }

            // TODO: must be a static member of SecurityContext.BASIC_AUTH ... etc.
            @Override
            public String getAuthenticationScheme() {
                return authenticator.getAuthenticationScheme();
            }
        };
    }

    public static final class RootAuthenticator implements JaxrsRequestAuthenticator {

        @Override
        public String getAuthenticationScheme() {
            return null;
        }
    }
}