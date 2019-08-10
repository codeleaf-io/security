package io.codeleaf.sec.spi;

import io.codeleaf.sec.Authorization;
import io.codeleaf.sec.Permissions;

import java.util.Set;

public interface Authorizer {

    boolean isAuthorized(Permissions permissions, Set<Authorization> authorizations);

}
