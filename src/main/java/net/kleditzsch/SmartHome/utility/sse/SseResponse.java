package net.kleditzsch.SmartHome.utility.sse;

import java.util.Objects;
import java.util.Optional;

public class SseResponse {

    /**
     * Daten (Pflichtfeld)
     */
    private String data;

    /**
     * Ereignis
     */
    private String event;

    /**
     * ID
     */
    private String id;

    /**
     * Wartezeit bei Neuverbindung
     */
    private int retry;

    /**
     * Kommentar
     */
    private String comment;

    /**
     * @param data Daten
     */
    public SseResponse(String data) {

        Objects.requireNonNull(data);
        this.data = data;
    }

    /**
     * gibt die Daten zurück
     *
     * @return Daten
     */
    public String getData() {
        return data;
    }

    /**
     * setzt die Daten
     *
     * @param data Daten
     */
    public void setData(String data) {

        Objects.requireNonNull(data);
        this.data = data;
    }

    /**
     * gibt das Ereignis zurück
     *
     * @return Ereignis
     */
    public Optional<String> getEvent() {
        return Optional.ofNullable(event);
    }

    /**
     * setzt das Ereignis
     *
     * @param event Ereignis
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * gibt die ID zurück
     *
     * @return ID
     */
    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    /**
     * setzt die ID
     *
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * gibt die Wartezeit zum Neuverbinden (in ms) zurück
     *
     * @return Wartezeit zum Neuverbinden (in ms)
     */
    public Optional<Integer> getRetry() {
        return Optional.ofNullable(retry);
    }

    /**
     * setzt die Wartezeit zum Neuverbinden (in ms)
     *
     * @param retry Wartezeit zum neuverbinden (in ms)
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }

    /**
     * gibt den Kommentar zurück
     *
     * @return Kommentar
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * setzt den Kommentar
     *
     * @param comment Kommentar
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
