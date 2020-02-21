package net.kleditzsch.applications.automation.model.device.sensor;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Temperatur
 */
public class TemperatureValue extends SensorValue {

    private Type type = Type.SENSORVALUE_TEMPERATURE;

    /**
     * Temperatur in °C
     */
    private double temperature = 0.0;

    /**
     * Offset in °K
     */
    private double offset = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public TemperatureValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public TemperatureValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Temperatur zurück
     *
     * @return Temperatur
     */
    public double getTemperature() {
        return this.temperature;
    }

    /**
     * gibt die Temperatur mit Offset zurück
     *
     * @return Temperatur mit Offset
     */
    public double getTemperatureWithOffset() {
        return this.temperature + this.offset;
    }

    /**
     * setzt die Temperatur
     *
     * @param temperature Temperatur
     */
    public void setTemperature(double temperature) {

        this.temperature = temperature;
        setChangedData();
    }

    /**
     * fügt eine neue Temperatur ein
     *
     * @param temperature Temperatur
     */
    public void pushTemperature(double temperature) {

        setTemperature(temperature);
        setLastPushTime(LocalDateTime.now());
    }

    /**
     * gibt das Offset zurück
     *
     * @return Offset
     */
    public double getOffset() {
        return this.offset;
    }

    /**
     * setzt das Offset
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
