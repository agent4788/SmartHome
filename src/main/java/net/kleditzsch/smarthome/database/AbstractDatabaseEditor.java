package net.kleditzsch.smarthome.database;

import net.kleditzsch.smarthome.model.base.Element;
import net.kleditzsch.smarthome.model.base.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Datenbank Verwaltung
 */
public abstract class AbstractDatabaseEditor<T extends Element> implements DatabaseEditor {

    /**
     * Liste aller Elemente
     */
    private List<T> data = new ArrayList<T>(50);

    /**
     * Lock objekt
     */
    private volatile ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);


    /**
     * gibt die Liste mit allen Elementen zurück
     *
     * @return Liste mit Elementen
     */
    public List<T> getData() {

        return data;
    }

    /**
     * sucht nach einem Element mit der übergebenen ID
     *
     * @param id Hash
     * @return Element
     */
    public Optional<T> getById(ID id) {

        return data.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    /**
     * sucht nach einem Element mit der übergebenen ID
     *
     * @param id Hash
     * @return Element
     */
    public Optional<T> getById(String id) {

        return data.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    /**
     * sucht nach einem Element mit dem übergebenen Namen
     *
     * @param name Name
     * @return Element
     */
    public Optional<T> getByName(String name) {

        return data.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * gibt eine Liste mit den Elementen der IDs zurück
     *
     * @param idList Liste mit IDs
     * @return Liste mit Elementen
     */
    public List<T> getSublistByID(List<String> idList) {

        return data.stream()
                .filter(e -> idList.contains(e.getId().toString()))
                .collect(Collectors.toList());
    }

    /**
     * gibt eine Liste mit den Elementen der IDs zurück (gefiltert nach einem Typ)
     *
     * @param idList Liste mit IDs
     * @param type Typ filtern
     * @return Liste mit Elementen
     */
    public List<T> getSublistByID(List<String> idList, Class type) {

        return data.stream()
                .filter(e -> idList.contains(e.getId().toString()) && type.isInstance(e))
                .collect(Collectors.toList());
    }

    /**
     * gibt eine Liste mit den Elementen der IDs zurück
     *
     * @param names Liste mit IDs
     * @return Liste mit Elementen
     */
    public List<T> getSublistByName(List<String> names) {

        return data.stream()
                .filter(e -> names.contains(e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * gibt das Lockobjekt zurück
     *
     * @return Lockobjekt
     */
    @Override
    public ReentrantReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Leselock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.ReadLock readLock() {
        return readWriteLock.readLock();
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Schreiblock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.WriteLock writeLock() {
        return readWriteLock.writeLock();
    }
}
