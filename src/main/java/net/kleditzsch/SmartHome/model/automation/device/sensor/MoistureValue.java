package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Feuchtigkeit
 */
public class MoistureValue extends SensorValue {

    private Type type = Type.SENSORVALUE_MOISTURE;

    /**
     * Feuchtigkeit in %
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 100, errorCode = 10205, message = "Ungültiger Sensorwert")
    private double moisture = 0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public MoistureValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt die Feuchtigkeit zurück
     *
     * @return Feuchtigkeit
     */
    public double getMoisture() {
        return moisture;
    }

    /**
     * setzt die Feuchtigkeit
     *
     * @param moisture Feuchtigkeit
     */
    public void setMoisture(double moisture) {

        this.moisture = moisture;
    }

    /**
     * fügt die Feuchtigkeit hinzu
     *
     * @param moisture Feuchtigkeit
     */
    public void pushMoisture(double moisture) {

        setMoisture(moisture);
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
