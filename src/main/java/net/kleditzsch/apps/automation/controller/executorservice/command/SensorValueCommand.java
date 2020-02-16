package net.kleditzsch.apps.automation.controller.executorservice.command;

import com.google.common.base.Preconditions;
import net.kleditzsch.apps.automation.controller.executorservice.command.Interface.Command;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

/**
 * Befehl zum aktualisieren von Sensordaten
 */
public class SensorValueCommand implements Command {

    /**
     * Schaltbares Element
     */
    private SensorValue sensorValue;

    /**
     * @param sensorValue Sensorwert
     */
    public SensorValueCommand(SensorValue sensorValue) {

        Preconditions.checkNotNull(sensorValue);
        this.sensorValue = sensorValue;
    }

    /**
     * gibt den Sensorwert zurück
     *
     * @return Sensorwert
     */
    public SensorValue getSensorValue() {
        return sensorValue;
    }
}
