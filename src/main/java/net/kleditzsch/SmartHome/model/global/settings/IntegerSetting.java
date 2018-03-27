package net.kleditzsch.SmartHome.model.global.settings;

import net.kleditzsch.SmartHome.model.global.settings.Interface.Setting;

/**
 * Integer Einstellung
 */
public class IntegerSetting extends Setting {

    /**
     * Wert der Einstellung
     */
    private int value;

    /**
     * Standard der Einstellung
     */
    private int defaultValue;

    /**
     * Typ
     */
    private Setting.Type type = Type.INTEGER;

    public IntegerSetting() {}

    /**
     * @param name
     * @param value
     * @param defaultValue
     */
    public IntegerSetting(String name, int value, int defaultValue) {

        setName(name);
        setValue(value);
        setDefaultValue(defaultValue);
    }

    /**
     * gibt den Wert der Einstellung zurück
     *
     * @return Wert
     */
    public int getValue() {
        return value;
    }

    /**
     * setzt den Wert der Einstellung
     *
     * @param value Wert
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * gibt den Standardwert zurück
     *
     * @return Standardwert
     */
    public int getDefaultValue() {
        return defaultValue;
    }

    /**
     * setzt den Standardwert
     *
     * @param defaultValue Standardwert
     */
    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
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
