package io.codeleaf.sec.profile;

import java.util.Set;

public interface SecurityZone {

    String getName();

    String getAuthenticatorName();

    Set<String> getAuthorizationLoaders();

    AuthenticationPolicy getAuthenticationPolicy();

}
