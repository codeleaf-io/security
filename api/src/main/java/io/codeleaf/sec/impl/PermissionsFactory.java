package io.codeleaf.sec.impl;

import io.codeleaf.config.spec.InvalidSettingException;
import io.codeleaf.config.spec.SettingNotFoundException;
import io.codeleaf.config.spec.Specification;
import io.codeleaf.config.util.Specifications;
import io.codeleaf.sec.Permissions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public final class PermissionsFactory {

    private static final class Handler implements InvocationHandler {

        private final Specification specification;
        private final Map<String, Object> resultCache = new HashMap<>();

        private Handler(Specification specification) {
            this.specification = specification;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Objects.equals(Object.class, method.getDeclaringClass())) {
                return method.invoke(specification, args);
            }
            if (args != null && args.length != 0) {
                throw new IllegalArgumentException("Unsupported method call!");
            }
            if (!resultCache.containsKey(method.getName())) {
                resultCache.put(method.getName(), getValue(method.getName(), method.getReturnType()));
            }
            return resultCache.get(method.getName());
        }

        @SuppressWarnings("unchecked")
        private <T> T getValue(String name, Class<T> returnType) throws SettingNotFoundException, InvalidSettingException {
            Object value;
            if (Objects.equals(String.class, returnType)) {
                value = specification.hasSetting(name)
                        ? Specifications.parseString(specification, name)
                        : null;
            } else if (Objects.equals(Integer.class, returnType) || Objects.equals(Integer.TYPE, returnType)) {
                value = specification.hasSetting(name)
                        ? Specifications.parseInt(specification, name)
                        : 0;
            } else if (Objects.equals(List.class, returnType)) {
                value = specification.hasSetting(name)
                        ? Specifications.parseList(specification, name)
                        : Collections.emptyList();
            } else if (Objects.equals(Set.class, returnType)) {
                value = specification.hasSetting(name)
                        ? Specifications.parseSet(specification, name)
                        : Collections.emptySet();
            } else if (Objects.equals(Boolean.class, returnType) || Objects.equals(Boolean.TYPE, returnType)) {
                value = specification.hasSetting(name) && Specifications.parseBoolean(specification, name);
            } else {
                throw new UnsupportedOperationException("Return type not supported!");
            }
            return (T) value;
        }

    }

    private PermissionsFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <P extends Permissions> P create(Class<P> permissionsTypeClass, Specification specification) {
        Objects.requireNonNull(permissionsTypeClass);
        Objects.requireNonNull(specification);
        return (P) Proxy.newProxyInstance(permissionsTypeClass.getClassLoader(), new Class<?>[]{permissionsTypeClass}, new PermissionsFactory.Handler(specification));
    }

}
