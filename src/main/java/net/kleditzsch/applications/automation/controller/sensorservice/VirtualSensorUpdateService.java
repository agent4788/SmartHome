package net.kleditzsch.applications.automation.controller.sensorservice;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.applications.automation.model.device.sensor.*;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.virtual.*;
import net.kleditzsch.applications.automation.model.editor.SensorEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VirtualSensorUpdateService implements Runnable {

    @Override
    public void run() {

        SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
        lock.lock();

        sensorEditor.getData().stream()
                .filter(e -> e instanceof VirtualSensorValue)
                .map(e -> (VirtualSensorValue) e)
                .forEach(e -> {

                    switch (e.getType()) {

                        case VIRTUALSENSORVALUE_ACTUAL_POWER:

                            VirtualActualPowerValue virtualSensorValue = (VirtualActualPowerValue) e;

                            List<SensorValue> sensorValues = new ArrayList<>();
                            Set<String> sensorIds = virtualSensorValue.getSensorValues();
                            sensorIds.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof ActualPowerValue) {

                                    sensorValues.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue.processValues(sensorValues);
                            virtualSensorValue.updateLastPushTime();
                            break;
                        case VIRTUALSENSORVALUE_ENERGY:

                            VirtualEnergyValue virtualSensorValue1 = (VirtualEnergyValue) e;

                            List<SensorValue> sensorValues1 = new ArrayList<>();
                            Set<String> sensorIds1 = virtualSensorValue1.getSensorValues();
                            sensorIds1.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof EnergyValue) {

                                    sensorValues1.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue1.processValues(sensorValues1);
                            virtualSensorValue1.updateLastPushTime();
                            break;
                        case VIRTUALSENSORVALUE_GAS_AMOUNT:

                            VirtualGasAmountValue virtualSensorValue2 = (VirtualGasAmountValue) e;

                            List<SensorValue> sensorValues2 = new ArrayList<>();
                            Set<String> sensorIds2 = virtualSensorValue2.getSensorValues();
                            sensorIds2.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof GasAmountValue) {

                                    sensorValues2.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue2.processValues(sensorValues2);
                            virtualSensorValue2.updateLastPushTime();
                            break;
                        case VIRTUALSENSORVALUE_LIGHT_INTENSITY:

                            VirtualLightIntensityValue virtualSensorValue3 = (VirtualLightIntensityValue) e;

                            List<SensorValue> sensorValues3 = new ArrayList<>();
                            Set<String> sensorIds3 = virtualSensorValue3.getSensorValues();
                            sensorIds3.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof LightIntensityValue) {

                                    sensorValues3.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue3.processValues(sensorValues3);
                            virtualSensorValue3.updateLastPushTime();
                            break;
                        case VIRTUALSENSORVALUE_TEMPERATURE:

                            VirtualTemperatureValue virtualSensorValue4 = (VirtualTemperatureValue) e;

                            List<SensorValue> sensorValues4 = new ArrayList<>();
                            Set<String> sensorIds4 = virtualSensorValue4.getSensorValues();
                            sensorIds4.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof TemperatureValue) {

                                    sensorValues4.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue4.processValues(sensorValues4);
                            virtualSensorValue4.updateLastPushTime();
                            break;
                        case VIRTUALSENSORVALUE_WATER_AMOUNT:

                            VirtualWaterAmountValue virtualSensorValue5 = (VirtualWaterAmountValue) e;

                            List<SensorValue> sensorValues5 = new ArrayList<>();
                            Set<String> sensorIds5 = virtualSensorValue5.getSensorValues();
                            sensorIds5.forEach(id -> {

                                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof WaterAmountValue) {

                                    sensorValues5.add(sensorValueOptional.get());
                                }
                            });

                            virtualSensorValue5.processValues(sensorValues5);
                            virtualSensorValue5.updateLastPushTime();
                            break;
                    }
                });

        lock.unlock();
    }
}
