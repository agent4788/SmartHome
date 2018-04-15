package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;

import java.util.List;

/**
 * Script mit an/aus Funktion
 */
public class ScriptDouble extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_SCRIPT_DOUBLE;

    /**
     * Befehle
     */
    private List<String> onCommand;
    private List<String> offCommand;

    /**
     * Pfad zum Arbeitsordner
     */
    private String workingDir;

    public ScriptDouble() {}

    /**
     * @param id ID
     * @param name Name
     */
    public ScriptDouble(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt den Ausschaltbefehl zurück
     *
     * @return Ausschaltbefehl
     */
    public List<String> getOffCommand() {
        return offCommand;
    }

    /**
     * setzt den Ausschaltbefehl
     *
     * @param offCommand Ausschaltbefehl
     */
    public void setOffCommand(List<String> offCommand) {

        Preconditions.checkNotNull(offCommand);
        Preconditions.checkArgument(offCommand.size() >= 1, "Ungültiger Befehl %s", offCommand);
        this.offCommand = offCommand;
    }

    /**
     * gibt den Einschaltbefehl zurück
     *
     * @return Einschaltbefehl
     */
    public List<String> getOnCommand() {
        return onCommand;
    }

    /**
     * setzt den Einschaltbefehl
     *
     * @param onCommand Einschaltbefehl
     */
    public void setOnCommand(List<String> onCommand) {

        Preconditions.checkNotNull(onCommand);
        Preconditions.checkArgument(onCommand.size() >= 1, "Ungültiger Befehl %s", onCommand);
        this.onCommand = onCommand;
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
