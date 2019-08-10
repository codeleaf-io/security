package io.codeleaf.sec.profile;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.common.behaviors.impl.DefaultRegistry;
import io.codeleaf.common.utils.Types;
import io.codeleaf.config.Configuration;
import io.codeleaf.config.ConfigurationNotFoundException;
import io.codeleaf.config.ConfigurationProvider;
import io.codeleaf.config.impl.AbstractConfigurationFactory;
import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.InvalidSpecificationException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.spec.impl.MapSpecification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.impl.ThreadLocalSecurityContextManager;
import io.codeleaf.sec.spi.Authenticator;
import io.codeleaf.sec.spi.SecurityContextManager;
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
        Map<String, AuthenticatorNode> authenticatorNodes = parseAuthenticatorNodes(specification, registry);
        Map<String, Configuration> protocolConfigurations = new LinkedHashMap<>(); // TODO
        List<SecurityZone> securityZones = new ArrayList<>(); // TODO
        SecurityContextManager securityContextManager = new ThreadLocalSecurityContextManager();
        return new SecurityProfile(registry, authenticatorNodes, protocolConfigurations, securityZones, securityContextManager);
    }

    private Map<String, AuthenticatorNode> parseAuthenticatorNodes(Specification specification, Registry registry) throws InvalidSpecificationException {
        Map<String, AuthenticatorNode> authenticatorNodes = new LinkedHashMap<>();
        System.out.println(specification.getChilds("authenticators"));
        for (String authenticatorName : specification.getChilds("authenticators")) {
            authenticatorNodes.put(authenticatorName, parseAuthenticatorNode(authenticatorName, specification, registry));
        }
        return authenticatorNodes;
    }

    @SuppressWarnings("unchecked")
    private AuthenticatorNode parseAuthenticatorNode(String authenticatorName, Specification specification, Registry registry) throws InvalidSpecificationException {
        LOGGER.debug("Parsing authenticator: " + authenticatorName + "...");
        String onFailure;
        if (specification.hasSetting("authenticators", authenticatorName, "onFailure")) {
            onFailure = Specifications.parseString(specification, "authenticators", authenticatorName, "onFailure");
        } else {
            onFailure = null;
        }
        Class<? extends Authenticator> authenticatorClass = (Class<? extends Authenticator>) parseClass(specification, specification.getSetting("authenticators", authenticatorName, "implementation"));
        Configuration configuration = parseAuthenticationConfiguration(authenticatorName, specification, registry);
        Authenticator authenticator = createAuthenticator(specification, authenticatorName, authenticatorClass, configuration, registry);
        registry.register(authenticatorName, authenticator);
        return new AuthenticatorNode(authenticatorName, authenticator, onFailure);
    }

    private Class<?> parseClass(Specification specification, Specification.Setting setting) throws InvalidSettingException {
        if (!(setting.getValue() instanceof String)) {
            throw new InvalidSettingException(specification, setting, "Implementation must be a String!");
        }
        try {
            return Class.forName((String) setting.getValue());
        } catch (ClassNotFoundException cause) {
            throw new InvalidSettingException(specification, setting, "Could not find class: " + setting.getValue(), cause);
        }
    }

    private Configuration parseAuthenticationConfiguration(String authenticatorName, Specification specification, Registry registry) throws InvalidSpecificationException {
        Specification.Setting configTypeSetting = specification.getSetting("authenticators", authenticatorName, "configuration", "type");
        Class<?> configurationClass = parseClass(specification, configTypeSetting);
        if (!Configuration.class.isAssignableFrom(configurationClass)) {
            throw new InvalidSettingException(specification, configTypeSetting, "Configuration type is not implementing Configuration: " + configTypeSetting.getValue());
        }
        try {
            Specification configSpecification = MapSpecification.create(specification, "authenticators", authenticatorName, "configuration", "settings");
            return ConfigurationProvider.get().parseConfiguration(Types.cast(configurationClass), configSpecification, registry);
        } catch (ConfigurationNotFoundException cause) {
            throw new InvalidSettingException(specification, specification.getSetting("authenticators", authenticatorName, "configuration", "settings"), cause.getCause());
        }
    }

    /*
     * First, looks at a static create(configuration, registry),
     * Second, looks at a static create(configuration),
     * When none found, for a constructor(configuration),
     * otherwise error.
     */
    private Authenticator createAuthenticator(Specification specification, String authenticatorName, Class<? extends Authenticator> authenticatorClass, Configuration configuration, Registry registry) throws InvalidSpecificationException {
        try {
            LOGGER.debug("Initializing authenticator: " + authenticatorName);
            Class<? extends Configuration> configurationClass = configuration.getClass();
            Method createConfigRegistryMethod = getPublicStaticCreateMethodOrNull(authenticatorClass, new Class<?>[]{configurationClass, Registry.class});
            Object instance = null;
            if (createConfigRegistryMethod != null) {
                instance = createConfigRegistryMethod.invoke(null, configuration, registry);
            } else {
                Method createConfigMethod = getPublicStaticCreateMethodOrNull(authenticatorClass, new Class<?>[]{configurationClass});
                if (createConfigMethod != null) {
                    instance = createConfigMethod.invoke(null, configuration);
                } else {
                    Constructor<?> constructor = getConstructor(authenticatorClass, configurationClass);
                    if (constructor != null) {
                        instance = constructor.newInstance(configuration);
                    }
                }
            }
            if (instance == null) {
                throw new IllegalStateException("No proper create method or constructor found for instantiation of: " + authenticatorName);
            }
//            else {
            // TODO: handle this in JAXRS - or maybe have a SecurityProfile init...
//                if (instance instanceof JaxrsHandshakeSession.SessionAware) {
//                    ((HandshakeSession.SessionAware) instance).init(HandshakeSessionManager.get());
//                }
//            }
            return (Authenticator) instance;
        } catch (IllegalAccessException | IllegalStateException | InvocationTargetException | InstantiationException cause) {
            LOGGER.error("Failed to initialize authenticator: " + cause.getMessage());
            throw new InvalidSpecificationException(specification, cause.getMessage(), cause);
        }
    }

    private Method getPublicStaticCreateMethodOrNull(Class<? extends Authenticator> authenticatorClass, Class<?>[] parameterTypes) {
        for (Method method : authenticatorClass.getMethods()) {
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

    private Constructor<?> getConstructor(Class<? extends Authenticator> authenticatorClass, Class<? extends Configuration> configurationClass) {
        for (Constructor<?> constructor : authenticatorClass.getConstructors()) {
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
