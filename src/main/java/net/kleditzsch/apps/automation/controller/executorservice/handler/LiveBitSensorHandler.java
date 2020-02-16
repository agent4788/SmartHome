package net.kleditzsch.apps.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.LiveBitValue;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LiveBitSensorHandler implements Runnable {

    /**
     * Lebenszeichen
     */
    private LiveBitValue liveBitValue;

    /**
     * @param liveBitValue Sensorwert Lebenszeichen
     */
    public LiveBitSensorHandler(LiveBitValue liveBitValue) {

        Preconditions.checkNotNull(liveBitValue);
        this.liveBitValue = liveBitValue;
    }

    @Override
    public void run() {

        //Befehle für deaktivierte Geräte ignorieren
        if(liveBitValue.isDisabled()) {

            return;
        }

        ReentrantReadWriteLock.WriteLock lock = null;
        try {

            LocalDateTime timeout = liveBitValue.getLastPushTime().plusNanos(liveBitValue.getLiveTimeout() * 1_000_000L);
            LocalDateTime now = LocalDateTime.now();
            if(liveBitValue.getState() && timeout.isBefore(now)) {

                //Timeout abgelaufen, Status zurücksetzen
                SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
                lock = sensorEditor.writeLock();
                lock.lock();

                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(liveBitValue.getId());
                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof LiveBitValue) {

                    ((LiveBitValue) sensorValueOptional.get()).setState(false);
                }
            }
        } catch (Exception e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Fehler in der Lebenszeichenüberwachung", e));
        } finally {

            if(lock != null && lock.isHeldByCurrentThread()) {

                lock.unlock();
            }
        }
    }
}
