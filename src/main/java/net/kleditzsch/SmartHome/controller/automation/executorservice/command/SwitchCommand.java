package net.kleditzsch.SmartHome.controller.automation.executorservice.command;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;

/**
 * Schaltbefehl für Doppelschaltelement
 */
public class SwitchCommand implements Command {

    /**
     * Schaltbares Element
     */
    private Switchable switchable;

    /**
     * Ziel Status
     */
    private SWITCH_COMMAND switchCommand;

    /**
     * @param switchable Schaltbares Element
     * @param targetState Ziel Status
     */
    public SwitchCommand(Switchable switchable, SWITCH_COMMAND targetState) {
        this.switchable = switchable;
        this.switchCommand = targetState;
    }

    /**
     * gibt das schaltbare Element zurück
     *
     * @return Schaltbares Element
     */
    public Switchable getSwitchable() {
        return switchable;
    }

    /**
     * gibt den angeforderten Status zurück
     *
     * @return Ziel Status
     */
    public SWITCH_COMMAND getSwitchCommand() {
        return switchCommand;
    }
}
