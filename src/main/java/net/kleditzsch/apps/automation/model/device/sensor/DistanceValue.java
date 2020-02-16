package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Entfernung
 */
public class DistanceValue extends SensorValue {

    private Type type = Type.SENSORVALUE_DISTANCE;

    /**
     * Entfernung in mm
     */
    private double distance = 0.0;

    /**
     * Offset in mm
     */
    private double offset = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public DistanceValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public DistanceValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Entfernung zurück
     *
     * @return Entfernung
     */
    public double getDistance() {
        return distance;
    }

    /**
     * gibt die Entfernung mit Offset zurück
     *
     * @return Entfernung
     */
    public double getDistanceWithOffset() {
        return distance + this.offset;
    }

    /**
     * setzt die Entfernung
     *
     * @param distance Entfernung
     */
    public void setDistance(double distance) {

        this.distance = distance;
        setChangedData();
    }

    /**
     * fügt die Entfernung hinzu
     *
     * @param distance Entfernung
     */
    public void pushDistance(double distance) {

        setDistance(distance);
        setLastPushTime(LocalDateTime.now());
    }

    /**
     * setzt das Offset
     *
     * @return Offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * gibt das Offset zurück
     *
     * @param offset Offset
     */
    public void setOffset(double offset) {

        this.offset = offset;
        setChangedData();
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
