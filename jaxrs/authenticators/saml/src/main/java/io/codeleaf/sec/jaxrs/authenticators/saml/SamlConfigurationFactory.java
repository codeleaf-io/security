package io.codeleaf.sec.jaxrs.authenticators.saml;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;

public final class SamlConfigurationFactory extends RegistryAwareConfigurationFactory<SamlConfiguration> {

    public SamlConfigurationFactory() {
        super(SamlConfiguration.class);
    }

    @Override
    protected SamlConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        return null;
    }

}
