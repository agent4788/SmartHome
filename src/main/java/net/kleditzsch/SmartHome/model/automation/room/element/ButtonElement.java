package net.kleditzsch.SmartHome.model.automation.room.element;

import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;

import java.util.ArrayList;
import java.util.List;

public class ButtonElement extends RoomElement {

    private Type type = Type.BUTTON_ELEMENT;

    /**
     * liste mit den Befehlen
     */
    private List<SwitchCommand> commands = new ArrayList<>();

    /**
     * gibt die Liste mit den Schaltbefehlen zurück
     *
     * @return Liste mit den Schaltbefehlen
     */
    public List<SwitchCommand> getCommands() {

        return commands;
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
