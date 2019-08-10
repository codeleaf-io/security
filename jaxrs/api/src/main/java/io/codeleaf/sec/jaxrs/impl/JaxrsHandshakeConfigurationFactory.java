package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.config.ConfigurationException;
import io.codeleaf.config.ConfigurationNotFoundException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.config.spec.*;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.SecurityProfileAwareConfigurationFactory;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdConfiguration;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdProtocol;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.profile.SecurityProfile;
import io.codeleaf.sec.spi.SessionDataStore;
import io.codeleaf.sec.stores.client.ClientSessionDataConfiguration;
import io.codeleaf.sec.stores.client.ClientSessionDataStore;

import java.io.IOException;

public final class JaxrsHandshakeConfigurationFactory extends SecurityProfileAwareConfigurationFactory<JaxrsHandshakeConfiguration> {

    private static final JaxrsHandshakeConfiguration DEFAULT;

    static {
        try {
            DEFAULT = new JaxrsHandshakeConfiguration("/authn", createDefaultProtocol(), createDefaultStore());
        } catch (ConfigurationException | IOException cause) {
            throw new ExceptionInInitializerError(cause);
        }
    }

    public JaxrsHandshakeConfigurationFactory() {
        super(DEFAULT);
    }

    public JaxrsHandshakeConfiguration parseConfiguration(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        try {
            return new JaxrsHandshakeConfiguration(
                    getPath(specification),
                    getProtocol(specification, securityProfile),
                    getStore(specification, securityProfile));
        } catch (IllegalArgumentException cause) {
            throw new InvalidSpecificationException(specification, "Can't parse specification: " + cause.getMessage(), cause);
        }
    }

    private String getPath(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("path")
                ? Specifications.parseString(specification, "path")
                : DEFAULT.getPath();
    }

    private SessionDataStore getStore(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        SessionDataStore sessionDataStore;
        if (specification.hasSetting("store")) {
            String store = Specifications.parseString(specification, "store");
            sessionDataStore = securityProfile.getRegistry().lookup(store, SessionDataStore.class);
        } else {
            sessionDataStore = DEFAULT.getStore();
        }
        return sessionDataStore;
    }

    private static SessionDataStore createDefaultStore() throws InvalidSpecificationException, SpecificationNotFoundException, SpecificationFormatException, ConfigurationNotFoundException, IOException {
        return ClientSessionDataStore.create(ConfigurationProvider.get().getConfiguration(ClientSessionDataConfiguration.class));
    }

    private JaxrsSessionIdProtocol getProtocol(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        JaxrsSessionIdProtocol jaxrsSessionIdProtocol;
        if (specification.hasSetting("protocol")) {
            String protocol = Specifications.parseString(specification, "protocol");
            jaxrsSessionIdProtocol = securityProfile.getRegistry().lookup(protocol, JaxrsSessionIdProtocol.class);
        } else {
            jaxrsSessionIdProtocol = DEFAULT.getProtocol();
        }
        return jaxrsSessionIdProtocol;
    }

    private static JaxrsSessionIdProtocol createDefaultProtocol() throws InvalidSpecificationException, SpecificationNotFoundException, SpecificationFormatException, ConfigurationNotFoundException, IOException {
        return new QuerySessionIdProtocol(ConfigurationProvider.get().getConfiguration(QuerySessionIdConfiguration.class));
    }
}
