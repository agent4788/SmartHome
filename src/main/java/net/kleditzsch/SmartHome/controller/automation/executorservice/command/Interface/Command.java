package net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface;

/**
 * Standard Befehl
 */
public interface Command {

    public enum SWITCH_COMMAND {

        ON,
        OFF,
        TOGGLE,
        UPDATE,
        UPDATE_SENSOR
    }
}
