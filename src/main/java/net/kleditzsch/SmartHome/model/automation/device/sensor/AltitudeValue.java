package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

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
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = -10_000, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 10_000, errorCode = 10205, message = "Ungültiger Sensorwert")
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
