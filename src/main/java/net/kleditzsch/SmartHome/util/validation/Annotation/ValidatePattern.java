package net.kleditzsch.SmartHome.util.validation.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * nach Regulärem Ausdruck validieren
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePattern {

    enum Flags {

        CASE_INSENSITIVE,
        COMMENTS,
        MULTILINE,
        DOTALL,
        UNICODE_CASE,
        UNICODE_CHARACTER_CLASS,
        UNIX_LINES
    }

    String value();
    Flags[] flags() default {};
    int errorCode() default 0;
    String message() default "";
}
