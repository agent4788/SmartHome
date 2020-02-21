package net.kleditzsch.applications.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.utility.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.applications.automation.controller.mqttservice.MqttService;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Shutter;
import net.kleditzsch.applications.automation.model.device.actor.shutter.MqttShutter;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MoveMqttShutterHandler implements Runnable {

    private MqttShutter shutter;

    private int targetLevel = 0;

    public MoveMqttShutterHandler(MqttShutter shutter, int targetLevel) {

        Preconditions.checkNotNull(shutter);
        Preconditions.checkArgument(targetLevel >= 0 && targetLevel <= 100);
        this.shutter = shutter;
        this.targetLevel = targetLevel;
    }

    @Override
    public void run() {

        boolean successfull = false;

        //deaktivierten Rolladen ignorieren
        if(shutter.isDisabled()) {

            return;
        }

        String topic = MqttService.MAIN_TOPIC + "shutter/" + shutter.getMqttName() + "/move/" + targetLevel;
        String now = DatabaseDateTimeUtil.getDatabaseDateTimeStr(LocalDateTime.now());

        successfull = SmartHome.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);

        //neuen Status setzen
        if(successfull) {

            ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            Optional<Actor> actorOptional = actorEditor.getById(shutter.getId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof Shutter) {

                Shutter shutter = (Shutter) actorOptional.get();
                shutter.setLastUpdateTime(LocalDateTime.now());
            }

            lock.unlock();
        }
    }
}
