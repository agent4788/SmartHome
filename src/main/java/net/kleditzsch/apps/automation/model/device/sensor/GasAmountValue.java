package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Gasmenge
 */
public class GasAmountValue extends SensorValue {

    private Type type = Type.SENSORVALUE_GAS_AMOUNT;

    /**
     * Gasmenge in Liter
     */
    private double gasAmount = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public GasAmountValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public GasAmountValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Gasmenge zurück
     *
     * @return Gasmenge
     */
    public double getGasAmount() {
        return gasAmount;
    }

    /**
     * setzt die Gasmenge
     *
     * @param gasAmount Gasmenge
     */
    public void setGasAmount(double gasAmount) {

        this.gasAmount = gasAmount;
        setChangedData();
    }

    /**
     * fügt eine Gasmenge hinzu
     *
     * @param gasAmount Gasmenge
     */
    public void pushGasAmount(double gasAmount) {

        setGasAmount(gasAmount);
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
