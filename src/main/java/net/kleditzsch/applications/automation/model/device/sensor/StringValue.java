package net.kleditzsch.applications.automation.model.device.sensor;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Zeichenkette
 */
public class StringValue extends SensorValue {

    private Type type = Type.SENSORVALUE_STRING;

    /**
     * Zeichenkette
     */
    protected String string = "";

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public StringValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public StringValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * setzt die Zeichenkette
     *
     * @return Zeichenkette
     */
    public String getString() {
        return string;
    }

    /**
     * gibt die Zeichenkette zurück
     *
     * @param string Zeichenkette
     */
    public void setString(String string) {

        this.string = string;
        setChangedData();
    }

    /**
     * fügt die Zeichenkette hinzu
     *
     * @param string Zeichenkette
     */
    public void pushString(String string) {

        setString(string);
        setLastPushTime(LocalDateTime.now());
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
