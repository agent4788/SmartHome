package net.kleditzsch.SmartHome.model.automation.device.sensor.virtual;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.WaterAmountValue;

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
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
