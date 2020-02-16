package net.kleditzsch.apps.automation.model.device.sensor.virtual;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.EnergyValue;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.VirtualSensorValue;

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
        sum = 0.0;
        average = 0.0;
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
     * gibt den Mittelwert zurück
     *
     * @return Mittelwert
     */
    public EnergyValue getAverageAsSensorValue() {

        EnergyValue value = new EnergyValue(ID.create(), ID.create().get(), getName());
        value.pushEnergy(getAverage());
        return value;
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
     * gibt die Summe zurück
     *
     * @return Summe
     */
    public EnergyValue getSumAsSensorValue() {

        EnergyValue value = new EnergyValue(ID.create(), ID.create().get(), getName());
        value.pushEnergy(getSum());
        return value;
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
