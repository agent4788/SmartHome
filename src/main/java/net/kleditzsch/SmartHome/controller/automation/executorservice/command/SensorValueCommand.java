package net.kleditzsch.SmartHome.controller.automation.executorservice.command;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

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
