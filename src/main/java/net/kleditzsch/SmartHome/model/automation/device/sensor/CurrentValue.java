package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Strom
 */
public class CurrentValue extends SensorValue {

    private Type type = Type.SENSORVALUE_CURRENT;

    /**
     * Strom in mA
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 500_000, errorCode = 10205, message = "Ungültiger Sensorwert")
    private double current = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public CurrentValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt den Strom zurück
     *
     * @return Strom
     */
    public double getCurrent() {
        return current;
    }

    /**
     * setzt den Strom
     *
     * @param current Strom
     */
    public void setCurrent(double current) {

        this.current = current;
    }

    /**
     * fügt den aktuellen Strom hinzu
     *
     * @param current Strom
     */
    public void pushCurrent(double current) {

        setCurrent(current);
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
