package net.kleditzsch.apps.automation.model.room.element;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.room.Interface.RoomElement;

import java.util.Optional;

public class VirtualSensorElement extends RoomElement {

    private Type type = Type.VIRTUAL_SENSOR_ELEMENT;

    /**
     * Sensor Werte IDs
     */
    private ID virtualSensorId;

    /**
     * gibt die ID des Virtuellen Sensors zurück
     *
     * @return ID des Virtuellen Sensors
     */
    public Optional<ID> getVirtualSensorId() {
        return Optional.ofNullable(virtualSensorId);
    }

    /**
     * setzt die ID des Virtuellen Sensors
     *
     * @param virtualSensorId ID des Virtuellen Sensors
     */
    public void setVirtualSensorId(ID virtualSensorId) {
        this.virtualSensorId = virtualSensorId;
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
