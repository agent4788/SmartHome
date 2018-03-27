package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;

/**
 * Script mit an/aus Funktion
 */
public class ScriptDouble extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_SCRIPT_DOUBLE;

    /**
     * Befehle
     */
    private String onCommand;
    private String offCommand;

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
    public String getOffCommand() {
        return offCommand;
    }

    /**
     * setzt den Ausschaltbefehl
     *
     * @param offCommand Ausschaltbefehl
     */
    public void setOffCommand(String offCommand) {

        Preconditions.checkNotNull(offCommand);
        Preconditions.checkArgument(offCommand.length() >= 3, "Ungültiger Befehl %s", offCommand);
        this.offCommand = offCommand;
    }

    /**
     * gibt den Einschaltbefehl zurück
     *
     * @return Einschaltbefehl
     */
    public String getOnCommand() {
        return onCommand;
    }

    /**
     * setzt den Einschaltbefehl
     *
     * @param onCommand Einschaltbefehl
     */
    public void setOnCommand(String onCommand) {

        Preconditions.checkNotNull(onCommand);
        Preconditions.checkArgument(onCommand.length() >= 3, "Ungültiger Befehl %s", onCommand);
        this.onCommand = onCommand;
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
