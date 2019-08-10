package io.codeleaf.sec.password.dummy;

import io.codeleaf.config.impl.AbstractConfigurationFactory;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;

import java.util.Collections;
import java.util.Set;

public final class DummyConfigurationFactory extends AbstractConfigurationFactory<DummyConfiguration> {

    private static final DummyConfiguration DEFAULT = new DummyConfiguration("dummy", "dummy", Collections.singleton("clients"), Collections.singleton("admin"));

    public DummyConfigurationFactory() {
        super(DEFAULT);
    }

    @Override
    public DummyConfiguration parseConfiguration(Specification specification) throws InvalidSpecificationException {
        return new DummyConfiguration(
                Specifications.parseString(specification, "userName"),
                Specifications.parseString(specification, "password"),
                parseGroups(specification),
                parseRoles(specification));
    }

    private Set<String> parseGroups(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("groups")
                ? Specifications.parseSet(specification, "groups")
                : DEFAULT.getGroups();
    }

    private Set<String> parseRoles(Specification specification) throws SettingNotFoundException {
        return specification.hasSetting("roles")
                ? Specifications.parseSet(specification, "roles")
                : DEFAULT.getRoles();
    }

}
