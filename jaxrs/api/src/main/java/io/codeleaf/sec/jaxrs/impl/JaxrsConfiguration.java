package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.config.Configuration;
import io.codeleaf.sec.jaxrs.impl.JaxrsHandshakeConfiguration;

public final class JaxrsConfiguration implements Configuration {

    private final JaxrsHandshakeConfiguration handshakeConfiguration;

    public JaxrsConfiguration(JaxrsHandshakeConfiguration handshakeConfiguration) {
        this.handshakeConfiguration = handshakeConfiguration;
    }

    public JaxrsHandshakeConfiguration getHandshakeConfiguration() {
        return handshakeConfiguration;
    }

}
