package io.codeleaf.sec.annotation;

import io.codeleaf.sec.profile.AuthenticationPolicy;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {

    AuthenticationPolicy value() default AuthenticationPolicy.REQUIRED;

    String authenticator() default "";
}
