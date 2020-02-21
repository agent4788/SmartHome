package net.kleditzsch.applications.automation.model.device.sensor;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Laufzeit
 */
public class DurationValue extends SensorValue {

    private Type type = Type.SENSORVALUE_DURATION;

    /**
     * Laufzeit in Sekunden
     */
    private long duration = 0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public DurationValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public DurationValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Laufzeit zurück
     *
     * @return Laufzeit
     */
    public long getDuration() {
        return duration;
    }

    /**
     * setzt die Laufzeit
     *
     * @param duration Laufzeit
     */
    public void setDuration(long duration) {

        this.duration = duration;
        setChangedData();
    }

    /**
     * fügt die Laufzeit hinzu
     *
     * @param duration Laufzeit
     */
    public void pushDuration(long duration) {

        setDuration(duration);
        setLastPushTime(LocalDateTime.now());
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
