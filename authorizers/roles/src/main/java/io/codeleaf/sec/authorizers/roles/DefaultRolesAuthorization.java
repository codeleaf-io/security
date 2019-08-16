package io.codeleaf.sec.authorizers.roles;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultRolesAuthorization implements RolesAuthorization {

    private final Set<String> roles;

    private DefaultRolesAuthorization(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    public static DefaultRolesAuthorization create(Set<String> roles) {
        Objects.requireNonNull(roles);
        return new DefaultRolesAuthorization(Collections.unmodifiableSet(new LinkedHashSet<>(roles)));
    }

}
