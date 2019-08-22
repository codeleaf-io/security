package io.codeleaf.sec.jaxrs.authenticators.ldap;

import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.impl.DefaultAuthentication;
import io.codeleaf.sec.jaxrs.spi.JaxrsRequestAuthenticator;
import io.codeleaf.sec.password.spi.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import java.util.Hashtable;

public final class LdapAuthenticator implements JaxrsRequestAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapAuthenticator.class);

    private final LdapConfiguration configuration;

    private final String commonName;
    private final String userId;
    private final String password;

    public LdapAuthenticator(LdapConfiguration configuration, String commonName, String userId, String password, String countryName, String localityName, String stateOrProvinceName) {
        this.configuration = configuration;
        this.commonName = commonName;
        this.userId = userId;
        this.password = password;
    }

    @Override
    public String getAuthenticationScheme() {
        return "LDAP";
    }

    @Override
    public Authentication authenticate(ContainerRequestContext requestContext) throws SecurityException {
        Credentials credentials = extractHeaderCredentials(requestContext);
        LOGGER.debug("Found credentials: " + (credentials != null));
        return credentials != null ? ldapAuthenticate(credentials) : null;
    }

    private Credentials extractHeaderCredentials(ContainerRequestContext requestContext) {
        return null;
    }

    private String getLdapUrl() {
        return "ldap://" + configuration.getHost() + ":" + configuration.getPort();
    }

    private String getBase() {
        return new StringBuilder()
                .append("ou=").append(configuration.getOrganizationalUnit())
                .append("dc=").append(configuration.getDnsDomainNameComponent())
                .toString();
    }

    private String getDn(String userId) {
        return new StringBuilder()
                .append("uid=").append(userId)
                .append(",").append(getBase())
                .toString();
    }

    private Authentication ldapAuthenticate(Credentials credentials) {

        // Setup environment for authenticating
        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, getLdapUrl());
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, getDn(credentials.getUserName()));
        environment.put(Context.SECURITY_CREDENTIALS, credentials.getPassword());

        try {
            DirContext authContext = new InitialDirContext(environment);
            return new DefaultAuthentication(() -> credentials.getUserName(), Collections.emptyMap(), false);
        } catch (AuthenticationException ex) {
            System.out.println("Invalid authentication." + ex.getMessage());
            ex.printStackTrace();
        } catch (NamingException ex) {
            System.out.println("Invalid Naming." + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}


