package net.kleditzsch.SmartHome.util.validation;

/**
 * Validierungsfehler
 */
public class ValidationError {

    /**
     * Fehler Daten
     */
    private String fieldName = "";
    private int errorCode = 0;
    private String message = "";

    /**
     * @param fieldName Name des Feldes
     * @param errorCode Fehlercode
     * @param message Fehlermeldung
     */
    public ValidationError(String fieldName, int errorCode, String message) {
        this.fieldName = fieldName;
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * gibt den Namen des Feldes zurück
     *
     * @return Name des Feldes
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * gibt den Fehlercode zurück
     *
     * @return Fehlercode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * gibt die fehlermeldung zurück
     *
     * @return fehlermeldung
     */
    public String getMessage() {
        return message;
    }
}
