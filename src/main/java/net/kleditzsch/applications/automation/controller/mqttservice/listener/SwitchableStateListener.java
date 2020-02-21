package net.kleditzsch.applications.automation.controller.mqttservice.listener;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.applications.automation.controller.mqttservice.MqttService;
import net.kleditzsch.applications.automation.controller.mqttservice.TopicListener;
import net.kleditzsch.applications.automation.model.device.AutomationElement;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.switchable.MqttDouble;
import net.kleditzsch.applications.automation.model.device.actor.switchable.MqttSingle;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SwitchableStateListener extends TopicListener {

    /**
     * Json Daten
     */
    private class JsonData {

        public int state;
        public LocalDateTime date;
    }

    @Override
    public List<String> listListenTopics() {

        return Arrays.asList(
                "switch/#"
        );
    }

    @Override
    public void run() {

        if(Objects.isNull(topic) || Objects.isNull(message)) {

            return;
        }

        //auf Topic <MAIN Topic>/switch/<MQTT Name</state hören
        String mqttName = topic.substring(MqttService.MAIN_TOPIC.length() + 7, topic.length() - 6);
        String payload = new String(message.getPayload());

        try {

            JsonData data = SmartHome.getInstance().getGson().fromJson(payload, JsonData.class);

            //Status nur verarbeiten wenn nicht älter als 5 Minuten
            if(data.date.isAfter(LocalDateTime.now().minusMinutes(5))) {

                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
                ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
                lock.lock();

                Optional<MqttDouble> mqttDoubleOptional = actorEditor.getData().stream()
                        .filter(e -> e instanceof MqttDouble)
                        .map(e -> (MqttDouble) e)
                        .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                        .findFirst();

                if (mqttDoubleOptional.isPresent()) {

                    //MQTT Gerät (an/aus) gefunden
                    Optional<Actor> actorOptional = actorEditor.getById(mqttDoubleOptional.get().getId());
                    if(actorOptional.isPresent() && actorOptional.get() instanceof MqttDouble) {

                        MqttDouble mqttDouble = (MqttDouble) actorOptional.get();
                        mqttDouble.setLastToggleTime(data.date);
                        if(mqttDouble.isInverse()) {

                            mqttDouble.setState(data.state == 1 ? AutomationElement.State.OFF : AutomationElement.State.ON);
                        } else {

                            mqttDouble.setState(data.state == 1 ? AutomationElement.State.ON : AutomationElement.State.OFF);
                        }
                    }

                } else {

                    Optional<MqttSingle> mqttSingleOptional = actorEditor.getData().stream()
                            .filter(e -> e instanceof MqttSingle)
                            .map(e -> (MqttSingle) e)
                            .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                            .findFirst();

                    if (mqttSingleOptional.isPresent()) {

                        //MQTT Gerät (einfach) gefunden
                        Optional<Actor> actorOptional = actorEditor.getById(mqttSingleOptional.get().getId());
                        if(actorOptional.isPresent() && actorOptional.get() instanceof MqttSingle) {

                            MqttSingle mqttSingle = (MqttSingle) actorOptional.get();
                            mqttSingle.setState(data.state == 1 ? AutomationElement.State.ON : AutomationElement.State.OFF);
                        }
                    }
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
