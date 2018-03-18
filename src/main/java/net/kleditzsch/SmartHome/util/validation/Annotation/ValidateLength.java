package net.kleditzsch.SmartHome.util.validation.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * String länge Validieren
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateLength {

    int min() default 0;
    int max() default Integer.MAX_VALUE;
    int errorCode() default 0;
    String message() default "";
}
