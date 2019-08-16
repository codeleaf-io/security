package io.codeleaf.sec.impl;

import io.codeleaf.sec.Permissions;
import io.codeleaf.sec.profile.AuthenticationPolicy;
import io.codeleaf.sec.profile.SecurityZone;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultSecurityZone implements SecurityZone {

    private final String name;
    private final AuthenticationPolicy authenticationPolicy;
    private final String authenticatorName;
    private final Set<String> authorizationLoaderNames;
    private final String authorizerName;
    private final Set<Permissions> permissions;

    public DefaultSecurityZone(String name, AuthenticationPolicy authenticationPolicy, String authenticatorName, Set<String> authorizationLoaderNames, String authorizerName, Set<Permissions> permissions) {
        this.name = name;
        this.authenticationPolicy = authenticationPolicy;
        this.authenticatorName = authenticatorName;
        this.authorizationLoaderNames = authorizationLoaderNames;
        this.authorizerName = authorizerName;
        this.permissions = permissions;
    }

    protected DefaultSecurityZone(SecurityZone securityZone) {
        this.name = securityZone.getName();
        this.authenticationPolicy = securityZone.getAuthenticationPolicy();
        this.authenticatorName = securityZone.getAuthenticatorName();
        this.authorizationLoaderNames = Collections.unmodifiableSet(new LinkedHashSet<>(securityZone.getAuthorizationLoaderNames()));
        this.authorizerName = securityZone.getAuthorizerName();
        this.permissions = Collections.unmodifiableSet(new LinkedHashSet<>(securityZone.getPermissions()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AuthenticationPolicy getAuthenticationPolicy() {
        return authenticationPolicy;
    }

    @Override
    public String getAuthenticatorName() {
        return authenticatorName;
    }

    @Override
    public Set<String> getAuthorizationLoaderNames() {
        return authorizationLoaderNames;
    }

    @Override
    public String getAuthorizerName() {
        return authorizerName;
    }

    @Override
    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
