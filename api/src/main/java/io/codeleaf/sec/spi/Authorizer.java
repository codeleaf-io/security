package io.codeleaf.sec.spi;

import io.codeleaf.sec.Authorization;
import io.codeleaf.sec.Permissions;

import java.util.Set;

public interface Authorizer<P extends Permissions> {

    Class<P> getPermissionsType();

    boolean isAuthorized(P permissions, Set<Authorization> authorizations);

}
