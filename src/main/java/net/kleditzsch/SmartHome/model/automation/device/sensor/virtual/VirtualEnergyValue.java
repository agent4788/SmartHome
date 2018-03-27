package net.kleditzsch.SmartHome.model.automation.device.sensor.virtual;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.EnergyValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.VirtualSensorValue;

import java.util.List;

/**
 * Virtueller Energiesensor
 */
public class VirtualEnergyValue extends VirtualSensorValue {

    private Type type = Type.VIRTUALSENSORVALUE_ENERGY;

    /**
     * Statistische Werte
     */
    private double average = 0.0;
    private double sum = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualEnergyValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * ermittelt aus den Sensorwerten die Statistischen Werte
     *
     * @param sensorValues Sensorwerte
     */
    @Override
    public void processValues(List<SensorValue> sensorValues) {

        int count = 0;
        for(SensorValue sensorValue : sensorValues) {

            if(sensorValue instanceof EnergyValue) {

                EnergyValue energyValue = (EnergyValue) sensorValue;
                this.sum += energyValue.getEnergy();
                count++;
            }
        }

        this.average = this.sum / count;
    }

    /**
     * gibt den Mittelwert zurück
     *
     * @return Mittelwert
     */
    public double getAverage() {
        return average;
    }

    /**
     * gibt die Summe zurück
     *
     * @return Summe
     */
    public double getSum() {
        return sum;
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
