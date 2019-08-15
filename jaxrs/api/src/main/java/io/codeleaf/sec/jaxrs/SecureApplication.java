package io.codeleaf.sec.jaxrs;

import io.codeleaf.config.ConfigurationException;
import io.codeleaf.sec.jaxrs.impl.AuthenticatorResources;
import io.codeleaf.sec.profile.SecurityProfile;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SecureApplication extends Application {

    private final SecurityProfile securityProfile;
    private final Set<Object> jaxrsFilters;

    public SecureApplication() {
        this(SecurityProfile.get());
    }

    public SecureApplication(SecurityProfile securityProfile) {
        try {
            this.securityProfile = securityProfile;
            this.jaxrsFilters = JaxrsFilterFactory.create(securityProfile);
        } catch (ConfigurationException | IOException cause) {
            throw new ExceptionInInitializerError(cause);
        }
    }

    protected Set<Class<?>> getSecureClasses() {
        return Collections.emptySet();
    }

    protected Set<Object> getSecureSingletons() {
        return Collections.emptySet();
    }

    public final Set<Class<?>> getClasses() {
        return getSecureClasses();
    }

    public final Set<Object> getSingletons() {
        Set<Object> singletons = new LinkedHashSet<>(jaxrsFilters);
        singletons.add(AuthenticatorResources.create(securityProfile));
        singletons.addAll(getSecureSingletons());
        return singletons;
    }

}
