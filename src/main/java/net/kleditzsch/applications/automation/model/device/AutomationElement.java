package net.kleditzsch.applications.automation.model.device;

import net.kleditzsch.smarthome.model.base.Element;

/**
 * Automatisierungselement
 */
public abstract class AutomationElement extends Element {

    /**
     * Element Typen
     */
    public enum Type {

        SWITCHABLE_AVM_SOCKET,
        SWITCHABLE_TPLINK_SOCKET,
        SWITCHABLE_SCRIPT_SINGLE,
        SWITCHABLE_SCRIPT_DOUBLE,
        SWITCHABLE_WAKE_ON_LAN,
        SWITCHABLE_MQTT_SINGLE,
        SWITCHABLE_MQTT_DOUBLE,

        SHUTTER_MQTT,

        SENSORVALUE_INPUT,
        SENSORVALUE_USER_AT_HOME,
        SENSORVALUE_LIVE_BIT,
        SENSORVALUE_ACTUAL_POWER,
        SENSORVALUE_AIR_PRESSURE,
        SENSORVALUE_ALTITUDE,
        SENSORVALUE_BATTERY_LEVEL,
        SENSORVALUE_DISTANCE,
        SENSORVALUE_DURATION,
        SENSORVALUE_ENERGY,
        SENSORVALUE_GAS_AMOUNT,
        SENSORVALUE_HUMIDITY,
        SENSORVALUE_LIGHT_INTENSITY,
        SENSORVALUE_MOISTURE,
        SENSORVALUE_STRING,
        SENSORVALUE_TEMPERATURE,
        SENSORVALUE_WATER_AMOUNT,
        SENSORVALUE_VOLTAGE,
        SENSORVALUE_CURRENT,
        SENSORVALUE_COUNTER,
        SENSORVALUE_BI_STATE,

        VIRTUALSENSORVALUE_ACTUAL_POWER,
        VIRTUALSENSORVALUE_ENERGY,
        VIRTUALSENSORVALUE_GAS_AMOUNT,
        VIRTUALSENSORVALUE_LIGHT_INTENSITY,
        VIRTUALSENSORVALUE_WATER_AMOUNT,
        VIRTUALSENSORVALUE_TEMPERATURE
    }

    /**
     * Status
     */
    public enum State {

        ON,
        OFF
    }

    /**
     * deaktiviert
     */
    private boolean disabled = false;

    /**
     * gibt an ob das ELement deaktiviert ist
     *
     * @return true wenn deaktiviert
     */
    public boolean isDisabled() {
        return this.disabled;
    }

    /**
     * aktiviert/deaktiviert das Element
     *
     * @param disabled aktiviert/deaktiviert
     */
    public void setDisabled(boolean disabled) {

        this.disabled = disabled;
        setChangedData();
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    public abstract Type getType();
}
