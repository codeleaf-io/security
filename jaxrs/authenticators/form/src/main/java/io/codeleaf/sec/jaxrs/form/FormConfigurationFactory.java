package io.codeleaf.sec.jaxrs.form;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.RegistryAwareConfigurationFactory;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;

import java.net.URI;

public final class FormConfigurationFactory extends RegistryAwareConfigurationFactory<FormConfiguration> {

    public FormConfigurationFactory() {
        super(FormConfiguration.class);
    }

    @Override
    public FormConfiguration parseConfiguration(Specification specification, Registry registry) throws InvalidSpecificationException {
        try {
            return new FormConfiguration(
                    getAuthenticator(specification, registry),
                    getCustomLoginFormUri(specification),
                    getUsernameField(specification),
                    getPasswordField(specification),
                    getContentField(specification));
        } catch (IllegalArgumentException cause) {
            throw new InvalidSpecificationException(specification, "Can't parse specification: " + cause.getMessage(), cause);
        }
    }

    private PasswordRequestAuthenticator getAuthenticator(Specification specification, Registry registry) throws InvalidSpecificationException {
        String authenticatorName = Specifications.parseString(specification, "passwordAuthenticator");
        PasswordRequestAuthenticator authenticator = registry.lookup(authenticatorName, PasswordRequestAuthenticator.class);
        if (authenticator == null) {
            throw new InvalidSpecificationException(specification, "No authenticator found with name: " + authenticatorName);
        }
        return authenticator;
    }

    private URI getCustomLoginFormUri(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("loginUri")
                ? URI.create(Specifications.parseString(specification, "loginUri"))
                : null;
    }

    private String getUsernameField(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("usernameField")
                ? Specifications.parseString(specification, "usernameField")
                : "username";
    }

    private String getPasswordField(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("passwordField")
                ? Specifications.parseString(specification, "passwordField")
                : "password";
    }

    private String getContentField(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("contextField")
                ? Specifications.parseString(specification, "contextField")
                : "context";
    }
}
