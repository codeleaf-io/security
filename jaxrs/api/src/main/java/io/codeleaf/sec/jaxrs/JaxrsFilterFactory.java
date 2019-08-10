package io.codeleaf.sec.jaxrs;

import io.codeleaf.sec.jaxrs.impl.JaxrsHandshakeStateHandler;
import io.codeleaf.sec.jaxrs.impl.JaxrsZoneHandler;
import io.codeleaf.sec.profile.SecurityProfile;

import java.util.Set;

public final class JaxrsFilterFactory {

    private JaxrsFilterFactory() {
    }

    public static Set<Object> create() {
        return create(SecurityProfile.get());
    }

    public static Set<Object> create(SecurityProfile securityProfile) {
        return new JaxrsZoneHandler(
                securityProfile.getSecurityContextManager(),
                securityProfile,
                JaxrsHandshakeStateHandler.create(securityProfile))
                .getFilters();
    }

}
