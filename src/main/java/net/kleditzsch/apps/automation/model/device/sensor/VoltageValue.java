package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Spannung
 */
public class VoltageValue extends SensorValue {

    private Type type = Type.SENSORVALUE_VOLTAGE;

    /**
     * Spannung in mV
     */
    private double voltage = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VoltageValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public VoltageValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Spannung zurück
     *
     * @return Spannung
     */
    public double getVoltage() {
        return voltage;
    }

    /**
     * setzt die Spannung
     *
     * @param voltage Spannung
     */
    public void setVoltage(double voltage) {

        this.voltage = voltage;
        setChangedData();
    }

    /**
     * fügt die aktuelle Spannung hinzu
     *
     * @param voltage Spannung
     */
    public void pushVoltage(double voltage) {

        setVoltage(voltage);
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
