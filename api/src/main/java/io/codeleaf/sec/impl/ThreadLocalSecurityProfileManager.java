package io.codeleaf.sec.impl;

import io.codeleaf.sec.profile.SecurityProfile;
import io.codeleaf.sec.spi.SecurityProfileProvider;

public final class ThreadLocalSecurityProfileManager implements SecurityProfileProvider {

    private final ThreadLocal<SecurityProfile> securityProfileThreadLocal = new ThreadLocal<>();

    @Override
    public SecurityProfile getSecurityProfile() {
        return securityProfileThreadLocal.get();
    }

    public void setSecurityProfile(SecurityProfile securityProfile) {
        securityProfileThreadLocal.set(securityProfile);
    }

    public void clearSecurityProfile() {
        securityProfileThreadLocal.remove();
    }
}
