package io.codeleaf.sec.spi;

import io.codeleaf.sec.SecurityContext;

public interface SecurityContextManager extends SecurityContextProvider {

    void setSecurityContext(Object context, SecurityContext securityContext);

    void clearSecurityContext(Object context);

}
