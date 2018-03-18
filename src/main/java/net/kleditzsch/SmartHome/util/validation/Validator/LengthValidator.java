package net.kleditzsch.SmartHome.util.validation.Validator;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateLength;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * prüft ob ein String eine vorgegebene länge hat
 */
public class LengthValidator extends AbstractValidator {

    /**
     * validiert das Feld
     * wenn der Validator nicht für das feld zurifft wird true zurück gegeben
     *
     * @param object         Objekt
     * @param field          Feld
     * @param resourceBundle Sprachpaket
     * @return valide
     */
    @Override
    public boolean validate(Object object, Field field, Optional<ResourceBundle> resourceBundle) throws IllegalAccessException {

        //prüfen on Annotation vorhanden
        if(field.isAnnotationPresent(ValidateLength.class)) {

            //Datentypen Filtern
            Class type = field.getType();
            if(type.isAssignableFrom(String.class)) {

                ValidateLength annotation = field.getAnnotation(ValidateLength.class);
                int min = annotation.min();
                int max = annotation.max();
                field.setAccessible(true);
                String value = (String) field.get(object);

                //Prüfbedingung
                if(value.length() < min || value.length() > max) {

                    //nicht Valid
                    String message = annotation.message();
                    if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                        //mit Sprachvariablen
                        message = resourceBundle.get().getString(annotation.message());
                    }
                    message = String.format(message, NumberFormat.getInstance().format(min), NumberFormat.getInstance().format(max));
                    setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                    return false;
                }
            }
        }
        return true;
    }
}
