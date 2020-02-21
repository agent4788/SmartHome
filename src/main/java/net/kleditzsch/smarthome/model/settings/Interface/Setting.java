package net.kleditzsch.smarthome.model.settings.Interface;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.model.base.ID;

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
     * eindeutige ID des Elements
     */
    private ID id = null;

    /**
     * Name des Elements
     */
    private Settings name;

    /**
     * gibt an ob Daten seit der letzten Speicherung geändert wurden
     */
    private boolean changedData = false;

    /**
     * gibt die ID des Elements zurück
     *
     * @return ID
     */
    public ID getId() {
        return id;
    }

    /**
     * setzt die ID des ELements
     *
     * @param id ID Objekt
     */
    public void setId(ID id) {

        Preconditions.checkNotNull(id);
        this.id = id;
        setChangedData();
    }

    /**
     * gibt den Namen des Elements zurück
     *
     * @return Name des Elements
     */
    public Settings getName() {
        return name;
    }

    /**
     * setzt den Namen des Elements
     *
     * @param name Name des Elements
     */
    public void setName(Settings name) {

        Preconditions.checkNotNull(name);
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
