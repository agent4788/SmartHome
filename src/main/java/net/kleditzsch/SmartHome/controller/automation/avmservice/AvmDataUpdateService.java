package net.kleditzsch.SmartHome.controller.automation.avmservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.device.sensor.ActualPowerValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.EnergyValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.TemperatureValue;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.AvmSocket;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.PowerMeter;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.Switch;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.TemperatureSensor;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AvmDataUpdateService implements Runnable {

    @Override
    public void run() {

        try {

            AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();

            //aktuelle Daten von der FritzBox holen
            avmEditor.load();
            LoggerUtil.getLogger(this.getClass()).finest("Die AVM Daten wurden erfolgreich aktualisiert");

            //Status der AVM Steckdosen aktualisieren
            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            List<AvmSocket> avmSockets = actorEditor.getData().stream()
                    .filter(e -> e instanceof AvmSocket)
                    .map(e -> ((AvmSocket) e))
                    .collect(Collectors.toList());

            ReentrantReadWriteLock.ReadLock avmLock = avmEditor.readLock();
            avmLock.lock();

            //Schaltstatus aktualisieren
            avmSockets.stream()
                    .filter(e -> !e.isDisabled())
                    .forEach(e -> {

                        Optional<SmarthomeDevice> deviceOptional = avmEditor.getDeviceByIdentifier(e.getIdentifier());
                        if(deviceOptional.isPresent() && deviceOptional.get().getSwitch().isPresent()) {

                            Switch.STATE state = deviceOptional.get().getSwitch().get().getState();
                            if((state == Switch.STATE.ON && !e.isInverse()) || (state == Switch.STATE.OFF && e.isInverse())) {

                                e.setState(Switchable.State.ON);
                            } else {

                                e.setState(Switchable.State.OFF);
                            }
                        }
                    });

            //Sensordaten aktualisieren
            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock sensorLock = sensorEditor.writeLock();
            sensorLock.lock();

            avmSockets.forEach(e -> {

                Optional<SmarthomeDevice> deviceOptional = avmEditor.getDeviceByIdentifier(e.getIdentifier());
                if(deviceOptional.isPresent()) {

                    //Temperatursensor
                    if(deviceOptional.get().getTemperatureSensor().isPresent()) {

                        TemperatureSensor temperatureSensor = deviceOptional.get().getTemperatureSensor().get();
                        Optional<SensorValue> sensorValueOptional = sensorEditor.getById(e.getTempSensorId());
                        if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof TemperatureValue) {

                            TemperatureValue temperatureValue = (TemperatureValue) sensorValueOptional.get();
                            temperatureValue.pushTemperature(temperatureSensor.getTemperature());
                        }
                    }

                    //Aktuelle Energie und aktueller Energieverbrauch
                    if(deviceOptional.get().getPowerMeter().isPresent()) {

                        PowerMeter powerMeter = deviceOptional.get().getPowerMeter().get();
                        Optional<SensorValue> sensorValueOptional = sensorEditor.getById(e.getPowerSensorId());
                        if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof ActualPowerValue) {

                            ActualPowerValue actualPowerValue = (ActualPowerValue) sensorValueOptional.get();
                            actualPowerValue.pushActualPower(powerMeter.getPower());
                        }
                        Optional<SensorValue> sensorValueOptional1 = sensorEditor.getById(e.getEnergySensorId());
                        if(sensorValueOptional1.isPresent() && sensorValueOptional1.get() instanceof EnergyValue) {

                            EnergyValue energyValue = (EnergyValue) sensorValueOptional1.get();
                            energyValue.pushEnergy(powerMeter.getEnergy());
                        }
                    }
                }
            });

            sensorLock.unlock();

            avmLock.unlock();
            lock.unlock();

        } catch (AuthException e) {

            LoggerUtil.getLogger(this.getClass()).finer("Die AVM Daten konnten nicht aktualisiert werden");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die AVM Daten konnten nicht aktualisiert werden", e));
        }
    }
}
