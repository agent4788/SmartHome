package net.kleditzsch.applications.automation.model.device.sensor.virtual;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.sensor.GasAmountValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.VirtualSensorValue;

import java.util.List;

/**
 * Virtueller Gaszähler
 */
public class VirtualGasAmountValue extends VirtualSensorValue {

    private Type type = Type.VIRTUALSENSORVALUE_GAS_AMOUNT;

    /**
     * Statistische Werte
     */
    private double sum = 0.0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public VirtualGasAmountValue(ID id, String identifier, String name) {
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

            if(sensorValue instanceof GasAmountValue) {

                GasAmountValue gasAmountValue = (GasAmountValue) sensorValue;
                this.sum += gasAmountValue.getGasAmount();
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
    public GasAmountValue getSumAsSensorValue() {

        GasAmountValue value = new GasAmountValue(ID.create(), ID.create().get(), getName());
        value.pushGasAmount(getSum());
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
