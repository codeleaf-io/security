package io.codeleaf.sec.profile;

import io.codeleaf.config.Configuration;

import java.util.Set;

public interface SecurityZone extends Configuration {

    String getName();

    String getAuthenticatorName();

    Set<String> getAuthorizationLoaders();

    AuthenticationPolicy getAuthenticationPolicy();

}
