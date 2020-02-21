package net.kleditzsch.smarthome.model.settings;

import net.kleditzsch.smarthome.model.settings.Interface.Setting;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;

/**
 * Double Einstellung
 */
public class DoubleSetting extends Setting {

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
    public DoubleSetting(Settings name, double value, double defaultValue) {

        setName(name);
        setValue(value);
        setDefaultValue(defaultValue);
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
        setChangedData();
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
