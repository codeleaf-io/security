package io.codeleaf.sec.spi;

import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.profile.SecurityProfile;

public interface SecurityProfileAware {

    void init(SecurityProfile securityProfile) throws SecurityException;

}
