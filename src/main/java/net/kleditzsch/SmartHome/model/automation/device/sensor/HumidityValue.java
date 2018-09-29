package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Luftfeuchte
 */
public class HumidityValue extends SensorValue {

    private Type type = Type.SENSORVALUE_HUMIDITY;

    /**
     * Luftfeuchte in Prozent
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 100, errorCode = 10205, message = "Ungültiger Sensorwert")
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
