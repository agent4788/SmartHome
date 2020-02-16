package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Feuchtigkeit
 */
public class MoistureValue extends SensorValue {

    private Type type = Type.SENSORVALUE_MOISTURE;

    /**
     * Feuchtigkeit in %
     */
    private double moisture = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public MoistureValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public MoistureValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
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
        setChangedData();
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
