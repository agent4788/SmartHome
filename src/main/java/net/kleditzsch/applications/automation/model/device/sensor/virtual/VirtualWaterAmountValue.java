package net.kleditzsch.applications.automation.model.device.sensor.virtual;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.WaterAmountValue;

import java.util.List;

/**
 * Virtueller Wasserzähler
 */
public class VirtualWaterAmountValue extends VirtualSensorValue {

    private Type type = Type.VIRTUALSENSORVALUE_WATER_AMOUNT;

    /**
     * Statistische Werte
     */
    private double sum = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualWaterAmountValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * ermittelt aus den Sensorwerten die Statistischen Werte
     *
     * @param sensorValues Sensorwerte
     */
    @Override
    public void processValues(List<SensorValue> sensorValues) {

        for(SensorValue sensorValue : sensorValues) {

            if(sensorValue instanceof WaterAmountValue) {

                WaterAmountValue waterAmountValue = (WaterAmountValue) sensorValue;
                this.sum += waterAmountValue.getWaterAmount();
            }
        }
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
    public WaterAmountValue getSumAsSensorValue() {

        WaterAmountValue value = new WaterAmountValue(ID.create(), ID.create().get(), getName());
        value.pushWaterAmount(getSum());
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
