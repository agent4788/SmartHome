package net.kleditzsch.SmartHome.util.validation.Validator;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidatePattern;
import net.kleditzsch.SmartHome.util.validation.Interface.AbstractValidator;
import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * prüft ob ein String dem Suchmuster entspricht
 */
public class PatternValidator extends AbstractValidator {

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
        if(field.isAnnotationPresent(ValidatePattern.class)) {

            //Datentypen Filtern
            Class type = field.getType();
            if(type.isAssignableFrom(String.class)) {

                ValidatePattern annotation = field.getAnnotation(ValidatePattern.class);
                String patternStr = annotation.value();
                ValidatePattern.Flags[] flagsArray = annotation.flags();
                int flags = 0;
                for(ValidatePattern.Flags flag : flagsArray) {

                    flags |= getFlag(flag);
                }

                field.setAccessible(true);
                String value = (String) field.get(object);

                //Prüfbedingung
                Pattern pattern = Pattern.compile(patternStr, flags);
                Matcher matcher = pattern.matcher(value);
                if(!matcher.find()) {

                    //nicht Valid
                    String message = annotation.message();
                    if(resourceBundle.isPresent() && resourceBundle.get().getString(annotation.message()).length() > 0) {

                        //mit Sprachvariablen
                        message = resourceBundle.get().getString(annotation.message());
                    }
                    message = String.format(message, value, patternStr);
                    setValidationError(new ValidationError(field.getName(), annotation.errorCode(), message));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * gibt das Pattern Flag zum Flag der Annotation zurück
     *
     * @param flag Flag der Annotation
     * @return Pattern Flag
     */
    private int getFlag(ValidatePattern.Flags flag) {

        switch (flag) {

            case CASE_INSENSITIVE:
                return Pattern.CASE_INSENSITIVE;
            case COMMENTS:
                return Pattern.COMMENTS;
            case MULTILINE:
                return Pattern.MULTILINE;
            case DOTALL:
                return Pattern.DOTALL;
            case UNICODE_CASE:
                return Pattern.UNICODE_CASE;
            case UNICODE_CHARACTER_CLASS:
                return Pattern.UNICODE_CHARACTER_CLASS;
            case UNIX_LINES:
                return Pattern.UNIX_LINES;
            default:
                return 0;
        }
    }
}
