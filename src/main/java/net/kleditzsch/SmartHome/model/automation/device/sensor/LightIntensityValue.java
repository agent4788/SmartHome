package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Lichtstärke
 */
public class LightIntensityValue extends SensorValue {

    private Type type = Type.SENSORVALUE_LIGHT_INTENSITY;

    /**
     * Lichtstärke in Prozent
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = 100, errorCode = 10205, message = "Ungültiger Sensorwert")
    private double lightIntensity = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public LightIntensityValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt die Lichtstärke zurück
     *
     * @return Lichtstärke
     */
    public double getLightIntensity() {
        return lightIntensity;
    }

    /**
     * setzt die Lichtstärke
     *
     * @param lightIntensity Lichtstärke
     */
    public void setLightIntensity(double lightIntensity) {

        this.lightIntensity = lightIntensity;
        setChangedData();
    }

    /**
     * fügt die Lichtstärke hinzu
     *
     * @param lightIntensity Lichtstärke
     */
    public void pushLightIntensity(double lightIntensity) {

        setLightIntensity(lightIntensity);
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
