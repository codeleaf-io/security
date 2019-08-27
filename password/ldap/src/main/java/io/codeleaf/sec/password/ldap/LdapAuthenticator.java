package io.codeleaf.sec.password.ldap;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.impl.DefaultAuthentication;
import io.codeleaf.sec.password.spi.Credentials;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Collections;
import java.util.Hashtable;

public final class LdapAuthenticator implements PasswordRequestAuthenticator {

    private static final Hashtable<String, String> ENVIRONMENT = new Hashtable<>();

    static {
        ENVIRONMENT.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ENVIRONMENT.put(Context.SECURITY_AUTHENTICATION, "simple");
    }

    private final LdapConfiguration configuration;

    public LdapAuthenticator(LdapConfiguration configuration) {
        this.configuration = configuration;
    }

    private String getUserDn(String userId) {
        return "uid=" + userId + "," + configuration.getBase();
    }

    @Override
    public Authentication authenticate(Credentials credentials) throws SecurityException {
        try {
            Attributes attributes = getAttributes(credentials);
            return new DefaultAuthentication(credentials::getUserName, Collections.singletonMap(LdapAuthenticator.class.getName(), attributes), true);
        } catch (AuthenticationException cause) {
            return null;
        } catch (NamingException cause) {
            throw new SecurityException("Invalid Naming: " + cause.getMessage(), cause);
        }
    }

    @SuppressWarnings("unchecked")
    private Attributes getAttributes(Credentials credentials) throws NamingException {
        String userDn = getUserDn(credentials.getUserName());
        Hashtable<String, String> environment = (Hashtable<String, String>) ENVIRONMENT.clone();
        environment.put(Context.PROVIDER_URL, configuration.getLdapUrl());
        environment.put(Context.SECURITY_PRINCIPAL, userDn);
        environment.put(Context.SECURITY_CREDENTIALS, credentials.getPassword());
        return new InitialDirContext(environment).getAttributes(userDn);
    }

}


