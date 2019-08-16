package io.codeleaf.sec.jaxrs.config;

import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.sec.impl.AbstractZoneFactory;
import io.codeleaf.sec.profile.SecurityZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JaxrsZoneFactory extends AbstractZoneFactory<JaxrsZone> {

    public JaxrsZoneFactory() {
        super(JaxrsZone.class);
    }

    @Override
    protected JaxrsZone doParseConfiguration(Specification specification, SecurityZone securityZone) throws InvalidSpecificationException {
        List<String> endpoints = parseEndpoints(specification, specification.getSetting("endpoints"));
        return new JaxrsZone(securityZone, endpoints);
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
