package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Lichtstärke
 */
public class LightIntensityValue extends SensorValue {

    private Type type = Type.SENSORVALUE_LIGHT_INTENSITY;

    /**
     * Lichtstärke in Prozent
     */
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
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public LightIntensityValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
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
