package io.codeleaf.sec.impl;

import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.sec.profile.SecurityZone;

public abstract class AbstractZoneFactory<Z extends SecurityZone> extends DefaultSecurityZoneFactory {

    public AbstractZoneFactory(Class<Z> zoneTypeClass) {
        super(zoneTypeClass);
    }

    @Override
    protected Z parseConfiguration(Specification specification, String zoneName) throws InvalidSpecificationException {
        return doParseConfiguration(specification, super.parseConfiguration(specification, zoneName));
    }

    protected abstract Z doParseConfiguration(Specification specification, SecurityZone securityZone) throws InvalidSpecificationException;

}
