package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Aktueller Energieverbrauch
 */
public class ActualPowerValue extends SensorValue {

    private Type type = Type.SENSORVALUE_ACTUAL_POWER;

    /**
     * Momentaner Energieverbrauch in Milliwatt
     */
    private double actualPower = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public ActualPowerValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public ActualPowerValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt den aktuellen Energieverbrauch zurück
     *
     * @return Energieverbrauch in Milliwatt
     */
    public double getActualPower() {
        return this.actualPower;
    }

    /**
     * setzt den aktuellen Energieverbrauch
     *
     * @param actualPower aktuellen Energieverbrauch
     */
    public void setActualPower(double actualPower) {

        this.actualPower = actualPower;
        setChangedData();
    }

    /**
     * fügt einen aktuellen Energieverbrauch
     *
     * @param actualPower Energieverbrauch in Milliwatt
     */
    public void pushActualPower(double actualPower) {

        setActualPower(actualPower);
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
