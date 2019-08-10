package io.codeleaf.sec.profile;

import io.codeleaf.config.Configuration;

public class NamedInstanceConfiguration {

    private final String name;
    private final Class<?> implementationClass;
    private final Configuration configuration;

    public NamedInstanceConfiguration(String name, Class<?> implementationClass, Configuration configuration) {
        this.name = name;
        this.implementationClass = implementationClass;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
