package io.codeleaf.sec.impl;

import io.codeleaf.sec.Authentication;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class DefaultAuthentication implements Authentication {

    private final Principal principal;
    private final Map<String, Object> attributes;
    private final boolean isSecure;

    public DefaultAuthentication(Principal principal, Map<String, Object> attributes, boolean isSecure) {
        this.principal = principal;
        this.attributes = attributes;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    public static DefaultAuthentication create(String identity) {
        Objects.requireNonNull(identity);
        return new DefaultAuthentication(() -> identity, Collections.emptyMap(), false);
    }
}
