package net.kleditzsch.applications.automation.model.device.sensor;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Zähler
 */
public class CounterValue extends SensorValue {

    private Type type = Type.SENSORVALUE_COUNTER;

    /**
     * Zählerwert
     */
    private BigInteger counterValue = new BigInteger("0");

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public CounterValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public CounterValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt den aktuellen Zählerwert zurück
     *
     * @return Zählerwert
     */
    public BigInteger getCounterValue() {
        return counterValue;
    }

    /**
     * setzt den aktuellen Zählerwert
     *
     * @param counterValue Zählerwert
     */
    public void setCounterValue(BigInteger counterValue) {

        this.counterValue = counterValue;
        setChangedData();
    }

    /**
     * fügt einen neuen Zählerwert hinzu
     *
     * @param conterValue Zählerwert
     */
    public void pushConterValue(BigInteger conterValue) {

        setCounterValue(conterValue);
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
