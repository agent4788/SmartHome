package net.kleditzsch.SmartHome.model.settings;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.model.settings.Interface.Setting;

/**
 * String Einstellung
 */
public class StringSetting extends Setting {

    /**
     * Wert der Einstellung
     */
    private String value;

    /**
     * Standard der Einstellung
     */
    private String defaultValue;

    /**
     * Typ
     */
    private Type type = Type.STRING;

    public StringSetting() {}

    /**
     * @param name
     * @param value
     * @param defaultValue
     */
    public StringSetting(String name, String value, String defaultValue) {

        setName(name);
        setValue(value);
        setDefaultValue(defaultValue);
    }

    /**
     * gibt den Wert der Einstellung zurück
     *
     * @return Wert
     */
    public String getValue() {
        return value;
    }

    /**
     * setzt den Wert der Einstellung
     *
     * @param value Wert
     */
    public void setValue(String value) {

        Preconditions.checkNotNull(value);
        this.value = value;
        setChangedData();
    }

    /**
     * gibt den Standardwert zurück
     *
     * @return Standardwert
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * setzt den Standardwert
     *
     * @param defaultValue Standardwert
     */
    public void setDefaultValue(String defaultValue) {

        Preconditions.checkNotNull(defaultValue);
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
