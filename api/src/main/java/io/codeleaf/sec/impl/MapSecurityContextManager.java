package io.codeleaf.sec.impl;

import io.codeleaf.sec.SecurityContext;
import io.codeleaf.sec.spi.SecurityContextManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MapSecurityContextManager implements SecurityContextManager {

    private final Map<Object, SecurityContext> securityContextMap = new ConcurrentHashMap<>();

    @Override
    public SecurityContext getSecurityContext(Object context) {
        return securityContextMap.get(context);
    }

    @Override
    public void setSecurityContext(Object context, SecurityContext securityContext) {
        securityContextMap.put(context, securityContext);
    }

    @Override
    public void clearSecurityContext(Object context) {
        securityContextMap.remove(context);
    }
}
