package io.codeleaf.sec.password.spi;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.spi.Authenticator;

public interface PasswordRequestAuthenticator extends Authenticator {

    default Authentication authenticate(String userName, String password) throws SecurityException {
        if (userName == null || password == null || userName.isEmpty()) {
            return null;
        }
        return authenticate(Credentials.create(userName, password));
    }

    Authentication authenticate(Credentials credentials) throws SecurityException;
}
