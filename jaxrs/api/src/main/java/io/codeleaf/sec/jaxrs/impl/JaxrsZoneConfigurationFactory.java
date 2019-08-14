package io.codeleaf.sec.jaxrs.impl;

import io.codeleaf.config.impl.AbstractConfigurationFactory;
import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.profile.AuthenticationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JaxrsZoneConfigurationFactory extends AbstractConfigurationFactory<JaxrsZone> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxrsZoneConfigurationFactory.class);

    public JaxrsZoneConfigurationFactory() {
        super(JaxrsZone.class);
    }

    @Override
    protected JaxrsZone parseConfiguration(Specification specification) throws InvalidSpecificationException {
        String zoneName = Specifications.parseString(specification, "name");
        LOGGER.debug("Parsing zone: " + zoneName + "...");
        AuthenticationPolicy policy = parsePolicy(specification, specification.getSetting("policy"));
        String authenticatorName = specification.hasSetting("authenticator")
                ? Specifications.parseString(specification, "authenticator")
                : null;
        if (policy == AuthenticationPolicy.REQUIRED && authenticatorName == null) {
            throw new SettingNotFoundException(specification, Collections.singletonList("authenticator"));
        }
        List<String> endpoints = parseEndpoints(specification, specification.getSetting("endpoints"));
        return new JaxrsZone(zoneName, policy, endpoints, authenticatorName);
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

    private List<String> parseEndpoints(Specification specification, Specification.Setting setting) throws InvalidSettingException {
        List<String> endpoints;
        if (setting.getValue() instanceof String) {
            endpoints = Collections.singletonList((String) setting.getValue());
        } else if (setting.getValue() instanceof List) {
            endpoints = new ArrayList<>();
            for (Object item : ((List<?>) setting.getValue())) {
                if (!(item instanceof String)) {
                    throw new InvalidSettingException(specification, setting, "Endpoints must only contain Strings!");
                } else {
                    endpoints.add((String) item);
                }
            }
        } else {
            throw new InvalidSettingException(specification, setting, "Endpoints must be a String or list of Strings!");
        }
        return endpoints;
    }

}
