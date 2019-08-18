package io.codeleaf.sec.spi;

import io.codeleaf.common.behaviors.Identity;
import io.codeleaf.sec.SecurityException;

public interface IdentityGenerator {

    void init() throws SecurityException;

    Identity generate(String name) throws SecurityException;

}
