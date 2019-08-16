package io.codeleaf.sec.profile;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.common.utils.Registries;
import io.codeleaf.common.utils.SingletonServiceLoader;
import io.codeleaf.config.Configuration;
import io.codeleaf.config.ConfigurationException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.sec.spi.SecurityContextManager;
import io.codeleaf.sec.spi.SecurityContextProvider;
import io.codeleaf.sec.spi.SecurityProfileProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SecurityProfile implements Configuration {

    private final Registry registry;
    private final Map<String, String> authenticatorChain;
    private final Map<String, Configuration> protocolConfigurations;
    private final List<SecurityZone> securityZones;
    private final SecurityContextManager securityContextManager;

    public SecurityProfile(Registry registry, Map<String, String> authenticatorChain, Map<String, Configuration> protocolConfigurations, List<SecurityZone> securityZones, SecurityContextManager securityContextManager) {
        this.registry = registry;
        this.authenticatorChain = authenticatorChain;
        this.protocolConfigurations = protocolConfigurations;
        this.securityZones = securityZones;
        this.securityContextManager = securityContextManager;
    }

    public Registry getRegistry() {
        return registry;
    }

    public Map<String, String> getAuthenticatorChain() {
        return authenticatorChain;
    }

    @SuppressWarnings("unchecked")
    public <C extends Configuration> C getProtocolConfiguration(Class<C> protocolConfigurationType) throws ConfigurationException, IOException {
        if (!protocolConfigurations.containsKey(protocolConfigurationType.getName())) {
            protocolConfigurations.put(protocolConfigurationType.getName(), ConfigurationProvider.get().getConfiguration(protocolConfigurationType, registry));
        }
        return (C) protocolConfigurations.get(protocolConfigurationType.getName());
    }

    public List<SecurityZone> getSecurityZones() {
        return securityZones;
    }

    public SecurityContextManager getSecurityContextManager() {
        return securityContextManager;
    }

    public SecurityContextProvider getSecurityContextProvider() {
        return securityContextManager;
    }

    public static SecurityProfile get() {
        return Holder.get();
    }

    private final static class Holder {

        private Holder() {
        }

        private static SecurityProfileProvider INSTANCE;

        static {
            init();
        }

        private static void init() {
            try {
                INSTANCE = SingletonServiceLoader.load(SecurityProfileProvider.class);
            } catch (Exception cause) {
                throw new ExceptionInInitializerError(cause);
            }
        }

        public static SecurityProfile get() {
            return INSTANCE.getSecurityProfile();
        }

    }

    private static final SecurityProfile EMPTY_PROFILE = new SecurityProfile(Registries.emptyRegistry(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), null);

    public static SecurityProfile emptyProfile() {
        return EMPTY_PROFILE;
    }

}
