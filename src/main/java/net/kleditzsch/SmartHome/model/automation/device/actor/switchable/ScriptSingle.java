package net.kleditzsch.SmartHome.model.automation.device.actor.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.SingleSwitchable;

import java.util.ArrayList;
import java.util.List;

/**
 * Script mit nur einem Befehl
 */
public class ScriptSingle extends SingleSwitchable {

    private Type type = Type.SWITCHABLE_SCRIPT_SINGLE;

    /**
     * Befehle
     */
    private List<String> command = new ArrayList<>();

    /**
     * Pfad zum Arbeitsordner
     */
    private String workingDir;

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
    public List<String> getCommand() {

        setChangedData();
        return command;
    }

    /**
     * setzt den Einschaltbefehl
     *
     * @param command Einschaltbefehl
     */
    public void setCommand(List<String> command) {

        Preconditions.checkNotNull(command);
        Preconditions.checkArgument(command.size() >= 1, "Ungültiger Befehl %s", command);
        this.command = command;
        setChangedData();
    }

    /**
     * gibt den Pfad zum Arbeitsordner zurück
     *
     * @return Pfad zum Arbeitsordner
     */
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * setzt den Pfad zum Arbeitsordner
     *
     * @param workingDir Pfad zum Arbeitsordner
     */
    public void setWorkingDir(String workingDir) {

        this.workingDir = workingDir;
        setChangedData();
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
