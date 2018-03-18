package net.kleditzsch.SmartHome.util.settings;

import net.kleditzsch.SmartHome.util.settings.Interface.Setting;

/**
 * Boolean Setting
 */
public class BooleanSetting implements Setting {

    /**
     * Name der Einstellung
     */
    private String name;

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
    public BooleanSetting(String name, boolean value, boolean defaultValue) {
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
