package io.codeleaf.sec.password.ldap;

import io.codeleaf.sec.password.spi.Credentials;

import javax.naming.Context;
import java.util.Hashtable;

public final class LdapEnvironment extends Hashtable<String, String> {

    private final String distinguishedName;

    private LdapEnvironment(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public static final class Factory {

        private final LdapConfiguration configuration;

        public Factory(LdapConfiguration configuration) {
            this.configuration = configuration;
        }

        public LdapEnvironment create(Credentials credentials) {
            String distinguishedName = getUserDn(credentials.getUserName());
            LdapEnvironment environment = new LdapEnvironment(distinguishedName);
            environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            environment.put(Context.SECURITY_AUTHENTICATION, "simple");
            environment.put(Context.PROVIDER_URL, configuration.getLdapUrl());
            environment.put(Context.SECURITY_PRINCIPAL, distinguishedName);
            environment.put(Context.SECURITY_CREDENTIALS, credentials.getPassword());
            return environment;
        }

        private String getUserDn(String userId) {
            return "uid=" + userId + "," + configuration.getBase();
        }

    }

}
