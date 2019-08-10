package io.codeleaf.sec.spi;

import io.codeleaf.sec.SecurityContext;

/**
 * This class is responsible for matching a current code execution thread to the appropriate Security Context
 */
public interface SecurityContextProvider {

    SecurityContext getSecurityContext(Object context);

}
