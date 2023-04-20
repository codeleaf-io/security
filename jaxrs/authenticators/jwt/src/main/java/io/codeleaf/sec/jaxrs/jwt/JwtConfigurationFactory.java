package io.codeleaf.sec.jaxrs.jwt;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.ConfigurationException;
import io.codeleaf.config.ConfigurationNotFoundException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.spec.SpecificationFormatException;
import io.codeleaf.config.spec.SpecificationNotFoundException;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdConfiguration;
import io.codeleaf.sec.jaxrs.protocols.query.QuerySessionIdProtocol;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.spi.SessionDataStore;
import io.codeleaf.sec.stores.client.ClientSessionDataConfiguration;
import io.codeleaf.sec.stores.client.ClientSessionDataStore;

import java.io.IOException;

public final class JwtConfigurationFactory extends RegistryAwareConfigurationFactory<JwtConfiguration> {

    private static final JwtConfiguration DEFAULT;

    static {
        try {
            DEFAULT = new JwtConfiguration(
                    createDefaultProtocol(),
                    createDefaultStore(),
                    new JwtAuthenticationSerializer());
        } catch (ConfigurationException | IOException cause) {
            throw new ExceptionInInitializerError(cause);
        }
    }

    public JwtConfigurationFactory() {
        super(DEFAULT);
    }

    @Override
    public JwtConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        return new JwtConfiguration(
                getProtocol(specification, registry),
                getStore(specification, registry),
                DEFAULT.getSerializer());
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

    private static JaxrsSessionIdProtocol createDefaultProtocol() {
        return new QuerySessionIdProtocol(new QuerySessionIdConfiguration("_j"));
    }
}
