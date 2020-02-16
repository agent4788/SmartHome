package net.kleditzsch.apps.automation.controller.mqttservice;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Objects;

/**
 * Topic Listener
 */
public abstract class TopicListener implements Runnable {

    /**
     * Topic
     */
    protected String topic;

    /**
     * Meldung
     */
    protected MqttMessage message;

    /**
     * gibt eine Liste mit den gewünschten Topics zurück
     *
     * @return Liste mit Topics
     */
    public abstract List<String> listListenTopics();

    /**
     * meldung Eingegangen
     *
     * @param topic Topic
     * @param message Meldung
     */
    public final void messageArrived(String topic, MqttMessage message) {

        if(Objects.isNull(topic) || Objects.isNull(message)) {

            return;
        }

        this.topic = topic;
        this.message = message;
    }
}

