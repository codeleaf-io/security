package io.codeleaf.sec.password.ldap;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.impl.DefaultAuthentication;
import io.codeleaf.sec.password.spi.Credentials;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Collections;

public final class LdapAuthenticator implements PasswordRequestAuthenticator {

    private final LdapConfiguration configuration;

    public LdapAuthenticator(LdapConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Authentication authenticate(Credentials credentials) throws SecurityException {
        try {
            LdapEnvironment environment = configuration.getEnvironmentFactory().create(credentials);
            Attributes attributes = new InitialDirContext(environment).getAttributes(environment.getDistinguishedName());
            return new DefaultAuthentication(credentials::getUserName, Collections.singletonMap(LdapAuthenticator.class.getName(), attributes), true);
        } catch (AuthenticationException cause) {
            return null;
        } catch (NamingException cause) {
            throw new SecurityException("Invalid Naming: " + cause.getMessage(), cause);
        }
    }

}


