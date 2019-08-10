package io.codeleaf.sec.impl;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.Authorization;
import io.codeleaf.sec.SecurityContext;

import java.util.Set;

public class DefaultSecurityContext implements SecurityContext {

    private final Authentication authentication;
    private final Set<Authorization> authorizations;

    public DefaultSecurityContext(Authentication authentication, Set<Authorization> authorizations) {
        this.authentication = authentication;
        this.authorizations = authorizations;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public Set<Authorization> getAuthorizations() {
        return authorizations;
    }

}
