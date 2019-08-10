package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;
import io.codeleaf.sec.profile.SecurityProfile;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("authn")
public final class AuthenticatorResources {

    private final Map<String, Object> resources;

    public AuthenticatorResources(Map<String, Object> resources) {
        this.resources = resources;
    }

    @Path("{authenticatorName}")
    public Object handleRequest(@PathParam("authenticatorName") String authenticatorName) {
        return resources.get(authenticatorName);
    }

    public static AuthenticatorResources create(SecurityProfile securityProfile) {
        return new AuthenticatorResources(getResources(securityProfile));
    }

    private static Map<String, Object> getResources(SecurityProfile securityProfile) {
        Map<String, Object> resources = new LinkedHashMap<>();
        for (String name : securityProfile.getRegistry().getNames(JaxrsRequestAuthenticator.class)) {
            JaxrsRequestAuthenticator authenticator = securityProfile.getRegistry().lookup(name, JaxrsRequestAuthenticator.class);
            Object resource = authenticator.getResource();
            if (resource != null) {
                resources.put(name, resource);
            }
        }
        return resources;
    }
}
