package io.codeleaf.sec.authorizers.roles;

import io.codeleaf.sec.Authorization;
import io.codeleaf.sec.spi.Authorizer;

import java.util.Set;

public final class RolesAuthorizer implements Authorizer<RolesPermissions> {

    @Override
    public Class<RolesPermissions> getPermissionsType() {
        return RolesPermissions.class;
    }

    @Override
    public boolean isAuthorized(RolesPermissions permissions, Set<Authorization> authorizations) {
        if (permissions.allowsAllRoles()) {
            return true;
        }
        for (Authorization authorization : authorizations) {
            if (authorization instanceof RolesAuthorization) {
                for (String role : ((RolesAuthorization) authorization).getRoles()) {
                    if (permissions.getAllowedRoles().contains(role)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
