package net.kleditzsch.SmartHome.model.automation.global;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

/**
 * Befehlsobjekt
 */
public class SwitchCommand {

    /**
     * ID des schaltbaren Elements
     */
    private ID switchableId;

    /**
     * Befehl
     */
    private SwitchCommands command;

    /**
     * @param switchableId ID des schaltbaren Elements
     * @param command Befehl
     */
    public SwitchCommand(ID switchableId, SwitchCommands command) {
        this.switchableId = switchableId;
        this.command = command;
    }

    /**
     * gibt die ID des schaltbaren Elements zurück
     *
     * @return ID des schaltbaren Elements
     */
    public ID getSwitchableId() {
        return switchableId;
    }

    /**
     * setzt die ID des schaltbaren Elements
     *
     * @param switchableId ID des schaltbaren Elements
     */
    public void setSwitchableId(ID switchableId) {
        this.switchableId = switchableId;
    }

    /**
     * gibt den Befehl zurück
     *
     * @return Befehl
     */
    public SwitchCommands getCommand() {
        return command;
    }

    /**
     * setzt den Befehl
     *
     * @param command Befehl
     */
    public void setCommand(SwitchCommands command) {
        this.command = command;
    }
}
