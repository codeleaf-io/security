package io.codeleaf.sec.profile;

import io.codeleaf.sec.spi.Authenticator;

public class AuthenticatorNode {

    private final String name;
    private final Authenticator authenticator;
    private final String onFailure;

    public AuthenticatorNode(String name, Authenticator authenticator, String onFailure) {
        this.name = name;
        this.authenticator = authenticator;
        this.onFailure = onFailure;
    }

    public String getName() {
        return name;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public String getOnFailure() {
        return onFailure;
    }

}
