package net.kleditzsch.applications.automation.controller.sensorservice;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.applications.automation.controller.executorservice.ExecutorService;
import net.kleditzsch.applications.automation.controller.executorservice.command.SensorValueCommand;
import net.kleditzsch.applications.automation.model.device.sensor.LiveBitValue;
import net.kleditzsch.applications.automation.model.editor.SensorEditor;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LiveBitUpdateService implements Runnable {

    @Override
    public void run() {

        SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
        lock.lock();

        ExecutorService executorService = SmartHome.getInstance().getAutomation().getExecutorService();
        sensorEditor.getData().stream()
                .filter(e -> e instanceof LiveBitValue)
                .map(e -> {

                    return (LiveBitValue) sensorEditor.copyOf(e);
                })
                .filter(LiveBitValue::getState)
                .forEach(e -> {

                    executorService.putCommand(new SensorValueCommand(e));
                });

        lock.unlock();
    }
}
