package io.codeleaf.sec.impl;

import io.codeleaf.sec.profile.SecurityProfile;
import io.codeleaf.sec.spi.SecurityProfileProvider;

// TODO: expose through interface, after Service Identity is supported
public final class ThreadLocalSecurityProfileManager implements SecurityProfileProvider {

    private static final ThreadLocal<SecurityProfile> securityProfileThreadLocal = new ThreadLocal<>();

    @Override
    public SecurityProfile getSecurityProfile() {
        return securityProfileThreadLocal.get();
    }

    public static void setSecurityProfile(SecurityProfile securityProfile) {
        securityProfileThreadLocal.set(securityProfile);
    }

    public static void clearSecurityProfile() {
        securityProfileThreadLocal.remove();
    }
}
