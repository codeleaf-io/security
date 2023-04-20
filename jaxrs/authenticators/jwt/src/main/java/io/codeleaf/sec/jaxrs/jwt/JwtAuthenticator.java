package io.codeleaf.sec.jaxrs.jwt;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.jaxrs.spi.JaxrsHandshakeState;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.spi.SessionDataStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.Objects;

public final class JwtAuthenticator implements JaxrsRequestAuthenticator {

    private final JaxrsSessionIdProtocol protocol;
    private final SessionDataStore store;
    private final JwtAuthenticationSerializer serializer;

    private JwtAuthenticator(JaxrsSessionIdProtocol protocol, SessionDataStore store, JwtAuthenticationSerializer serializer) {
        this.protocol = protocol;
        this.store = store;
        this.serializer = serializer;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }

    @Override
    public Authentication authenticate(ContainerRequestContext requestContext) throws SecurityException {
        Authentication authentication;
        String sessionId = protocol.getSessionId(requestContext);
        if (sessionId != null) {
            String jwt = store.retrieveSessionData(sessionId);
            authentication = jwt != null ? serializer.deserialize(jwt) : null;
        } else {
            authentication = null;
        }
        return authentication;
    }

    @Override
    public Response.ResponseBuilder onFailureCompleted(ContainerRequestContext requestContext, Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String jwt = serializer.serialize(authentication);
        String sessionId = store.storeSessionData(jwt);
        JaxrsHandshakeState state = (JaxrsHandshakeState) requestContext.getProperty("handshakeState");
        Response.ResponseBuilder responseBuilder = Response.seeOther(state.getUri());
        protocol.setSessionId(requestContext, responseBuilder, sessionId);
        return responseBuilder;
    }

    public static JwtAuthenticator create(JwtConfiguration configuration) {
        Objects.requireNonNull(configuration);
        return new JwtAuthenticator(configuration.getProtocol(), configuration.getStore(), configuration.getSerializer());
    }
}