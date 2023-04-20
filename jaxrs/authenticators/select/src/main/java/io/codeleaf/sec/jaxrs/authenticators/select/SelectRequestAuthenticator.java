package io.codeleaf.sec.jaxrs.authenticators.select;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public final class SelectRequestAuthenticator implements JaxrsRequestAuthenticator {

    private final SelectRequestConfiguration configuration;
    private final Registry registry;
    private static final ThreadLocal<JaxrsRequestAuthenticator> authenticators = new ThreadLocal<>();

    public SelectRequestAuthenticator(SelectRequestConfiguration configuration, Registry registry) {
        this.configuration = configuration;
        this.registry = registry;
    }

    @Override
    public String getAuthenticationScheme() {
        throw new IllegalStateException("This should not be called!");
    }

    @Override
    public Authentication authenticate(ContainerRequestContext requestContext) throws SecurityException {
        String authenticatorName = requestContext.getUriInfo().getQueryParameters().getFirst(configuration.getParameterName());
        if (!registry.contains(authenticatorName, JaxrsRequestAuthenticator.class)) {
            return null;
        }
        JaxrsRequestAuthenticator authenticator = registry.lookup(authenticatorName, JaxrsRequestAuthenticator.class);
        authenticators.set(authenticator);
        return authenticator.authenticate(requestContext);
    }

    @Override
    public Response.ResponseBuilder onNotAuthenticated(ContainerRequestContext requestContext) {
        JaxrsRequestAuthenticator authenticator = authenticators.get();
        if (authenticator != null) {
            try {
                return authenticator.onNotAuthenticated(requestContext);
            } finally {
                authenticators.remove();
            }
        } else {
            return configuration.isCustom()
                    ? Response.temporaryRedirect(configuration.getCustomPageUrl())
                    : createDefaultResponse(requestContext);
        }
    }

    private Response.ResponseBuilder createDefaultResponse(ContainerRequestContext requestContext) {
        String html = "<html>\n" +
                "    <head>\n" +
                "        <title>Select login page</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Please select the method to login</h1>\n";
        for (SelectRequestConfiguration.Option option : configuration.getOptions().values()) {
            html += getHtmlOption(option, requestContext);
        }
        html += "    </body>\n" +
                "</html>\n";
        return Response.status(Response.Status.OK).entity(html).type(MediaType.TEXT_HTML_TYPE);
    }

    private String getHtmlOption(SelectRequestConfiguration.Option option, ContainerRequestContext requestContext) {
        if (!option.isEnabled()) {
            return null;
        }
        URI uri = requestContext.getUriInfo().getRequestUriBuilder().queryParam(configuration.getParameterName(), option.getAuthenticatorName()).build();
        String html = "        <div><a href=\"#" + uri.toString() + "\">";
        if (option.getIconUrl() != null) {
            html += "<img src=\"" + option.getIconUrl() + "\"/>";
        }
        html += option.getLabel() + "</a></div>\n";
        return html;
    }
}
