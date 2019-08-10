package io.codeleaf.sec.authz;

import io.codeleaf.sec.Permissions;

public interface FilePermissions extends Permissions {

    String getUser();

    String getGroup();

    int getUserBits();

    int getGroupBits();

    int getOtherBits();

}
