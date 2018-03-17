package net.kleditzsch.SmartHome.global.base;

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
     * @param id ID
     * @param name Name
     */
    public Element(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * @param id ID
     * @param name Name
     * @param description Beschreibung
     */
    public Element(ID id, String name, String description) {

        setId(id);
        setName(name);
        setDescription(description);
    }

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

        if(description != null) {

            this.description = description;
        } else {

            throw new IllegalArgumentException("Ungültige Beschreibung");
        }
    }
}
