package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;

/**
 * Script mit nur einem Befehl
 */
public class ScriptSingle extends SingleSwitchable {

    private Type type = Type.SWITCHABLE_SCRIPT_SINGLE;

    /**
     * Befehle
     */
    private String command;

    public ScriptSingle() {}

    /**
     * @param id ID
     * @param name Name
     */
    public ScriptSingle(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt den Einschaltbefehl zurück
     *
     * @return Einschaltbefehl
     */
    public String getCommand() {
        return command;
    }

    /**
     * setzt den Einschaltbefehl
     *
     * @param command Einschaltbefehl
     */
    public void setCommand(String command) {

        Preconditions.checkNotNull(command);
        Preconditions.checkArgument(command.length() >= 3, "Ungültiger Befehl %s", command);
        this.command = command;
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
