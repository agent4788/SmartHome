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
    private BigInteger conterValue = new BigInteger("0");

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
    public BigInteger getConterValue() {
        return conterValue;
    }

    /**
     * setzt den aktuellen Zählerwert
     *
     * @param conterValue Zählerwert
     */
    public void setConterValue(BigInteger conterValue) {
        this.conterValue = conterValue;
    }

    /**
     * fügt einen neuen Zählerwert hinzu
     *
     * @param conterValue Zählerwert
     */
    public void pushConterValue(BigInteger conterValue) {

        setConterValue(conterValue);
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
