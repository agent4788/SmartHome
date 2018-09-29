package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Gasmenge
 */
public class GasAmountValue extends SensorValue {

    private Type type = Type.SENSORVALUE_GAS_AMOUNT;

    /**
     * Gasmenge in Liter
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = Double.MAX_VALUE, errorCode = 10205, message = "Ungültiger Sensorwert")
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
