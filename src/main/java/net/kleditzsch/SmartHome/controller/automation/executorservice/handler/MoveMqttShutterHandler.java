package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.MqttService;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.shutter.MqttShutter;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;

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

        successfull = Application.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);

        //neuen Status setzen
        if(successfull) {

            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
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
