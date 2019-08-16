package io.codeleaf.sec.authorizers.roles;

import io.codeleaf.sec.Permissions;

import java.util.Set;

public interface RolesPermissions extends Permissions {

    Set<String> getAllowedRoles();

    default boolean allowsAllRoles() {
        return false;
    }

}
