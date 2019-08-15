package io.codeleaf.sec.jaxrs.config;

import io.codeleaf.sec.profile.AuthenticationPolicy;
import io.codeleaf.sec.profile.SecurityZone;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class JaxrsZone implements SecurityZone {

    private final String name;
    private final AuthenticationPolicy policy;
    private final List<String> endpoints;
    private final String authenticatorName;

    public JaxrsZone(String name, AuthenticationPolicy policy, List<String> endpoints, String authenticatorName) {
        this.name = name;
        this.policy = policy;
        this.endpoints = endpoints;
        this.authenticatorName = authenticatorName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AuthenticationPolicy getAuthenticationPolicy() {
        return policy;
    }

    @Override
    public String getAuthenticatorName() {
        return authenticatorName;
    }

    @Override
    public Set<String> getAuthorizationLoaders() {
        return Collections.emptySet();
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

}
