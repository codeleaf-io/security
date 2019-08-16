package io.codeleaf.sec.authorizers.groups;

import io.codeleaf.sec.Authorization;

import java.util.Set;

public interface GroupAuthorization extends Authorization {

    Set<String> getGroups();

}
