package net.kleditzsch.apps.automation.model.device.sensor.Interface;

import net.kleditzsch.SmartHome.model.base.ID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Viritueller Sensor
 */
public abstract class VirtualSensorValue extends SensorValue {

    /**
     * überwachte Sensoren
     */
    private final Set<String> sensorValues = new HashSet<>();

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualSensorValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt die Liste der überwachten Sensoren zurück
     *
     * @return Liste der überwachten Sensoren
     */
    public Set<String> getSensorValues() {

        setChangedData();
        return sensorValues;
    }

    /**
     * ermittelt aus den Sensorwerten die Statistischen Werte
     *
     * @param sensorValues Sensorwerte
     */
    public abstract void processValues(List<SensorValue> sensorValues);
}
