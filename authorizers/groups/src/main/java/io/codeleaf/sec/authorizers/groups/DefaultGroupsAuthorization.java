package io.codeleaf.sec.authorizers.groups;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultGroupsAuthorization implements GroupAuthorization {

    private final Set<String> groups;

    private DefaultGroupsAuthorization(Set<String> groups) {
        this.groups = groups;
    }

    @Override
    public Set<String> getGroups() {
        return groups;
    }

    public static DefaultGroupsAuthorization create(Set<String> groups) {
        Objects.requireNonNull(groups);
        return new DefaultGroupsAuthorization(Collections.unmodifiableSet(new LinkedHashSet<>(groups)));
    }

}
