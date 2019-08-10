package io.codeleaf.sec.jaxrs;

import io.codeleaf.sec.jaxrs.impl.AuthenticatorResources;
import io.codeleaf.sec.profile.SecurityProfile;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SecureApplication extends Application {

    private final SecurityProfile securityProfile;

    public SecureApplication() {
        this(SecurityProfile.get());
    }

    public SecureApplication(SecurityProfile securityProfile) {
        this.securityProfile = securityProfile;
    }

    protected Set<Class<?>> getSecureClasses() {
        return Collections.emptySet();
    }

    protected Set<Object> getSecureSingletons() {
        return Collections.emptySet();
    }

    public final Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.addAll(getSecureClasses());
        return classes;
    }

    public final Set<Object> getSingletons() {
        Set<Object> singletons = new LinkedHashSet<>(JaxrsFilterFactory.create(securityProfile));
        singletons.add(AuthenticatorResources.create(securityProfile));
        singletons.addAll(getSecureSingletons());
        return singletons;
    }

}
