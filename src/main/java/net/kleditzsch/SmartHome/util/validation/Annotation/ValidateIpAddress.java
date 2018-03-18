package net.kleditzsch.SmartHome.util.validation.Annotation;

/**
 * prüfen ob der String eine gültige IP Adresse enthält
 */
public @interface ValidateIpAddress {

    int errorCode() default 0;
    String message() default "";
}
