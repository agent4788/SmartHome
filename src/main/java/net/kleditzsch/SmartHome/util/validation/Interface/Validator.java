package net.kleditzsch.SmartHome.util.validation.Interface;

import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * führt die Validierung aus
 *
 * @author Oliver Kleditzsch
 * @copyright Copyright (c) 2016, Oliver Kleditzsch
 */
public interface Validator {

    /**
     * validiert das Feld
     * wenn der Validator nicht für das feld zurifft wird true zurück gegeben
     *
     * @param object Objekt
     * @param field Feld
     * @param resourceBundle Sprachpaket
     * @return valide
     */
    boolean validate(Object object, Field field, Optional<ResourceBundle> resourceBundle) throws IllegalAccessException;

    /**
     * wenn Validierung Fehlgeschlagen gibt die Funktion das Fehlerobjekt zurück
     *
     * @return Fehlgeschlagen
     */
    Optional<ValidationError> getValidationError();
}
