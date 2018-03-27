package net.kleditzsch.SmartHome.model.automation.device.sensor.virtual;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.LightIntensityValue;

import java.util.List;

/**
 * Virtuelle Lichtstärke
 */
public class VirtualLightIntensityValue extends VirtualSensorValue {

    private Type type = Type.VIRTUALSENSORVALUE_LIGHT_INTENSITY;

    /**
     * Statistische Werte
     */
    private double average = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualLightIntensityValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * ermittelt aus den Sensorwerten die Statistischen Werte
     *
     * @param sensorValues Sensorwerte
     */
    @Override
    public void processValues(List<SensorValue> sensorValues) {

        int count = 0;
        double sum = 0;
        for(SensorValue sensorValue : sensorValues) {

            if(sensorValue instanceof LightIntensityValue) {

                LightIntensityValue lightIntensityValue = (LightIntensityValue) sensorValue;
                sum += lightIntensityValue.getLightIntensity();
                count++;
            }
        }

        this.average = sum / count;
    }

    /**
     * gibt den Mittelwert zurück
     *
     * @return Mittelwert
     */
    public double getAverage() {
        return average;
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
