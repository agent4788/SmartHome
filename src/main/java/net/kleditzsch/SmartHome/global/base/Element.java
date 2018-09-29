package net.kleditzsch.SmartHome.global.base;

import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidatePattern;

import java.util.Optional;

/**
 * Basis Element
 *
 * @author Oliver Kleditzsch
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

        if(id != null) {

            this.id = id;
        } else {

            throw new IllegalArgumentException("Ungültige ID");
        }
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

        if(name != null && name.trim().length() >= 3) {

            this.name = name;
            setChangedData();
        } else {

            throw new IllegalArgumentException("Ungültiger Name");
        }
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
