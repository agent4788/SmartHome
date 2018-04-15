package net.kleditzsch.SmartHome.model.automation.device;

import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.util.Optional;

/**
 * Automatisierungselement
 */
public abstract class AutomationElement extends Element {

    /**
     * Element Typen
     */
    public enum Type {

        @SerializedName("SWITCHABLE_AVM_SOCKET")
        SWITCHABLE_AVM_SOCKET,
        @SerializedName("SWITCHABLE_TPLINK_SOCKET")
        SWITCHABLE_TPLINK_SOCKET,
        @SerializedName("SWITCHABLE_OUTPUT")
        SWITCHABLE_OUTPUT,
        @SerializedName("SWITCHABLE_REBOOT_SHUTDOWN")
        SWITCHABLE_SCRIPT_SINGLE,
        @SerializedName("SWITCHABLE_SCRIPT_DOUBLE")
        SWITCHABLE_SCRIPT_DOUBLE,
        @SerializedName("SWITCHABLE_WAKE_ON_LAN")
        SWITCHABLE_WAKE_ON_LAN,

        @SerializedName("SENSORVALUE_INPUT")
        SENSORVALUE_INPUT,
        @SerializedName("SENSORVALUE_USER_AT_HOME")
        SENSORVALUE_USER_AT_HOME,
        @SerializedName("SENSORVALUE_LIVE_BIT")
        SENSORVALUE_LIVE_BIT,
        @SerializedName("SENSORVALUE_ACTUAL_POWER")
        SENSORVALUE_ACTUAL_POWER,
        @SerializedName("SENSORVALUE_AIR_PRESSURE")
        SENSORVALUE_AIR_PRESSURE,
        @SerializedName("SENSORVALUE_ALTITUDE")
        SENSORVALUE_ALTITUDE,
        @SerializedName("SENSORVALUE_BATTERY_LEVEL")
        SENSORVALUE_BATTERY_LEVEL,
        @SerializedName("SENSORVALUE_DISTANCE")
        SENSORVALUE_DISTANCE,
        @SerializedName("SENSORVALUE_DURATION")
        SENSORVALUE_DURATION,
        @SerializedName("SENSORVALUE_ENERGY")
        SENSORVALUE_ENERGY,
        @SerializedName("SENSORVALUE_GAS_AMOUNT")
        SENSORVALUE_GAS_AMOUNT,
        @SerializedName("SENSORVALUE_HUMIDITY")
        SENSORVALUE_HUMIDITY,
        @SerializedName("SENSORVALUE_LIGHT_INTENSITY")
        SENSORVALUE_LIGHT_INTENSITY,
        @SerializedName("SENSORVALUE_MOISTURE")
        SENSORVALUE_MOISTURE,
        @SerializedName("SENSORVALUE_STRING")
        SENSORVALUE_STRING,
        @SerializedName("SENSORVALUE_TEMPERATURE")
        SENSORVALUE_TEMPERATURE,
        @SerializedName("SENSORVALUE_WATER_AMOUNT")
        SENSORVALUE_WATER_AMOUNT,
        @SerializedName("SENSORVALUE_VOLTAGE")
        SENSORVALUE_VOLTAGE,
        @SerializedName("SENSORVALUE_CURRENT")
        SENSORVALUE_CURRENT,

        @SerializedName("VIRTUALSENSORVALUE_ACTUAL_POWER")
        VIRTUALSENSORVALUE_ACTUAL_POWER,
        @SerializedName("VIRTUALSENSORVALUE_ENERGY")
        VIRTUALSENSORVALUE_ENERGY,
        @SerializedName("VIRTUALSENSORVALUE_GAS_AMOUNT")
        VIRTUALSENSORVALUE_GAS_AMOUNT,
        @SerializedName("VIRTUALSENSORVALUE_LIGHT_INTENSITY")
        VIRTUALSENSORVALUE_LIGHT_INTENSITY,
        @SerializedName("VIRTUALSENSORVALUE_WATER_AMOUNT")
        VIRTUALSENSORVALUE_WATER_AMOUNT,
        @SerializedName("VIRTUALSENSORVALUE_TEMPERATURE")
        VIRTUALSENSORVALUE_TEMPERATURE
    }

    /**
     * Status
     */
    public enum State {

        @SerializedName("ON")
        ON,
        @SerializedName("OFF")
        OFF
    }

    /**
     * deaktiviert
     */
    private boolean disabled = false;

    /**
     * Kommentar
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    private String comment = "";

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
    }

    /**
     * gibt den Kommentar zurück
     *
     * @return Kommentar
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * setzt den Kommentar
     *
     * @param comment Kommentar
     */
    public void setComment(String comment) {

        this.comment = comment;
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    public abstract Type getType();
}
