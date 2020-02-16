package net.kleditzsch.apps.automation.model.device.sensor;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * Energieverbrauch
 */
public class EnergyValue extends SensorValue {

    private Type type = Type.SENSORVALUE_ENERGY;

    /**
     * Energieverbrauch in Wh
     */
    private double energy = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public EnergyValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public EnergyValue(ID id, String identifier, String name, int timeout) {
        super(id, identifier, name, timeout);
    }

    /**
     * gibt den Energieverbrauch zurück
     *
     * @return Energieverbrauch
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * setzt den Energieverbrauch
     *
     * @param energy Energieverbrauch
     */
    public void setEnergy(double energy) {

        this.energy = energy;
        setChangedData();
    }

    /**
     * fügt den Energieverbrauch hinzu
     *
     * @param energy Energieverbrauch
     */
    public void pushEnergy(double energy) {

        setEnergy(energy);
        setLastPushTime(LocalDateTime.now());
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
