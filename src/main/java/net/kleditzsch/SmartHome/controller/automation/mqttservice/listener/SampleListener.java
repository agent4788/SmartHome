package net.kleditzsch.SmartHome.controller.automation.mqttservice.listener;

import net.kleditzsch.SmartHome.controller.automation.mqttservice.TopicListener;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SampleListener extends TopicListener {

    @Override
    public List<String> listListenTopics() {

        return Arrays.asList(
                "test#"
        );
    }

    @Override
    public void run() {

        if(Objects.isNull(topic) || Objects.isNull(message)) {

            return;
        }

        System.out.println("Beispiel Listener");
        System.out.println("Topic: " + topic);
        System.out.println("Payload: " + new String(message.getPayload()));
        System.out.println("Timestamp: " + LocalDateTime.now());
        System.out.println("-------------------------------------");

        topic = null;
        message = null;
    }
}
