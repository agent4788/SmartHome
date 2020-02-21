package net.kleditzsch.applications.automation.model.device.sensor.virtual;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.ActualPowerValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.VirtualSensorValue;

import java.util.List;

/**
 * Virtueller aktueller Verbrauch
 */
public class VirtualActualPowerValue extends VirtualSensorValue {

    private Type type = Type.VIRTUALSENSORVALUE_ACTUAL_POWER;

    /**
     * Statistische Werte
     */
    private double average = 0.0;
    private double sum = 0.0;
    private double min = -100000000;
    private double max = -100000000;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualActualPowerValue(ID id, String identifier, String name) {
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
        this.sum = 0;
        for(SensorValue sensorValue : sensorValues) {

            if(sensorValue instanceof ActualPowerValue) {

                ActualPowerValue actualPowerValue = (ActualPowerValue) sensorValue;
                this.sum += actualPowerValue.getActualPower();
                count++;

                if(this.min == -100000000 || actualPowerValue.getActualPower() < this.min) {

                    this.min = actualPowerValue.getActualPower();
                }
                if(this.max == -100000000 || actualPowerValue.getActualPower() > this.max) {

                    this.max = actualPowerValue.getActualPower();
                }
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
    public ActualPowerValue getAverageAsSensorValue() {

        ActualPowerValue value = new ActualPowerValue(ID.create(), ID.create().get(), getName());
        value.pushActualPower(getAverage());
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
    public ActualPowerValue getSumAsSensorValue() {

        ActualPowerValue value = new ActualPowerValue(ID.create(), ID.create().get(), getName());
        value.pushActualPower(getSum());
        return value;
    }

    /**
     * gibt den Minimalwert zurück
     *
     * @return Minimalwert
     */
    public double getMin() {
        return min;
    }

    /**
     * gibt den Minimalwert zurück
     *
     * @return Minimalwert
     */
    public ActualPowerValue getMinAsSensorValue() {

        ActualPowerValue value = new ActualPowerValue(ID.create(), ID.create().get(), getName());
        value.pushActualPower(getMin());
        return value;
    }

    /**
     * gibt den Maximalwert zurück
     *
     * @return Maximalwert
     */
    public double getMax() {
        return max;
    }

    /**
     * gibt den Maximalwert zurück
     *
     * @return Maximalwert
     */
    public ActualPowerValue getMaxAsSensorValue() {

        ActualPowerValue value = new ActualPowerValue(ID.create(), ID.create().get(), getName());
        value.pushActualPower(getMax());
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
