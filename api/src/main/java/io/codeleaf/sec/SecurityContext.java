package io.codeleaf.sec;

import io.codeleaf.sec.profile.SecurityProfile;

import java.util.Set;

public interface SecurityContext {

    Authentication getAuthentication();

    Set<Authorization> getAuthorizations();

    default boolean isAuthenticated() {
        return getAuthentication() != null;
    }

    static SecurityContext get(Object context) {
        return SecurityProfile.get().getSecurityContextProvider().getSecurityContext(context);
    }

}