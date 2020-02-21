package net.kleditzsch.applications.automation.controller.executorservice.handler;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.UserAtHomeValue;
import net.kleditzsch.applications.automation.model.editor.SensorEditor;

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

        //Befehle für deaktivierte Geräte ignorieren
        if(userAtHomeValue.isDisabled()) {

            return;
        }

        ReentrantReadWriteLock.WriteLock lock = null;
        try {

            if(userAtHomeValue.isUseExternalDataSource()) {

                //Timeout Prüfen
                LocalDateTime timeout = userAtHomeValue.getLastPushTime().plusNanos(userAtHomeValue.getLiveTimeout() * 1_000_000L);
                LocalDateTime now = LocalDateTime.now();
                if(userAtHomeValue.isAtHome() && timeout.isBefore(now)) {

                    //Timeout abgelaufen, Status zurücksetzen
                    SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
                    lock = sensorEditor.writeLock();
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
                    SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
                    lock = sensorEditor.writeLock();
                    lock.lock();

                    Optional<SensorValue> sensorValueOptional = sensorEditor.getById(userAtHomeValue.getId());
                    if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof UserAtHomeValue) {

                        ((UserAtHomeValue) sensorValueOptional.get()).pushAtHome(true);
                    }

                    lock.unlock();
                } else {

                    //nicht erreichbar
                    SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
                    lock = sensorEditor.writeLock();
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
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Update Benutzer zu Hause fehlgeschlagen", e));
        } finally {

            if(lock != null && lock.isHeldByCurrentThread()) {

                lock.unlock();
            }
        }
    }
}
