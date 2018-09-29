package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Spannung
 */
public class VoltageValue extends SensorValue {

    private Type type = Type.SENSORVALUE_VOLTAGE;

    /**
     * Spannung in mV
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 100_000_000, errorCode = 10205, message = "Ungültiger Sensorwert")
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
