package net.kleditzsch.SmartHome.util.validation.Validator;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Minmalwert validieren
 */
public class MinValidator extends AbstractValidator {

    /**
     * validiert das Feld
     * wenn der Validator nicht für das feld zurifft wird true zurück gegeben
     *
     * @param object Objekt
     * @param field Feld
     * @return valide
     */
    public boolean validate(Object object, Field field, Optional<ResourceBundle> resourceBundle) throws IllegalAccessException {

        //prüfen on Annotation vorhanden
        if(field.isAnnotationPresent(ValidateMin.class)) {

            //Datentypen Filtern
            Class type = field.getType();
            if(type.isAssignableFrom(int.class)
                    || type.isAssignableFrom(long.class)
                    || type.isAssignableFrom(double.class)
                    || type.isAssignableFrom(float.class)
                    || type.isAssignableFrom(Integer.class)
                    || type.isAssignableFrom(Long.class)
                    || type.isAssignableFrom(Double.class)
                    || type.isAssignableFrom(Float.class)
                    ) {

                //Vergleichswert holen
                ValidateMin annotation = field.getAnnotation(ValidateMin.class);
                double min = annotation.value();

                field.setAccessible(true);
                double value;
                if(type.isAssignableFrom(Integer.class)) {

                    value = (Integer) field.get(object);
                } else if(type.isAssignableFrom(Long.class)) {

                    value = (Long) field.get(object);
                }  else if(type.isAssignableFrom(Double.class)) {

                    value = (Double) field.get(object);
                }  else if(type.isAssignableFrom(Float.class)) {

                    value = (Float) field.get(object);
                } else {

                    value = field.getDouble(object);
                }

                //mit Wert vergleichen
                if(value < min) {

                    //nicht Valid
                    String message = annotation.message();
                    if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                        //mit Sprachvariablen
                        message = resourceBundle.get().getString(annotation.message());
                    }
                    message = String.format(message, NumberFormat.getInstance().format(min));
                    setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                    return false;
                }
            }
        }
        return true;
    }
}
