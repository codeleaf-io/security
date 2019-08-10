package io.codeleaf.sec.impl;

import io.codeleaf.config.Configuration;
import io.codeleaf.config.impl.ContextAwareConfigurationFactory;
import io.codeleaf.sec.profile.SecurityProfile;

public abstract class SecurityProfileAwareConfigurationFactory<T extends Configuration> extends ContextAwareConfigurationFactory<T, SecurityProfile> {

    public SecurityProfileAwareConfigurationFactory(Class<T> configurationTypeClass) {
        super(configurationTypeClass, SecurityProfile.class);
    }

    @SuppressWarnings("unchecked")
    public SecurityProfileAwareConfigurationFactory(T defaultConfiguration) {
        this((Class<T>) defaultConfiguration.getClass(), defaultConfiguration);
    }

    public SecurityProfileAwareConfigurationFactory(Class<T> configurationTypeClass, T defaultConfiguration) {
        super(configurationTypeClass, SecurityProfile.class, defaultConfiguration, SecurityProfile::emptyProfile);
    }

}
