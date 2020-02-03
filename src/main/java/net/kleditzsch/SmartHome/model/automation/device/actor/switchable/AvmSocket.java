package net.kleditzsch.SmartHome.model.automation.device.actor.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.DoubleSwitchable;

/**
 * AVM Steckdose
 */
public class AvmSocket extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_AVM_SOCKET;

    /**
     * Identifizierung
     */
    private String identifier;

    /**
     * Sensorwerte der Steckdose
     */
    private ID tempSensorId, powerSensorId, energySensorId;

    public AvmSocket() {}

    /**
     * @param id ID
     * @param name Name
     */
    public AvmSocket(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt die Identifizierung der Steckdose zurück
     *
     * @return Identifizierung
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * setzt die Identifizierung der Steckdose
     *
     * @param identifier Identifizierung
     */
    public void setIdentifier(String identifier) {

        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(identifier.matches("^[0-9 ]{10,15}$"), "Ungültige Identifizierung %s", identifier);
        this.identifier = identifier.replace(" ", "");
        setChangedData();
    }

    /**
     * gibt die ID des Temperatur Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getTempSensorId() {
        return tempSensorId;
    }

    /**
     * setzt die ID des Temperatur Sensorwertes
     *
     * @param tempSensorId Sensor ID
     */
    public void setTempSensorId(ID tempSensorId) {

        Preconditions.checkNotNull(tempSensorId);
        this.tempSensorId = tempSensorId;
        setChangedData();
    }

    /**
     * gibt die ID des Verbrauchs Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getPowerSensorId() {
        return powerSensorId;
    }

    /**
     * setzt die ID des Verbrauchs Sensorwertes
     *
     * @param powerSensorId Sensor ID
     */
    public void setPowerSensorId(ID powerSensorId) {

        Preconditions.checkNotNull(powerSensorId);
        this.powerSensorId = powerSensorId;
        setChangedData();
    }

    /**
     * gibt die ID des Energie Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getEnergySensorId() {
        return energySensorId;
    }

    /**
     * setzt die ID des Energie Sensorwertes
     *
     * @param energySensorId Sensor ID
     */
    public void setEnergySensorId(ID energySensorId) {

        Preconditions.checkNotNull(energySensorId);
        this.energySensorId = energySensorId;
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
