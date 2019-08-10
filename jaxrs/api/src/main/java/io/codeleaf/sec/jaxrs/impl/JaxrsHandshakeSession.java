package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.jaxrs.spi.JaxrsHandshakeState;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Map;

public interface JaxrsHandshakeSession {

    interface SessionAware {

        void init(JaxrsHandshakeSession session);
    }

    JaxrsHandshakeState getState();

    JaxrsRequestAuthenticatorExecutor getExecutor();

    ContainerRequestContext getRequestContext();

    Map<String, Object> getAttributes();
}
