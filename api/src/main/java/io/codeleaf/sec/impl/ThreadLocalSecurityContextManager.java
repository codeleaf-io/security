package io.codeleaf.sec.impl;

import io.codeleaf.sec.SecurityContext;
import io.codeleaf.sec.spi.SecurityContextManager;

public final class ThreadLocalSecurityContextManager implements SecurityContextManager {

    private final ThreadLocal<SecurityContext> securityContextThreadLocal = new ThreadLocal<>();

    @Override
    public SecurityContext getSecurityContext(Object context) {
        return securityContextThreadLocal.get();
    }

    @Override
    public void setSecurityContext(Object context, SecurityContext securityContext) {
        securityContextThreadLocal.set(securityContext);
    }

    @Override
    public void clearSecurityContext(Object context) {
        securityContextThreadLocal.remove();
    }
}
