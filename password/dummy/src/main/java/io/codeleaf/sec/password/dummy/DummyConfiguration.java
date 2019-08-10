package io.codeleaf.sec.password.dummy;

import io.codeleaf.config.Configuration;

import java.util.Set;

public final class DummyConfiguration implements Configuration {

    private final String userName;
    private final String password;
    private final Set<String> groups;
    private final Set<String> roles;

    DummyConfiguration(String userName, String password, Set<String> groups, Set<String> roles) {
        this.userName = userName;
        this.password = password;
        this.groups = groups;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<String> getRoles() {
        return roles;
    }

}
