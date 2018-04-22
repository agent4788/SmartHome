package net.kleditzsch.SmartHome.model.automation.room.Interface;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

/**
 * Befehlsobjekt
 */
public class SwitchCommand {

    private ID switchableId;
    private SwitchCommands command;

    public SwitchCommand(ID switchableId, SwitchCommands command) {
        this.switchableId = switchableId;
        this.command = command;
    }

    public ID getSwitchableId() {
        return switchableId;
    }

    public void setSwitchableId(ID switchableId) {
        this.switchableId = switchableId;
    }

    public SwitchCommands getCommand() {
        return command;
    }

    public void setCommand(SwitchCommands command) {
        this.command = command;
    }
}
