package io.codeleaf.sec.jaxrs.config;

import io.codeleaf.sec.impl.DefaultSecurityZone;
import io.codeleaf.sec.profile.SecurityZone;

import java.util.List;

public final class JaxrsZone extends DefaultSecurityZone {

    private final List<String> endpoints;

    public JaxrsZone(SecurityZone securityZone, List<String> endpoints) {
        super(securityZone);
        this.endpoints = endpoints;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

}
