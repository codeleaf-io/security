package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.sec.jaxrs.config.JaxrsZone;
import io.codeleaf.sec.profile.SecurityProfile;
import io.codeleaf.sec.profile.SecurityZone;

public final class JaxrsZones {

    private JaxrsZones() {
    }

    public static JaxrsZone getZone(String requestPath, SecurityProfile securityProfile) {
        for (SecurityZone zone : securityProfile.getSecurityZones()) {
            if (zone instanceof JaxrsZone) {
                for (String endpoint : ((JaxrsZone) zone).getEndpoints()) {
                    if (endpointMatches(endpoint, requestPath)) {
                        return (JaxrsZone) zone;
                    }
                }
            }
        }
        return null;
    }

    private static boolean endpointMatches(String endpoint, String requestPath) {
        return requestPath.matches(endpoint);
    }
}
