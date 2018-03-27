package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Luftdruck
 */
public class AirPressureValue extends SensorValue {

    private Type type = Type.SENSORVALUE_AIR_PRESSURE;

    /**
     * Luftdruck in hPa
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 500, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 1500, errorCode = 10205, message = "Ungültiger Sensorwert")
    private double airPressure = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public AirPressureValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt den Luftdruck zurück
     *
     * @return Luftdruck
     */
    double getAirPressure() {
        return this.airPressure;
    }

    /**
     * setzt den Luftdruck
     *
     * @param airPressure Luftdruck
     */
    public void setAirPressure(double airPressure) {

        this.airPressure = airPressure;
    }

    /**
     * füght den Luftdruck hinzu
     *
     * @param airPressure Luftdruck
     */
    void pushAirPressure(double airPressure) {

        setAirPressure(airPressure);
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
