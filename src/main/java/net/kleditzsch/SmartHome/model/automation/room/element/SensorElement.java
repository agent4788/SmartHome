package net.kleditzsch.SmartHome.model.automation.room.element;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;

import java.util.Optional;

public class SensorElement extends RoomElement {

    private Type type = Type.SENSOR_ELEMENT;

    /**
     * Sensor Werte IDs
     */
    private ID firstSensorValueId;
    private ID secondSensorValueId;
    private ID thirdSensorValueId;

    /**
     * gibt die ID des ersten Sensorwertes zurück
     *
     * @return ID des Sensorwertes
     */
    public Optional<ID> getFirstSensorValueId() {
        return Optional.ofNullable(firstSensorValueId);
    }

    /**
     * setzt die ID des ersten Sensorwertes
     *
     * @param firstSensorValueId ID des Sensorwerten
     */
    public void setFirstSensorValueId(ID firstSensorValueId) {
        this.firstSensorValueId = firstSensorValueId;
    }

    /**
     * gibt die ID des zweiten Sensorwertes zurück
     *
     * @return ID des Sensorwertes
     */
    public Optional<ID> getSecondSensorValueId() {
        return Optional.ofNullable(secondSensorValueId);
    }

    /**
     * setzt die ID des zweiten Sensorwertes
     *
     * @param secondSensorValueId ID des Sensorwerten
     */
    public void setSecondSensorValueId(ID secondSensorValueId) {
        this.secondSensorValueId = secondSensorValueId;
    }

    /**
     * gibt die ID des dritten Sensorwertes zurück
     *
     * @return ID des Sensorwertes
     */
    public Optional<ID> getThirdSensorValueId() {
        return Optional.ofNullable(thirdSensorValueId);
    }

    /**
     * setzt die ID des dritten Sensorwertes
     *
     * @param thirdSensorValueId ID des Sensorwerten
     */
    public void setThirdSensorValueId(ID thirdSensorValueId) {
        this.thirdSensorValueId = thirdSensorValueId;
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
