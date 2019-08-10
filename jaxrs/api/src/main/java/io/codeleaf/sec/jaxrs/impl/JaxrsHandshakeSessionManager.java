package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.jaxrs.spi.JaxrsHandshakeState;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JaxrsHandshakeSessionManager implements JaxrsHandshakeSession {

    private static final ThreadLocal<JaxrsHandshakeState> states = new ThreadLocal<>();
    private static final ThreadLocal<JaxrsRequestAuthenticatorExecutor> executors = new ThreadLocal<>();
    private static final ThreadLocal<ContainerRequestContext> requestContexts = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> attributes = ThreadLocal.withInitial(LinkedHashMap::new);

    private JaxrsHandshakeSessionManager() {
    }

    @Override
    public JaxrsHandshakeState getState() {
        return states.get();
    }

    public void setState(JaxrsHandshakeState state) {
        states.set(state);
    }

    @Override
    public JaxrsRequestAuthenticatorExecutor getExecutor() {
        return executors.get();
    }

    public void setExecutor(JaxrsRequestAuthenticatorExecutor executor) {
        executors.set(executor);
    }

    @Override
    public ContainerRequestContext getRequestContext() {
        return requestContexts.get();
    }

    public void setRequestContext(ContainerRequestContext requestContext) {
        requestContexts.set(requestContext);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes.get();
    }

    public void clear() {
        states.remove();
        executors.remove();
        requestContexts.remove();
    }

    private static final JaxrsHandshakeSessionManager INSTANCE = new JaxrsHandshakeSessionManager();

    public static JaxrsHandshakeSessionManager get() {
        return INSTANCE;
    }
}
