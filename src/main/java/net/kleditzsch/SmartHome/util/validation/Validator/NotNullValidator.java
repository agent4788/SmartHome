package net.kleditzsch.SmartHome.util.validation.Validator;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * prüft ob ein feld nicht null ist
 */
public class NotNullValidator extends AbstractValidator {

    /**
     * validiert das Feld
     * wenn der Validator nicht für das feld zurifft wird true zurück gegeben
     *
     * @param object         Objekt
     * @param field          Feld
     * @param resourceBundle Sprachpaket
     * @return valide
     */
    public boolean validate(Object object, Field field, Optional<ResourceBundle> resourceBundle) throws IllegalAccessException {

        //prüfen on Annotation vorhanden
        if(field.isAnnotationPresent(ValidateNotNull.class)) {

            ValidateNotNull annotation = field.getAnnotation(ValidateNotNull.class);
            field.setAccessible(true);
            Object value = field.get(object);

            //auf null validieren
            if(value == null) {

                //nicht Valid
                String message = annotation.message();
                if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                    //mit Sprachvariablen
                    message = resourceBundle.get().getString(annotation.message());
                }
                message = String.format(message, field.getName());
                setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                return false;
            }
        }
        return true;
    }
}
