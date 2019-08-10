package io.codeleaf.sec.authz;

import io.codeleaf.sec.Permissions;

import java.util.Set;

public interface RolesPermissions extends Permissions {

    Set<String> getAllowedRoles();

    default boolean allowsAllRoles() {
        return false;
    }

}
