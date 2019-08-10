package io.codeleaf.sec.authz;

import io.codeleaf.sec.Authorization;

import java.util.Set;

public interface RolesAuthorization extends Authorization {

    Set<String> getRoles();

}
