package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.UserAtHomeValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserAtHomeSensorHandler implements Runnable {

    /**
     * Benutzer zu Hause
     */
    private UserAtHomeValue userAtHomeValue;

    /**
     * @param userAtHomeValue Sensorwert Benutzer zu Hause
     */
    public UserAtHomeSensorHandler(UserAtHomeValue userAtHomeValue) {
        this.userAtHomeValue = userAtHomeValue;
    }

    @Override
    public void run() {

        try {

            if(userAtHomeValue.isUseExternalDataSource()) {

                //Timeout Prüfen
                LocalDateTime timeout = userAtHomeValue.getLastPushTime().plusNanos(userAtHomeValue.getTimeout() * 1_000_000L);
                LocalDateTime now = LocalDateTime.now();
                if(userAtHomeValue.isAtHome() && timeout.isBefore(now)) {

                    //Timeout abgelaufen, Status zurücksetzen
                    SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                    ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
                    lock.lock();

                    Optional<SensorValue> sensorValueOptional = sensorEditor.getById(userAtHomeValue.getId());
                    if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof UserAtHomeValue) {

                        ((UserAtHomeValue) sensorValueOptional.get()).setAtHome(false);
                    }

                    lock.unlock();
                }
            } else {

                //Ping versenden
                InetAddress address = InetAddress.getByName(userAtHomeValue.getIpAddress());
                if(address.isReachable(500)) {

                    //erreichbar
                    SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                    ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
                    lock.lock();

                    Optional<SensorValue> sensorValueOptional = sensorEditor.getById(userAtHomeValue.getId());
                    if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof UserAtHomeValue) {

                        ((UserAtHomeValue) sensorValueOptional.get()).pushAtHome(true);
                    }

                    lock.unlock();
                } else {

                    //nicht erreichbar
                    SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                    ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
                    lock.lock();

                    Optional<SensorValue> sensorValueOptional = sensorEditor.getById(userAtHomeValue.getId());
                    if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof UserAtHomeValue) {

                        ((UserAtHomeValue) sensorValueOptional.get()).pushAtHome(false);
                    }

                    lock.unlock();
                }
            }
        } catch (Exception e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
        }
    }
}
