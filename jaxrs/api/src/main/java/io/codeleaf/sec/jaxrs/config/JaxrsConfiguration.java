package io.codeleaf.sec.jaxrs.config;

import io.codeleaf.config.Configuration;

public final class JaxrsConfiguration implements Configuration {

    private final JaxrsHandshakeConfiguration handshakeConfiguration;

    public JaxrsConfiguration(JaxrsHandshakeConfiguration handshakeConfiguration) {
        this.handshakeConfiguration = handshakeConfiguration;
    }

    public JaxrsHandshakeConfiguration getHandshakeConfiguration() {
        return handshakeConfiguration;
    }

}
