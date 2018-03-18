package net.kleditzsch.SmartHome.util.validation.Interface;

import net.kleditzsch.SmartHome.util.validation.ValidationError;

import java.util.Optional;

/**
 * Standard Implementierung Validator
 */
public abstract class AbstractValidator implements Validator {

    /**
     * Validierungsfehler
     */
    private ValidationError validationError = null;

    /**
     * setzt das Fehlerobjekt
     *
     * @param validationError Fehlerobjekt
     */
    protected void setValidationError(ValidationError validationError) {
        this.validationError = validationError;
    }

    /**
     * wenn Validierung Fehlgeschlagen gibt die Funktion das Fehlerobjekt zurück
     *
     * @return Fehlgeschlagen
     */
    public Optional<ValidationError> getValidationError() {
        return Optional.ofNullable(validationError);
    }
}
