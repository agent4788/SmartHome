package net.kleditzsch.SmartHome.model.global.settings.Interface;

import com.google.gson.annotations.SerializedName;

/**
 * Einstellung
 */
public abstract class Setting {

    /**
     * Einstellungstypen
     */
    public enum Type {

        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
        LIST
    }

    /**
     * Name der Einstellung
     */
    private String name;

    /**
     * gibt an ob Daten seit der letzten Speicherung geändert wurden
     */
    private boolean changedData = false;

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
        setChangedData();
    }

    /**
     * gibt an ob die Daten des Objektes geändert wurden
     *
     * @return Daten geändert
     */
    public boolean isChangedData() {

        return changedData;
    }

    /**
     * Daten geäbdert
     */
    protected void setChangedData() {

        changedData = true;
    }

    /**
     * reset des Daten geändert Flags
     */
    public void resetChangedData() {

        changedData = false;
    }

    /**
     * gibt den Typ der Einstellung zurück
     *
     * @return Typ
     */
    public abstract Type getType();
}
