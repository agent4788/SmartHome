package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Standorthöhe
 *
 * @author Oliver Kleditzsch
 * @copyright Copyright (c) 2016, Oliver Kleditzsch
 */
public class AltitudeValue extends SensorValue {

    private Type type = Type.SENSORVALUE_ALTITUDE;

    /**
     * Standorthöhe in Meter
     */
    private double altitude = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public AltitudeValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public AltitudeValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Standorthöhe zurück
     *
     * @return Standorthöhe
     */
    public double getAltitude() {
        return this.altitude;
    }

    /**
     * setzt die Standorthöhe
     *
     * @param altitude Standorthöhe
     */
    public void setAltitude(double altitude) {

        this.altitude = altitude;
        setChangedData();
    }

    /**
     * fügt die Standorthöhe hinzu
     *
     * @param altitude Standorthöhe
     */
    public void pushAltitude(double altitude) {

        setAltitude(altitude);
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
