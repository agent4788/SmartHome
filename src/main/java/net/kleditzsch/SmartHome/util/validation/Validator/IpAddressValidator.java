package net.kleditzsch.SmartHome.util.validation.Validator;

import com.google.common.net.InetAddresses;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateIpAddress;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * prüft ob das Feld eine gültige IP Adresse enthält
 */
public class IpAddressValidator extends AbstractValidator {

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
        if(field.isAnnotationPresent(ValidateIpAddress.class)) {

            //Datentypen Filtern
            Class type = field.getType();
            if(type.isAssignableFrom(String.class)) {

                ValidateIpAddress annotation = field.getAnnotation(ValidateIpAddress.class);
                field.setAccessible(true);
                String value = (String) field.get(object);

                //Prüfbedingung
                if(!InetAddresses.isInetAddress(value)) {

                    //nicht Valid
                    String message = annotation.message();
                    if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                        //mit Sprachvariablen
                        message = resourceBundle.get().getString(annotation.message());
                    }
                    message = String.format(message, value);
                    setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                    return false;
                }
            }
        }
        return true;
    }
}
