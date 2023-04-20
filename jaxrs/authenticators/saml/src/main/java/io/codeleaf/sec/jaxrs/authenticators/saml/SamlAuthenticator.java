package io.codeleaf.sec.jaxrs.authenticators.saml;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;

import javax.ws.rs.container.ContainerRequestContext;

public class SamlAuthenticator implements JaxrsRequestAuthenticator {

    private final SamlConfiguration configuration;
    private final SamlResource resource;

    public SamlAuthenticator(SamlConfiguration configuration, SamlResource resource) {
        this.configuration = configuration;
        this.resource = resource;
    }

    @Override
    public String getAuthenticationScheme() {
        return "SAML";
    }

    @Override
    public Authentication authenticate(ContainerRequestContext requestContext) throws SecurityException {
        return null;
    }

    @Override
    public Object getResource() {
        return resource;
    }

    public static SamlAuthenticator create(SamlConfiguration configuration) {
        return new SamlAuthenticator(configuration, new SamlResource(configuration));
    }

}
