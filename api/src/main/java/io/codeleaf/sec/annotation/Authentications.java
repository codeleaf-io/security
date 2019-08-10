package io.codeleaf.sec.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public final class Authentications {

    private Authentications() {
    }

    public static Authentication getAuthentication(Method method, Class<?> clazz) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(clazz);
        Authentication authentication;
        Authentication methodAuthentication = getMethodAuthentication(method);
        if (methodAuthentication != null) {
            authentication = methodAuthentication;
        } else {
            Authentication classAuthentication = getClassAuthentication(clazz);
            if (classAuthentication != null) {
                authentication = classAuthentication;
            } else {
                authentication = null;
            }
        }
        return authentication;
    }

    public static Authentication getMethodAuthentication(Method method) {
        Objects.requireNonNull(method);
        Authentication authentication;
        Authentication declaredAuthentication = method.getAnnotation(Authentication.class);
        if (declaredAuthentication != null) {
            authentication = declaredAuthentication;
        } else {
            Authentication inheritedMethodPolicy = getInheritedMethodAuthentication(method);
            if (inheritedMethodPolicy != null) {
                authentication = inheritedMethodPolicy;
            } else {
                authentication = null;
            }
        }
        return authentication;
    }

    public static Authentication getInheritedMethodAuthentication(Method method) {
        Objects.requireNonNull(method);
        for (Class<?> clazz = method.getDeclaringClass().getSuperclass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (declaredMethod.getName().equals(method.getName()) && Arrays.equals(declaredMethod.getParameterTypes(), method.getParameterTypes())) {
                    Authentication authentication = declaredMethod.getAnnotation(Authentication.class);
                    if (authentication != null) {
                        return authentication;
                    }
                }
            }
        }
        return null;
    }

    public static Authentication getClassAuthentication(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        Authentication authentication;
        Authentication declaredAuthentication = clazz.getAnnotation(Authentication.class);
        if (declaredAuthentication != null) {
            authentication = declaredAuthentication;
        } else {
            Authentication inheritedClassAuthentication = getInheritedClassAuthentication(clazz);
            if (inheritedClassAuthentication != null) {
                authentication = inheritedClassAuthentication;
            } else {
                authentication = null;
            }
        }
        return authentication;
    }

    public static Authentication getInheritedClassAuthentication(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        for (Class<?> superclass = clazz; !superclass.equals(Object.class); superclass = superclass.getSuperclass()) {
            Authentication authentication = superclass.getAnnotation(Authentication.class);
            if (authentication != null) {
                return authentication;
            }
        }
        return null;
    }
}
