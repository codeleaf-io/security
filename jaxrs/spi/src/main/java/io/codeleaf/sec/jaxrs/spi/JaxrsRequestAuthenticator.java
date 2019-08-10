package io.codeleaf.sec.jaxrs.spi;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.spi.Authenticator;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import java.io.IOException;

public interface JaxrsRequestAuthenticator extends Authenticator {

    String getAuthenticationScheme();

    default Authentication authenticate(ContainerRequestContext requestContext) throws SecurityException {
        return null;
    }

    default JaxrsHandshakeState setHandshakeState(ContainerRequestContext requestContext, ResourceInfo resourceInfo, JaxrsHandshakeState extractedState) throws SecurityException, IOException {
        return extractedState;
    }

    /**
     * Returns <code>null</code> when we want to continue to the next configured authenticator.
     * Returns a Response when would like to abort, and send the Response as defined using
     * {@link ContainerRequestContext#abortWith(Response)} to the client.
     *
     * @param requestContext
     * @return
     */
    default Response.ResponseBuilder onNotAuthenticated(ContainerRequestContext requestContext) {
        return null;
    }

    default Response.ResponseBuilder onFailureCompleted(ContainerRequestContext requestContext, Authentication authentication) {
        return null;
    }

    default void onServiceCompleted(ContainerRequestContext requestContext, ContainerResponseContext responseContext, Authentication authentication) {
    }

    default Object getResource() {
        return null;
    }
}