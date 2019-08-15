package io.codeleaf.sec.impl;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.common.behaviors.impl.DefaultRegistry;
import io.codeleaf.config.Configuration;
import io.codeleaf.config.impl.ContextAwareConfigurationFactory;

public abstract class RegistryAwareConfigurationFactory<T extends Configuration> extends ContextAwareConfigurationFactory<T, Registry> {

    public RegistryAwareConfigurationFactory(Class<T> configurationTypeClass) {
        super(configurationTypeClass, Registry.class, null, DefaultRegistry::new);
    }

    @SuppressWarnings("unchecked")
    public RegistryAwareConfigurationFactory(T defaultConfiguration) {
        this((Class<T>) defaultConfiguration.getClass(), defaultConfiguration);
    }

    public RegistryAwareConfigurationFactory(Class<T> configurationTypeClass, T defaultConfiguration) {
        super(configurationTypeClass, Registry.class, defaultConfiguration, DefaultRegistry::new);
    }

}
