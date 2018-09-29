package net.kleditzsch.SmartHome.model.global.settings;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.model.global.settings.Interface.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Listen Einstellung
 */
public class ListSetting extends Setting {

    /**
     * Wert der Einstellung
     */
    private List<String> value = new ArrayList<>();

    /**
     * Standard der Einstellung
     */
    private List<String> defaultValue = new ArrayList<>();

    /**
     * Typ
     */
    private Type type = Type.LIST;

    public ListSetting() {
    }

    /**
     * @param name Name der Einstellung
     */
    public ListSetting(String name) {
        setName(name);
    }

    /**
     * gibt die Liste der Zeichenketten zurück
     *
     * @return Liste der Zeichenketten
     */
    public List<String> getValue() {

        setChangedData();
        return value;
    }

    /**
     * setzt die Werteliste
     *
     * @param value Werteliste
     */
    public void setValue(List<String> value) {

        Preconditions.checkNotNull(value);
        this.value = value;
        setChangedData();
    }

    /**
     * gibt die Standard Liste der Zeichenketten zurück
     *
     * @return Standard Liste der Zeichenketten
     */
    public List<String> getDefaultValue() {

        setChangedData();
        return defaultValue;
    }

    /**
     * setzt die Liste der Standardwerte
     *
     * @param defaultValue Standardwerte
     */
    public void setDefaultValue(List<String> defaultValue) {

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
