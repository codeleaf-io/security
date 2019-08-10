package io.codeleaf.sec.spi;

import io.codeleaf.sec.profile.SecurityProfile;

/**
 * This class is responsible for matching a current code execution thread to the appropriate Security Profile
 */
public interface SecurityProfileProvider {

    SecurityProfile getSecurityProfile();

}
