package no.nav.modig.security.tilgangskontroll.policy.pep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessControl {
    AccessControlAttribute[] attributes() default {};

    /**
     * Use {@link AccessControlAttribute} instead
     */
    @Deprecated
    String resourceId() default "";

    /**
     * Use {@link AccessControlAttribute} instead
     */
    @Deprecated
    String actionId() default "";
}
