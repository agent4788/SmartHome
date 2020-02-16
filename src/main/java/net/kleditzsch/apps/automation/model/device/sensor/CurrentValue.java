package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Strom
 */
public class CurrentValue extends SensorValue {

    private Type type = Type.SENSORVALUE_CURRENT;

    /**
     * Strom in mA
     */
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
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public CurrentValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
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
        setChangedData();
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
