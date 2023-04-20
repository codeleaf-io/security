package io.codeleaf.sec.jaxrs.protocols.cookie;

import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public final class CookieSessionIdProtocol implements JaxrsSessionIdProtocol {

    private final CookieSessionIdConfiguration configuration;

    public CookieSessionIdProtocol(CookieSessionIdConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setSessionId(ContainerRequestContext requestContext, Response.ResponseBuilder response, String sessionId) {
        response.cookie(new NewCookie(configuration.getName(), sessionId, configuration.getPath(), configuration.getDomain(),
                configuration.getComment(), configuration.getMaxAge(), configuration.isSecure(), configuration.isHttpOnly()));
    }

    @Override
    public void clearSessionId(ContainerRequestContext requestContext, Response.ResponseBuilder response, String sessionId) {
        response.cookie(new NewCookie(configuration.getName(), "", configuration.getPath(), configuration.getDomain(),
                configuration.getComment(), 0, configuration.isSecure(), configuration.isHttpOnly()));
    }

    @Override
    public String getSessionId(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(configuration.getName());
        return cookie == null ? null : cookie.getValue();
    }
}
