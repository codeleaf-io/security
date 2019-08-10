package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.sec.impl.SecurityProfileAwareConfigurationFactory;
import io.codeleaf.sec.profile.SecurityProfile;

public final class JaxrsConfigurationFactory extends SecurityProfileAwareConfigurationFactory<JaxrsConfiguration> {

    private final JaxrsHandshakeConfigurationFactory factory = new JaxrsHandshakeConfigurationFactory();

    public JaxrsConfigurationFactory() {
        super(JaxrsConfiguration.class);
    }

    @Override
    protected JaxrsConfiguration parseConfiguration(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        return new JaxrsConfiguration(factory.parseConfiguration(specification, securityProfile));
    }

}
