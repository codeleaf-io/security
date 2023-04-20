package io.codeleaf.sec.jaxrs.jwt;

import io.codeleaf.config.Configuration;
import io.codeleaf.sec.jaxrs.spi.JaxrsSessionIdProtocol;
import io.codeleaf.sec.spi.SessionDataStore;

public final class JwtConfiguration implements Configuration {

    private final JaxrsSessionIdProtocol protocol;
    private final SessionDataStore store;
    private final JwtAuthenticationSerializer serializer;

    public JwtConfiguration(JaxrsSessionIdProtocol protocol, SessionDataStore store, JwtAuthenticationSerializer serializer) {
        this.protocol = protocol;
        this.store = store;
        this.serializer = serializer;
    }

    public JaxrsSessionIdProtocol getProtocol() {
        return protocol;
    }

    public SessionDataStore getStore() {
        return store;
    }

    public JwtAuthenticationSerializer getSerializer() {
        return serializer;
    }
}
