package net.kleditzsch.applications.automation.model.room.element;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.room.Interface.RoomElement;

import java.util.ArrayList;
import java.util.List;

public class ShutterElement extends RoomElement {

    private Type type = Type.SHUTTER_ELEMENT;

    /**
     * Liste mit den zugeordneten Rollladen IDs
     */
    private List<ID> shutterIds = new ArrayList<>();

    /**
     * gibt eine Liste mit den zugeordneten Rollladen IDs zurück
     *
     * @return Liste mit den zugeordneten Rollladen IDs
     */
    public List<ID> getShutterIds() {
        return shutterIds;
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
