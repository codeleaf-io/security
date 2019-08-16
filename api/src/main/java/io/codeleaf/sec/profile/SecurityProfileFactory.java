package io.codeleaf.sec.profile;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.common.behaviors.impl.DefaultRegistry;
import io.codeleaf.config.Configuration;
import io.codeleaf.config.impl.AbstractConfigurationFactory;
import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.spec.impl.MapSpecification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.impl.ThreadLocalSecurityContextManager;
import io.codeleaf.sec.spi.SecurityContextManager;
import io.codeleaf.sec.spi.SecurityProfileAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;

public final class SecurityProfileFactory extends AbstractConfigurationFactory<SecurityProfile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityProfileFactory.class);

    public SecurityProfileFactory() {
        super(SecurityProfile.class);
    }

    @Override
    protected SecurityProfile parseConfiguration(Specification specification) throws InvalidSpecificationException {
        Registry registry = new DefaultRegistry();
        parseRegistryObjects(specification, registry);
        Map<String, String> authenticatorChain = parseAuthenticatorChain(specification, registry);
        Map<String, Configuration> protocolConfigurations = parseProtocolConfigurations(specification, registry);
        List<SecurityZone> securityZones = parseSecurityZones(specification);
        SecurityContextManager securityContextManager = new ThreadLocalSecurityContextManager();
        SecurityProfile securityProfile = new SecurityProfile(registry, authenticatorChain, protocolConfigurations, securityZones, securityContextManager);
        initRegistryObjects(specification, securityProfile);
        return securityProfile;
    }

    private void initRegistryObjects(Specification specification, SecurityProfile securityProfile) throws InvalidSpecificationException {
        try {
            for (String name : securityProfile.getRegistry().getNames(SecurityProfileAware.class)) {
                securityProfile.getRegistry().lookup(name, SecurityProfileAware.class).init(securityProfile);
            }
        } catch (SecurityException cause) {
            throw new InvalidSpecificationException(specification, "Failed to initialize objects: " + cause.getMessage(), cause);
        }
    }

    private void parseRegistryObjects(Specification specification, Registry registry) throws InvalidSpecificationException {
        for (String objectName : specification.getChilds("registry")) {
            Class<?> objectType = Specifications.parseClass(specification, "registry", objectName, "type");
            Object object;
            if (!specification.getChilds("registry", objectName, "configuration").isEmpty()) {
                Configuration configuration = Specifications.parseConfiguration(specification, registry, "registry", objectName, "configuration");
                object = createObject(specification, objectName, objectType, configuration, registry);
            } else {
                object = createObject(specification, objectName, objectType);
            }
            registry.register(objectName, object);
        }
    }

    private List<SecurityZone> parseSecurityZones(Specification specification) throws SettingNotFoundException, InvalidSettingException {
        List<SecurityZone> securityZones = new ArrayList<>();
        for (String zoneName : specification.getChilds("zones")) {
            securityZones.add(parseSecurityZone(specification, zoneName));
        }
        return securityZones;
    }

    private SecurityZone parseSecurityZone(Specification specification, String zoneName) throws SettingNotFoundException, InvalidSettingException {
        try {
            return (SecurityZone) Specifications.parseConfiguration(specification, zoneName, Arrays.asList("zones", zoneName));
        } catch (ClassCastException cause) {
            throw new InvalidSettingException(specification, specification.getSetting("zones", zoneName), "Zone " + zoneName + " is not extending SecurityZone!", cause);
        } catch (InvalidSpecificationException cause) {
            List<String> field = Arrays.asList("zones", zoneName);
            Specification.Setting setting = new Specification.Setting(field, MapSpecification.create(specification, field));
            throw new InvalidSettingException(specification, setting, "Invalid security zone " + zoneName + ": " + cause.getMessage(), cause);
        }
    }

    private Map<String, Configuration> parseProtocolConfigurations(Specification specification, Registry registry) throws SettingNotFoundException, InvalidSettingException {
        Map<String, Configuration> protocolConfigurations = new LinkedHashMap<>();
        for (String protocolName : specification.getChilds("protocols")) {
            Configuration configuration = Specifications.parseConfiguration(specification, registry, "protocols", protocolName);
            protocolConfigurations.put(configuration.getClass().getName(), configuration);
        }
        return protocolConfigurations;
    }

    private Map<String, String> parseAuthenticatorChain(Specification specification, Registry registry) throws InvalidSpecificationException {
        Map<String, String> authenticatorChain = new LinkedHashMap<>();
        for (String authenticatorName : specification.getChilds("authenticatorChain")) {
            authenticatorChain.put(authenticatorName, Specifications.parseString(specification, "authenticatorChain", authenticatorName));
        }
        return authenticatorChain;
    }

    private Object createObject(Specification specification, String objectName, Class<?> objectTypeClass) throws InvalidSpecificationException {
        try {
            LOGGER.debug("Instantiating object: " + objectName);
            return objectTypeClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException cause) {
            LOGGER.error("Failed to instantiate object: " + cause.getMessage());
            throw new InvalidSpecificationException(specification, cause.getMessage(), cause);
        }
    }

    /*
     * First, looks at a static create(configuration, registry),
     * Second, looks at a static create(configuration),
     * When none found, for a constructor(configuration),
     * otherwise error.
     */
    private Object createObject(Specification specification, String objectName, Class<?> objectTypeClass, Configuration configuration, Registry registry) throws InvalidSpecificationException {
        try {
            LOGGER.debug("Initializing object: " + objectName);
            Class<? extends Configuration> configurationClass = configuration.getClass();
            Method createConfigRegistryMethod = getPublicStaticCreateMethodOrNull(objectTypeClass, new Class<?>[]{configurationClass, Registry.class});
            Object instance = null;
            if (createConfigRegistryMethod != null) {
                instance = createConfigRegistryMethod.invoke(null, configuration, registry);
            } else {
                Method createConfigMethod = getPublicStaticCreateMethodOrNull(objectTypeClass, new Class<?>[]{configurationClass});
                if (createConfigMethod != null) {
                    instance = createConfigMethod.invoke(null, configuration);
                } else {
                    Constructor<?> constructor = getConstructor(objectTypeClass, configurationClass);
                    if (constructor != null) {
                        instance = constructor.newInstance(configuration);
                    }
                }
            }
            if (instance == null) {
                throw new IllegalStateException("No proper create method or constructor found for instantiation of: " + objectName);
            }
            return instance;
        } catch (IllegalAccessException | IllegalStateException | InvocationTargetException | InstantiationException cause) {
            LOGGER.error("Failed to initialize object: " + cause.getMessage());
            throw new InvalidSpecificationException(specification, cause.getMessage(), cause);
        }
    }

    private Method getPublicStaticCreateMethodOrNull(Class<?> objectTypeClass, Class<?>[] parameterTypes) {
        for (Method method : objectTypeClass.getMethods()) {
            if (method.getName().equals("create")) {
                if (Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                    if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private Constructor<?> getConstructor(Class<?> objectTypeClass, Class<? extends Configuration> configurationClass) {
        for (Constructor<?> constructor : objectTypeClass.getConstructors()) {
            Type[] parameterTypes = constructor.getGenericParameterTypes();
            for (Type type : parameterTypes) {
                if (type.equals(configurationClass)) {
                    return constructor;
                }
            }
        }
        return null;
    }
}
