package net.kleditzsch.applications.automation.model.global;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.options.SwitchCommands;
import net.kleditzsch.applications.automation.model.global.Interface.Command;

/**
 * Befehlsobjekt
 */
public class SwitchCommand implements Command {

    private Type type = Type.COMMAND_SWITCH;

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
        setSwitchableId(switchableId);
        setCommand(command);
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

        Preconditions.checkNotNull(switchableId);
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

        Preconditions.checkNotNull(command);
        this.command = command;
    }

    /**
     * gibt den Typ des Befels zurück
     *
     * @return Typ des Befehles
     */
    public Type getType() {

        return type;
    }
}
