package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.jaxrs.spi.JaxrsHandshakeState;
import io.codeleaf.sec.profile.SecurityProfile;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public final class JaxrsHandshakeStateHandler {

    private final JaxrsHandshakeConfiguration configuration;

    public JaxrsHandshakeStateHandler(JaxrsHandshakeConfiguration configuration) {
        this.configuration = configuration;
    }

    public JaxrsHandshakeConfiguration getConfiguration() {
        return configuration;
    }

    public String getPath() {
        return configuration.getPath();
    }

    public void setHandshakeState(ContainerRequestContext containerRequestContext, Response.ResponseBuilder responseBuilder, JaxrsHandshakeState handshakeState) {
        Objects.requireNonNull(handshakeState);
        String sessionData = handshakeState.encode();
        String sessionId = configuration.getStore().storeSessionData(sessionData);
        configuration.getProtocol().setSessionId(containerRequestContext, responseBuilder, sessionId);
    }

    public JaxrsHandshakeState extractHandshakeState(ContainerRequestContext containerRequestContext) {
        String sessionId = configuration.getProtocol().getSessionId(containerRequestContext);
        if (sessionId != null) {
            String sessionData = configuration.getStore().retrieveSessionData(sessionId);
            if (sessionData != null) {
                return JaxrsHandshakeState.decode(sessionData);
            }
        }
        return null;
    }

    public Response clearHandshakeState(ContainerRequestContext requestContext) {
        Response response;
        JaxrsHandshakeState handshakeState = extractHandshakeState(requestContext);
        if (handshakeState != null) {
            Response.ResponseBuilder builder = Response.temporaryRedirect(requestContext.getUriInfo().getRequestUri());
            configuration.getProtocol().clearSessionId(requestContext, builder);
            response = builder.build();
        } else {
            response = null;
        }
        return response;
    }

    public boolean isHandshakePath(URI uri) {
        if (uri == null) {
            return false;
        }
        String path = uri.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] segments = path.split("/");
        return segments.length > 0 && Objects.equals(segments[0], getPath().replace("/", ""));
    }

    public boolean isHandshakePath(UriInfo uriInfo) {
        List<PathSegment> segments = uriInfo.getPathSegments();
        return segments.size() > 0 && segments.get(0).getPath().equals(getPath().replace("/", ""));
    }

    public String getHandshakeAuthenticatorName(UriInfo uriInfo) {
        List<PathSegment> segments = uriInfo.getPathSegments();
        if (segments.size() < 1) {
            throw new IllegalArgumentException();
        }
        return segments.get(1).getPath();
    }

    public static JaxrsHandshakeStateHandler create(SecurityProfile securityProfile) {
        return new JaxrsHandshakeStateHandler(securityProfile.getProtocolConfiguration(JaxrsConfiguration.class).getHandshakeConfiguration());
    }
}
