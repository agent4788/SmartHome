package net.kleditzsch.SmartHome.util.validation.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * prüfen ob ein Datums oder Zeitobjekt in der Vergangenheit liegt
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePast {

    int errorCode() default 0;
    String message() default "";
}
