package net.kleditzsch.SmartHome.util.settings;

import net.kleditzsch.SmartHome.util.settings.Interface.Setting;

/**
 * Double Einstellung
 *
 * @author Oliver Kleditzsch
 * @copyright Copyright (c) 2016, Oliver Kleditzsch
 */
public class DoubleSetting implements Setting {

    /**
     * Name der Einstellung
     */
    private String name;

    /**
     * Wert der Einstellung
     */
    private double value;

    /**
     * Standard der Einstellung
     */
    private double defaultValue;

    /**
     * Typ
     */
    private Type type = Type.DOUBLE;

    public DoubleSetting() {}

    /**
     * @param name
     * @param value
     * @param defaultValue
     */
    public DoubleSetting(String name, double value, double defaultValue) {
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
    public double getValue() {
        return value;
    }

    /**
     * setzt den Wert der Einstellung
     *
     * @param value Wert
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * gibt den Standardwert zurück
     *
     * @return Standardwert
     */
    public double getDefaultValue() {
        return defaultValue;
    }

    /**
     * setzt den Standardwert
     *
     * @param defaultValue Standardwert
     */
    public void setDefaultValue(double defaultValue) {
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
