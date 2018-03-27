package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Energieverbrauch
 */
public class EnergyValue extends SensorValue {

    private Type type = Type.SENSORVALUE_ENERGY;

    /**
     * Energieverbrauch in Wh
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10205, message = "Ungültiger Sensorwert")
    @ValidateMax(value = Double.MAX_VALUE, errorCode = 10205, message = "Ungültiger Sensorwert")
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
