package io.codeleaf.sec.jaxrs.authenticators.basic;

import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.SecurityProfileAwareConfigurationFactory;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;
import io.codeleaf.sec.profile.SecurityProfile;

public final class BasicConfigurationFactory extends SecurityProfileAwareConfigurationFactory<BasicConfiguration> {

    public BasicConfigurationFactory() {
        super(BasicConfiguration.class);
    }

    @Override
    public BasicConfiguration parseConfiguration(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        try {
            return new BasicConfiguration(
                    getAuthenticator(specification, securityProfile),
                    getRealm(specification),
                    getPrompt(specification));
        } catch (IllegalArgumentException cause) {
            throw new InvalidSpecificationException(specification, "Can't parse specification: " + cause.getMessage(), cause);
        }
    }

    private String getRealm(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("realm")
                ? Specifications.parseString(specification, "realm")
                : "Secured Application";
    }

    private PasswordRequestAuthenticator getAuthenticator(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        String authenticatorName = Specifications.parseString(specification, "passwordAuthenticator");
        PasswordRequestAuthenticator authenticator = securityProfile.getRegistry().lookup(authenticatorName, PasswordRequestAuthenticator.class);
        if (authenticator == null) {
            throw new InvalidSpecificationException(specification, "No authenticator found with name: " + authenticatorName);
        }
        return authenticator;
    }

    private boolean getPrompt(Specification specification) throws SettingNotFoundException, InvalidSettingException {
        return !specification.hasSetting("prompt") || Specifications.parseBoolean(specification, "prompt");
    }
}
