package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Wassermenge
 */
public class WaterAmountValue extends SensorValue {

    private Type type = Type.SENSORVALUE_WATER_AMOUNT;

    /**
     * Wassermenge in Liter
     */
    private double waterAmount = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public WaterAmountValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public WaterAmountValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Wassermenge zurück
     *
     * @return Wassermenge
     */
    public double getWaterAmount() {
        return waterAmount;
    }

    /**
     * setzt die Wassermenge
     *
     * @param waterAmount Wassermenge
     */
    public void setWaterAmount(double waterAmount) {

        this.waterAmount = waterAmount;
        setChangedData();
    }

    /**
     * addiert eine Wassermenge hinzu
     *
     * @param waterAmount Wassermenge
     */
    public void pushWaterAmount(double waterAmount) {

        setWaterAmount(waterAmount);
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
