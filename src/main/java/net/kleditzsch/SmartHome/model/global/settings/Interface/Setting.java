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

        @SerializedName("STRING")
        STRING,
        @SerializedName("INTEGER")
        INTEGER,
        @SerializedName("DOUBLE")
        DOUBLE,
        @SerializedName("BOOLEAN")
        BOOLEAN,
        @SerializedName("LIST")
        LIST
    }

    /**
     * Name der Einstellung
     */
    private String name;

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
     * gibt den Typ der Einstellung zurück
     *
     * @return Typ
     */
    public abstract Type getType();
}
