package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Luftdruck
 */
public class AirPressureValue extends SensorValue {

    private Type type = Type.SENSORVALUE_AIR_PRESSURE;

    /**
     * Luftdruck in hPa
     */
    private double airPressure = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public AirPressureValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public AirPressureValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt den Luftdruck zurück
     *
     * @return Luftdruck
     */
    public double getAirPressure() {
        return this.airPressure;
    }

    /**
     * setzt den Luftdruck
     *
     * @param airPressure Luftdruck
     */
    public void setAirPressure(double airPressure) {

        this.airPressure = airPressure;
        setChangedData();
    }

    /**
     * füght den Luftdruck hinzu
     *
     * @param airPressure Luftdruck
     */
    public void pushAirPressure(double airPressure) {

        setAirPressure(airPressure);
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
