package io.codeleaf.sec.profile;

public enum AuthenticationPolicy {
    REQUIRED, // must be authenticated or won't proceed
    OPTIONAL, // authentication is optional
    NONE // no authentication will happen, even if credentials were provided
}
