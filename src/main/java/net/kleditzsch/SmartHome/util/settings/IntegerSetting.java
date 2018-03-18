package net.kleditzsch.SmartHome.util.settings;

import net.kleditzsch.SmartHome.util.settings.Interface.Setting;

/**
 * Integer Einstellung
 *
 * @author Oliver Kleditzsch
 * @copyright Copyright (c) 2016, Oliver Kleditzsch
 */
public class IntegerSetting implements Setting {

    /**
     * Name der Einstellung
     */
    private String name;

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
    private Type type = Type.INTEGER;

    public IntegerSetting() {}

    /**
     * @param name
     * @param value
     * @param defaultValue
     */
    public IntegerSetting(String name, int value, int defaultValue) {
        this.name = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    /**
     * gibt den Namen der Einstellung zurück
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * setzt den Namen der Einstellung
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
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
