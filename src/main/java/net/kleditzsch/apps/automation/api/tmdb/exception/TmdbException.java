package net.kleditzsch.apps.automation.api.tmdb.exception;

/**
 * The Movie DB Ausnahme
 */
public class TmdbException extends Exception {

    /**
     * Fehlercode
     */
    private int statusCode = 0;

    /**
     * @param message Meldung
     * @param statusCode Fehlercode
     */
    public TmdbException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * @param message Meldung
     * @param statusCode Fehlercode
     * @param cause vorherige Ausnahme
     */
    public TmdbException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * gibt den Fehlercode zurück
     *
     * @return Fehlercode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * gibt die Objektinfo als String zurück
     *
     * @return Zeichenkette
     */
    @Override
    public String toString() {
        return "TMDB Anfrage Fehlgeschlagen Fehler: " + getStatusCode() + " -> " + getMessage();
    }
}
