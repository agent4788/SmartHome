package net.kleditzsch.SmartHome.util.validation.Validator;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidatePast;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * prüft ob ein Datumsobjekt in der Vergangenheit liegt
 */
public class PastValidator extends AbstractValidator {

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
        if(field.isAnnotationPresent(ValidatePast.class)) {

            //Datentypen Filtern
            Class type = field.getType();
            if(type.isAssignableFrom(LocalDateTime.class)
                    | type.isAssignableFrom(LocalDate.class)
                    | type.isAssignableFrom(LocalTime.class)) {

                ValidatePast annotation = field.getAnnotation(ValidatePast.class);
                field.setAccessible(true);

                //Prüfbedingung
                boolean valid = true;
                if(type.isAssignableFrom(LocalDateTime.class)) {

                    LocalDateTime value = (LocalDateTime) field.get(object);
                    LocalDateTime now = LocalDateTime.now();
                    if(!value.isAfter(now)) {

                        valid = false;
                    }
                } else if(type.isAssignableFrom(LocalDate.class)) {

                    LocalDate value = (LocalDate) field.get(object);
                    LocalDate now = LocalDate.now();
                    if(!value.isAfter(now)) {

                        valid = false;
                    }
                } else if(type.isAssignableFrom(LocalTime.class)) {

                    LocalTime value = (LocalTime) field.get(object);
                    LocalTime now = LocalTime.now();
                    if(!value.isAfter(now)) {

                        valid = false;
                    }
                }

                if(valid) {

                    //nicht Valid
                    String message = annotation.message();
                    if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                        //mit Sprachvariablen
                        message = resourceBundle.get().getString(annotation.message());
                    }
                    setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                    return false;
                }
            }
        }
        return true;
    }
}
