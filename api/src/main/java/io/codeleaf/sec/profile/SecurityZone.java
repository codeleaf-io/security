package io.codeleaf.sec.profile;

import io.codeleaf.config.Configuration;
import io.codeleaf.sec.Permissions;

import java.util.Set;

public interface SecurityZone extends Configuration {

    String getName();

    AuthenticationPolicy getAuthenticationPolicy();

    String getAuthenticatorName();

    Set<String> getAuthorizationLoaderNames();

    String getAuthorizerName();

    Set<Permissions> getPermissions();

}
