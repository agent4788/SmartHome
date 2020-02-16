package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Luftfeuchte
 */
public class HumidityValue extends SensorValue {

    private Type type = Type.SENSORVALUE_HUMIDITY;

    /**
     * Luftfeuchte in Prozent
     */
    private double humidity = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public HumidityValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public HumidityValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt die Luftfeuchte zurück
     *
     * @return Luftfeuchte
     */
    public double getHumidity() {
        return humidity;
    }

    /**
     * setzt die Luftfeuchte
     *
     * @param humidity Luftfeuchte
     */
    public void setHumidity(double humidity) {

        this.humidity = humidity;
        setChangedData();
    }

    /**
     * fügt die Luftfeuchte hinzu
     *
     * @param humidity Luftfeuchte
     */
    public void pushHumidity(double humidity) {

        setHumidity(humidity);
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
