package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.config.Configuration;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.spi.SessionDataStore;

public final class JaxrsHandshakeConfiguration implements Configuration {

    private final String path;
    private final JaxrsSessionIdProtocol protocol;
    private final SessionDataStore store;

    public JaxrsHandshakeConfiguration(String path, JaxrsSessionIdProtocol protocol, SessionDataStore store) {
        this.path = path;
        this.protocol = protocol;
        this.store = store;
    }

    public String getPath() {
        return path;
    }

    public JaxrsSessionIdProtocol getProtocol() {
        return protocol;
    }

    public SessionDataStore getStore() {
        return store;
    }
}
