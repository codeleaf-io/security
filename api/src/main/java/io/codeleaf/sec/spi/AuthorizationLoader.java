package io.codeleaf.sec.spi;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.Authorization;

import java.util.Set;

public interface AuthorizationLoader {

    Set<Authorization> loadAuthorizations(Authentication authentication);

}
