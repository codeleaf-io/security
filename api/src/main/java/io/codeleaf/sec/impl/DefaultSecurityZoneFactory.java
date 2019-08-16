package io.codeleaf.sec.impl;

import io.codeleaf.config.impl.ContextAwareConfigurationFactory;
import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.spec.impl.MapSpecification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.Permissions;
import io.codeleaf.sec.profile.AuthenticationPolicy;
import io.codeleaf.sec.profile.SecurityZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultSecurityZoneFactory extends ContextAwareConfigurationFactory<SecurityZone, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityZoneFactory.class);

    public DefaultSecurityZoneFactory() {
        super(SecurityZone.class, String.class);
    }

    @SuppressWarnings("unchecked")
    DefaultSecurityZoneFactory(Class<? extends SecurityZone> zoneTypeClass) {
        super((Class) zoneTypeClass, String.class);
    }

    @Override
    protected SecurityZone parseConfiguration(Specification specification, String zoneName) throws InvalidSpecificationException {
        LOGGER.debug("Parsing zone: " + zoneName + "...");
        AuthenticationPolicy policy = parsePolicy(specification, specification.getSetting("policy"));
        String authenticatorName = specification.hasSetting("authenticator")
                ? Specifications.parseString(specification, "authenticator")
                : null;
        if (policy == AuthenticationPolicy.NONE && authenticatorName != null) {
            throw new InvalidSettingException(specification, specification.getSetting("authenticator"), "Can't define authenticator when policy = NONE");
        }
        if (policy != AuthenticationPolicy.NONE && authenticatorName == null) {
            throw new SettingNotFoundException(specification, Collections.singletonList("authenticator"));
        }
        Set<String> authorizationLoaderNames = specification.hasSetting("authorizationProviders")
                ? parseAuthenticatorProviders(specification, specification.getSetting("authorizationProviders"))
                : Collections.emptySet();
        if (authenticatorName == null && !authorizationLoaderNames.isEmpty()) {
            throw new InvalidSettingException(specification, specification.getSetting("authorizationProviders"), "Only allowed when authenticator is set!");
        }
        String authorizerName = specification.hasSetting("authorizer")
                ? Specifications.parseString(specification, "authorizer")
                : null;
        if (policy != AuthenticationPolicy.REQUIRED && authorizerName != null) {
            throw new InvalidSettingException(specification, specification.getSetting("authorizer"), "Only allowed when policy = REQUIRED!");
        }
        Set<Permissions> permissions = specification.getChilds("permissions").isEmpty()
                ? Collections.emptySet()
                : parsePermissions(specification);
        if (!permissions.isEmpty() && authorizerName == null) {
            throw new InvalidSettingException(specification, specification.getSetting("permissions"), "Only allowed when authorizer is set!");
        }
        return new DefaultSecurityZone(zoneName, policy, authenticatorName, authorizationLoaderNames, authorizerName, permissions);
    }

    private Set<Permissions> parsePermissions(Specification specification) throws InvalidSpecificationException {
        Set<Permissions> permissions = new LinkedHashSet<>();
        for (String permissionType : specification.getChilds("permissions")) {
            permissions.add(createPermissionsProxy(permissionType, specification));
        }
        return permissions;
    }

    @SuppressWarnings("unchecked")
    private Permissions createPermissionsProxy(String permissionType, Specification specification) throws InvalidSpecificationException {
        try {
            Class<?> permissionsClass = Class.forName(permissionType);
            if (!Permissions.class.isAssignableFrom(permissionsClass)) {
                throw new InvalidSpecificationException(specification, "Permissions type does not extend Permissions: " + permissionType);
            }
            return PermissionsFactory.create((Class<? extends Permissions>) permissionsClass, MapSpecification.create(specification, "permissions", permissionType));
        } catch (ClassNotFoundException cause) {
            throw new InvalidSpecificationException(specification, "Unknown permissions type: " + permissionType, cause);
        }
    }

    private Set<String> parseAuthenticatorProviders(Specification specification, Specification.Setting setting) throws InvalidSettingException {
        Set<String> providers;
        if (setting.getValue() instanceof String) {
            providers = Collections.singleton((String) setting.getValue());
        } else if (setting.getValue() instanceof List) {
            providers = new LinkedHashSet<>();
            for (Object item : ((List<?>) setting.getValue())) {
                if (!(item instanceof String)) {
                    throw new InvalidSettingException(specification, setting, "AuthorizationProviders must only contain Strings!");
                } else {
                    providers.add((String) item);
                }
            }
        } else {
            throw new InvalidSettingException(specification, setting, "AuthorizationProviders must be a String or list of Strings!");
        }
        return providers;
    }

    private AuthenticationPolicy parsePolicy(Specification specification, Specification.Setting setting) throws InvalidSettingException {
        if (!(setting.getValue() instanceof String)) {
            throw new InvalidSettingException(specification, setting, "policy must be a String!");
        }
        AuthenticationPolicy policy;
        switch ((String) setting.getValue()) {
            case "none":
                policy = AuthenticationPolicy.NONE;
                break;
            case "optional":
                policy = AuthenticationPolicy.OPTIONAL;
                break;
            case "required":
                policy = AuthenticationPolicy.REQUIRED;
                break;
            default:
                throw new InvalidSettingException(specification, setting, "Invalid policy value, must be: none, optional, redirect or required!");
        }
        return policy;
    }

}
