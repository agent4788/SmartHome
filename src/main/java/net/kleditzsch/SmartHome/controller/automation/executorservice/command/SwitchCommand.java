package net.kleditzsch.SmartHome.controller.automation.executorservice.command;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

/**
 * Schaltbefehl für Schaltelement
 */
public class SwitchCommand implements Command {

    /**
     * Schaltbares Element
     */
    private Switchable switchable;

    /**
     * Ziel Status
     */
    private SwitchCommands switchCommands;

    /**
     * @param switchable Schaltbares Element
     * @param targetState Ziel Status
     */
    public SwitchCommand(Switchable switchable, SwitchCommands targetState) {

        Preconditions.checkNotNull(switchable);
        Preconditions.checkNotNull(targetState);
        this.switchable = switchable;
        this.switchCommands = targetState;
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
    public SwitchCommands getSwitchCommands() {
        return switchCommands;
    }
}
