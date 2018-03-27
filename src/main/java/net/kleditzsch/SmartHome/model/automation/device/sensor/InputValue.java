package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Eingang
 */
public class InputValue extends SensorValue {

    private Type type = Type.SENSORVALUE_INPUT;

    /**
     * Status
     */
    private boolean state = false;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public InputValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt an ob der Eingang aktiviert ist
     *
     * @return true wenn aktiviert
     */
    public boolean getState() {
        return state;
    }

    /**
     * setzt den Status
     *
     * @param state Status
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * fügt einen neuen Status ein
     *
     * @param state Status
     */
    public void pushState(boolean state) {

        setState(state);
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
