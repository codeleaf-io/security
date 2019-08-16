package io.codeleaf.sec.profile;

import io.codeleaf.sec.SecurityException;

public interface SecurityProfileAware {

    void setSecurityProfile(SecurityProfile securityProfile) throws SecurityException;

}
