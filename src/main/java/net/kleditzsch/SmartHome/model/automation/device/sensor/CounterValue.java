package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

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
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
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
