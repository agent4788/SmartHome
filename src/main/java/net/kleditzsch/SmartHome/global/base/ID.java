package net.kleditzsch.SmartHome.global.base;

import java.util.UUID;

/**
 * Verwaltet eine eindeutige ID
 *
 * @author Oliver Kleditzsch
 */
public class ID {

    /**
     * ID
     */
    private UUID id;

    /**
     * erzeugt ein ID Objekt aus der übergebenen ID
     *
     * @param id ID
     * @return ID Objekt
     */
    public static ID of(String id) throws IllegalArgumentException {

        return new ID(UUID.fromString(id));
    }

    /**
     * erstellt eine neueeindeutige ID
     *
     * @return ID Objekt
     */
    public static ID create() {

        return new ID(UUID.randomUUID());
    }

    /**
     * @param id ID
     */
    private ID(UUID id) {

        this.id = id;
    }

    /**
     * gibt die ID als Zeichenkette zurück
     *
     * @return ID
     */
    public String get() {

        return this.id.toString();
    }

    /**
     * prüft ob beide ID Objekte gleich sind
     *
     * @param id ID Objekt
     */
    public boolean compareTo(ID id) {

        if((this.id.compareTo(UUID.fromString(id.get())) == 0)) {

            return true;
        }
        return false;
    }

    /**
     * prüft ob beide ID Objekte gleich sind
     *
     * @param id ID
     */
    public boolean compareTo(String id) {

        if((this.id.compareTo(UUID.fromString(id)) == 0)) {

            return true;
        }
        return false;
    }

    /**
     * gibt die ID als Zeichenkette zurück
     *
     * @return ID als Zeichenkette
     */
    @Override
    public String toString() {

        return get();
    }
}
