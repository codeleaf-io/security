package io.codeleaf.sec.jaxrs.authenticators.ldap;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;

public final class LdapConfigurationFactory extends RegistryAwareConfigurationFactory<LdapConfiguration> {

    public LdapConfigurationFactory() {
        super(LdapConfiguration.class);
    }

    @Override
    public LdapConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        try {
            return new LdapConfiguration(
                    getAuthenticator(specification, registry),
                    getHost(specification),
                    getPort(specification),
                    getDnsDomainNameComponent(specification),
                    getOrganizationalUnit(specification),
                    getOrganizationName(specification)
            );
        } catch (IllegalArgumentException cause) {
            throw new InvalidSpecificationException(specification, "Can't parse specification: " + cause.getMessage(), cause);
        }
    }

    private int getPort(Specification specification) throws SettingNotFoundException, InvalidSettingException {
        return specification.hasSetting("port") ? Specifications.parseInt(specification, "port") : -1;

    }

    private LdapAuthenticator getAuthenticator(Specification specification, Registry registry) throws InvalidSpecificationException {
        String authenticatorName = Specifications.parseString(specification, "LdapAuthenticator");
        LdapAuthenticator authenticator = registry.lookup(authenticatorName, LdapAuthenticator.class);
        if (authenticator == null) {
            throw new InvalidSpecificationException(specification, "No authenticator found with name: " + authenticatorName);
        }
        return authenticator;
    }

    private boolean getPrompt(Specification specification) throws SettingNotFoundException, InvalidSettingException {
        return !specification.hasSetting("prompt") || Specifications.parseBoolean(specification, "prompt");
    }

    public String getHost(Specification specification) throws InvalidSpecificationException {
        return specification.hasSetting("host") ? Specifications.parseString(specification, "host") : "localhost";
    }

    public String getDnsDomainNameComponent(Specification specification) throws InvalidSpecificationException {
        return specification.hasSetting("dnsDomainNameComponent") ? Specifications.parseString(specification, "dnsDomainNameComponent") : null;
    }

    public String getOrganizationalUnit(Specification specification) throws InvalidSpecificationException {
        return specification.hasSetting("organizationalUnit") ? Specifications.parseString(specification, "organizationalUnit") : null;
    }

    public String getOrganizationName(Specification specification) throws InvalidSpecificationException {
        return specification.hasSetting("organizationalName") ? Specifications.parseString(specification, "organizationalName") : null;
    }
}
