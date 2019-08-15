package io.codeleaf.sec.jaxrs;

import io.codeleaf.config.ConfigurationException;
import io.codeleaf.sec.jaxrs.impl.JaxrsHandshakeStateHandler;
import io.codeleaf.sec.jaxrs.impl.JaxrsZoneHandler;
import io.codeleaf.sec.profile.SecurityProfile;

import java.io.IOException;
import java.util.Set;

public final class JaxrsFilterFactory {

    private JaxrsFilterFactory() {
    }

    public static Set<Object> create() throws IOException, ConfigurationException {
        return create(SecurityProfile.get());
    }

    public static Set<Object> create(SecurityProfile securityProfile) throws IOException, ConfigurationException {
        return new JaxrsZoneHandler(
                securityProfile.getSecurityContextManager(),
                securityProfile,
                JaxrsHandshakeStateHandler.create(securityProfile))
                .getFilters();
    }

}
