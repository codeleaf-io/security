package io.codeleaf.sec.password.dummy;

import io.codeleaf.config.ConfigurationException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.sec.Authentication;
import io.codeleaf.sec.Authorization;
import io.codeleaf.sec.impl.DefaultAuthentication;
import io.codeleaf.sec.impl.DefaultGroupsAndRolesAuthorization;
import io.codeleaf.sec.password.spi.Credentials;
import io.codeleaf.sec.password.spi.PasswordRequestAuthenticator;
import io.codeleaf.sec.spi.AuthorizationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public final class DummyAuthorizationLoader implements PasswordRequestAuthenticator, AuthorizationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyAuthorizationLoader.class);

    private final String userName;
    private final String password;
    private final Set<Authorization> authorizations;

    private DummyAuthorizationLoader(String userName, String password, Set<Authorization> authorizations) {
        this.userName = userName;
        this.password = password;
        this.authorizations = authorizations;
    }

    @Override
    public Authentication authenticate(Credentials credentials) {
        boolean matches = true;
        if (!userName.equals(credentials.getUserName())) {
            LOGGER.debug("Username not matching!");
            matches = false;
        }
        if (!password.equals(credentials.getPassword())) {
            LOGGER.debug("Password not matching!");
            matches = false;
        }
        if (matches) {
            LOGGER.debug("Correct credentials");
        }
        return matches ? DefaultAuthentication.create(userName) : null;
    }

    @Override
    public Set<Authorization> loadAuthorizations(Authentication authentication) {
        return authorizations;
    }

    public DummyAuthorizationLoader() throws ConfigurationException, IOException {
        this(ConfigurationProvider.get().getConfiguration(DummyConfiguration.class));
    }

    public DummyAuthorizationLoader(DummyConfiguration dummyConfiguration) {
        this(dummyConfiguration.getUserName(), dummyConfiguration.getPassword(),
                Collections.singleton(DefaultGroupsAndRolesAuthorization.create(dummyConfiguration.getGroups(), dummyConfiguration.getRoles())));
    }
}