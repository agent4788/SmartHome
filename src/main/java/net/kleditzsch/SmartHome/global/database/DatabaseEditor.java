package net.kleditzsch.SmartHome.global.database;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Datenbank Verwaltung
 */
public interface DatabaseEditor {

    /**
     * lädt die Elemente aus der Datenbank
     */
    void load();

    /**
     * speichert die Elemente in der Datenbank
     */
    void dump();

    /**
     * gibt das Lockobjekt zurück
     *
     * @return Lockobjekt
     */
    ReentrantReadWriteLock getReadWriteLock();

    /**
     * gibt ein Lock Objekt zum erlangen des Leselock zurück
     *
     * @return Lock Objekt
     */
    ReentrantReadWriteLock.ReadLock readLock();

    /**
     * gibt ein Lock Objekt zum erlangen des Schreiblock zurück
     *
     * @return Lock Objekt
     */
    ReentrantReadWriteLock.WriteLock writeLock();
}
