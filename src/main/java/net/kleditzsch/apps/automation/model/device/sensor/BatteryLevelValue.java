package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Batterie Ladestand
 */
public class BatteryLevelValue extends SensorValue {

    private Type type = Type.SENSORVALUE_BATTERY_LEVEL;

    /**
     * Ladezustand in %
     */
    private double batteryLevel = 0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public BatteryLevelValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public BatteryLevelValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt den Ladezustand zurück
     *
     * @return Ladezustand
     */
    public double getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * setzt den Ladezustand
     *
     * @param batteryLevel Ladezustand
     */
    public void setBatteryLevel(double batteryLevel) {

        this.batteryLevel = batteryLevel;
        setChangedData();
    }

    /**
     * fügt den Ladezustand hinzu
     *
     * @param batteryLevel Ladezustand
     */
    public void pushBatteryLevel(double batteryLevel) {

        setBatteryLevel(batteryLevel);
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
