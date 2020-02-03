package net.kleditzsch.SmartHome.controller.automation.mqttservice.listener;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.MqttService;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.TopicListener;
import net.kleditzsch.SmartHome.model.automation.device.actor.shutter.MqttShutter;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShutterStateListener extends TopicListener {

    /**
     * Json Daten
     */
    private class JsonData {

        public int level;
        public LocalDateTime date;
    }

    @Override
    public List<String> listListenTopics() {

        return Arrays.asList(
                "shutter/#"
        );
    }

    @Override
    public void run() {

        if(Objects.isNull(topic) || Objects.isNull(message)) {

            return;
        }

        //auf Topic <MAIN Topic>/shutter/<MQTT Name>/level hören
        String mqttName = topic.substring(MqttService.MAIN_TOPIC.length() + 8, topic.length() - 6);
        String payload = new String(message.getPayload());

        try {

            JsonData data = Application.getInstance().getGson().fromJson(payload, JsonData.class);

            //Status nur verarbeiten wenn nicht älter als 5 Minuten oder niveau ungültig
            if(data.date.isAfter(LocalDateTime.now().minusMinutes(5)) && data.level >= 0 && data.level <= 100) {

                ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
                ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
                lock.lock();

                Optional<MqttShutter> mqttShutterOptional = actorEditor.getData().stream()
                        .filter(e -> e instanceof MqttShutter)
                        .map(e -> (MqttShutter) e)
                        .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                        .findFirst();

                if(mqttShutterOptional.isPresent()) {

                    MqttShutter shutter = mqttShutterOptional.get();
                    shutter.setLastUpdateTime(LocalDateTime.now());
                    shutter.setLevel(data.level);
                }

                lock.unlock();
            }

        } catch (Exception e) {

            //ungültige Daten empfangen -> ignorieren
        }

        topic = null;
        message = null;
    }
}
