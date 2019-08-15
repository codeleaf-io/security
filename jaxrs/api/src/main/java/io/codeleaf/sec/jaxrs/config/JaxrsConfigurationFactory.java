package io.codeleaf.sec.jaxrs.config;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;

public final class JaxrsConfigurationFactory extends RegistryAwareConfigurationFactory<JaxrsConfiguration> {

    private static final JaxrsConfiguration DEFAULT = new JaxrsConfiguration(JaxrsHandshakeConfigurationFactory.DEFAULT);

    private final JaxrsHandshakeConfigurationFactory factory = new JaxrsHandshakeConfigurationFactory();

    public JaxrsConfigurationFactory() {
        super(DEFAULT);
    }

    @Override
    protected JaxrsConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        return new JaxrsConfiguration(factory.parseConfiguration(specification, registry));
    }

}
