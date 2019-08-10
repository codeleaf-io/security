package io.codeleaf.sec.jaxrs.spi;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

public interface JaxrsSessionIdProtocol {

    void setSessionId(ContainerRequestContext requestContext, Response.ResponseBuilder response, String sessionId);

    default void clearSessionId(ContainerRequestContext requestContext, Response.ResponseBuilder response) {
        clearSessionId(requestContext, response, getSessionId(requestContext));
    }

    void clearSessionId(ContainerRequestContext requestContext, Response.ResponseBuilder response, String sessionId);

    String getSessionId(ContainerRequestContext requestContext);
}
