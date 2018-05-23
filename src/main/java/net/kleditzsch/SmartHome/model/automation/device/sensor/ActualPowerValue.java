package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Aktueller Energieverbrauch
 */
public class ActualPowerValue extends SensorValue {

    private Type type = Type.SENSORVALUE_ACTUAL_POWER;

    /**
     * Momentaner Energieverbrauch in Milliwatt
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
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
