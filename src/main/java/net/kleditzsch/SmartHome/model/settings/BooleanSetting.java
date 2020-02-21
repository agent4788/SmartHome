package net.kleditzsch.SmartHome.model.settings;

import net.kleditzsch.SmartHome.model.settings.Interface.Setting;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;

/**
 * Boolean Setting
 */
public class BooleanSetting extends Setting {

    /**
     * Wert der Einstellung
     */
    private boolean value;

    /**
     * Standard der Einstellung
     */
    private boolean defaultValue;

    /**
     * Typ
     */
    private Type type = Type.BOOLEAN;

    public BooleanSetting() {}

    /**
     * @param name
     * @param value
     * @param defaultValue
     */
    public BooleanSetting(Settings name, boolean value, boolean defaultValue) {

        setName(name);
        setValue(value);
        setDefaultValue(defaultValue);
    }

    /**
     * gibt den Wert der Einstellung zurück
     *
     * @return Wert
     */
    public boolean getValue() {
        return value;
    }

    /**
     * setzt den Wert der Einstellung
     *
     * @param value Wert
     */
    public void setValue(boolean value) {

        this.value = value;
        setChangedData();
    }

    /**
     * gibt den Standardwert zurück
     *
     * @return Standardwert
     */
    public boolean getDefaultValue() {
        return defaultValue;
    }

    /**
     * setzt den Standardwert
     *
     * @param defaultValue Standardwert
     */
    public void setDefaultValue(boolean defaultValue) {

        this.defaultValue = defaultValue;
        setChangedData();
    }

    /**
     * gibt den Typ der Einstellung zurück
     *
     * @return Typ
     */
    @Override
    public Type getType() {
        return type;
    }
}
