package net.kleditzsch.SmartHome.controller.automation.sensorservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SensorValueCommand;
import net.kleditzsch.SmartHome.model.automation.device.sensor.LiveBitValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LiveBitUpdateService implements Runnable {

    @Override
    public void run() {

        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
        lock.lock();

        ExecutorService executorService = Application.getInstance().getAutomation().getExecutorService();
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
