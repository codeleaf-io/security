package io.codeleaf.sec;

import java.security.Principal;
import java.util.Map;

public interface Authentication {

    Principal getPrincipal();

    default String getIdentity() {
        return getPrincipal().getName();
    }

    Map<String, Object> getAttributes();

    boolean isSecure();

}