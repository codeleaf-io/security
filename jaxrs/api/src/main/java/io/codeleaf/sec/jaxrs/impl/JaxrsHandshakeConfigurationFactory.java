package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.ConfigurationNotFoundException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.config.spec.*;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdConfiguration;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdProtocol;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.spi.SessionDataStore;
import io.codeleaf.sec.stores.client.ClientSessionDataConfiguration;
import io.codeleaf.sec.stores.client.ClientSessionDataStore;

import java.io.IOException;

public final class JaxrsHandshakeConfigurationFactory extends RegistryAwareConfigurationFactory<JaxrsHandshakeConfiguration> {

    private static final JaxrsHandshakeConfiguration DEFAULT = createDefault();

    static JaxrsHandshakeConfiguration createDefault() {
        try {
            return new JaxrsHandshakeConfiguration("/authn", createDefaultProtocol(), createDefaultStore());
        } catch (Throwable cause) {
            throw new ExceptionInInitializerError(cause);
        }
    }

    public JaxrsHandshakeConfigurationFactory() {
        super(DEFAULT);
    }

    public JaxrsHandshakeConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        try {
            return new JaxrsHandshakeConfiguration(
                    getPath(specification),
                    getProtocol(specification, registry),
                    getStore(specification, registry));
        } catch (IllegalArgumentException cause) {
            throw new InvalidSpecificationException(specification, "Can't parse specification: " + cause.getMessage(), cause);
        }
    }

    private String getPath(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("path")
                ? Specifications.parseString(specification, "path")
                : DEFAULT.getPath();
    }

    private SessionDataStore getStore(Specification specification, Registry registry) throws InvalidSpecificationException {
        SessionDataStore sessionDataStore;
        if (specification.hasSetting("store")) {
            String store = Specifications.parseString(specification, "store");
            sessionDataStore = registry.lookup(store, SessionDataStore.class);
        } else {
            sessionDataStore = DEFAULT.getStore();
        }
        return sessionDataStore;
    }

    private static SessionDataStore createDefaultStore() throws InvalidSpecificationException, SpecificationNotFoundException, SpecificationFormatException, ConfigurationNotFoundException, IOException {
        return ClientSessionDataStore.create(ConfigurationProvider.get().getConfiguration(ClientSessionDataConfiguration.class));
    }

    private JaxrsSessionIdProtocol getProtocol(Specification specification, Registry registry) throws InvalidSpecificationException {
        JaxrsSessionIdProtocol jaxrsSessionIdProtocol;
        if (specification.hasSetting("protocol")) {
            String protocol = Specifications.parseString(specification, "protocol");
            jaxrsSessionIdProtocol = registry.lookup(protocol, JaxrsSessionIdProtocol.class);
        } else {
            jaxrsSessionIdProtocol = DEFAULT.getProtocol();
        }
        return jaxrsSessionIdProtocol;
    }

    private static JaxrsSessionIdProtocol createDefaultProtocol() throws InvalidSpecificationException, SpecificationNotFoundException, SpecificationFormatException, ConfigurationNotFoundException, IOException {
        return new QuerySessionIdProtocol(ConfigurationProvider.get().getConfiguration(QuerySessionIdConfiguration.class));
    }

}
