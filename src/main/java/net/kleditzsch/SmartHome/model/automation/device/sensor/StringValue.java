package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotEmpty;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Zeichenkette
 */
public class StringValue extends SensorValue {

    private Type type = Type.SENSORVALUE_STRING;

    /**
     * Zeichenkette
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateNotEmpty(errorCode = 10205, message = "Ungültiger Sensorwert")
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
