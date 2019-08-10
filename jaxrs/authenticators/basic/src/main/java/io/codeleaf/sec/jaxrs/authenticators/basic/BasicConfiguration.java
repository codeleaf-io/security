package io.codeleaf.sec.jaxrs.authenticators.basic;

import io.codeleaf.config.Configuration;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;

public final class BasicConfiguration implements Configuration {

    private final PasswordRequestAuthenticator authenticator;
    private final String realm;
    private final boolean prompt;

    BasicConfiguration(PasswordRequestAuthenticator authenticator, String realm, boolean prompt) {
        this.authenticator = authenticator;
        this.realm = realm;
        this.prompt = prompt;
    }

    public PasswordRequestAuthenticator getAuthenticator() {
        return authenticator;
    }

    public String getRealm() {
        return realm;
    }

    public boolean prompt() {
        return prompt;
    }
}
