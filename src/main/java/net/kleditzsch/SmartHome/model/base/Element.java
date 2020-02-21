package net.kleditzsch.SmartHome.model.base;

import com.google.common.base.Preconditions;

import java.util.Optional;

/**
 * Basis Element
 */
public abstract class Element {

    /**
     * eindeutige ID des Elements
     */
    private ID id = null;

    /**
     * Name des Elements
     */
    private String name = "";

    /**
     * Beschreibund des Elements
     */
    private String description = "";

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
    public String getName() {
        return name;
    }

    /**
     * setzt den Namen des Elements
     *
     * @param name Name des Elements
     */
    public void setName(String name) {

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(name.length() >= 1 && name.length() <= 50);
        this.name = name;
        setChangedData();
    }

    /**
     * gibt die Beschreibung des Elements zurück
     *
     * @return Beschreibung des Elements
     */
    public Optional<String> getDescription() {

        return Optional.ofNullable(description);
    }

    /**
     * setzt die Beschreibung des Elements
     *
     * @param description Beschreibung des Elements
     */
    public void setDescription(String description) {

        Preconditions.checkNotNull(description);
        Preconditions.checkArgument(description.length() <= 250);
        this.description = description;
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
}
