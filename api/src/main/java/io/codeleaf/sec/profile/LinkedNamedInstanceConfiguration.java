package io.codeleaf.sec.profile;

import io.codeleaf.config.Configuration;

public class LinkedNamedInstanceConfiguration extends NamedInstanceConfiguration {

    private final String next;

    public LinkedNamedInstanceConfiguration(String name, Class<?> implementationClass, Configuration configuration, String next) {
        super(name, implementationClass, configuration);
        this.next = next;
    }

    public String getNext() {
        return next;
    }

}
