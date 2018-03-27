package net.kleditzsch.SmartHome.model.automation.device.sensor.Interface;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateSize;

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
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateSize(min = 1, errorCode = 10204, message = "Es muss mindestens ein Sensorwert vorhanden sein")
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
        return sensorValues;
    }

    /**
     * ermittelt aus den Sensorwerten die Statistischen Werte
     *
     * @param sensorValues Sensorwerte
     */
    public abstract void processValues(List<SensorValue> sensorValues);
}
