package net.kleditzsch.apps.automation.controller.mqttservice;

import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Callback bei MQTT Ereignissen
 */
public class MqttCallback implements org.eclipse.paho.client.mqttv3.MqttCallback {

    /**
     * Threadpool
     */
    private ExecutorService executor;

    /**
     * Topic Listener
     */
    private List<TopicListener> topicListeners = new ArrayList<>();

    /**
     * Abbildung von Topics auf die Listener
     */
    private Map<String, TopicListener> topicMap = new HashMap<>();

    /**
     * Abbildung von Wildcard Topics auf die Listener
     */
    private Map<String, TopicListener> topicMapWildcard = new HashMap<>();

    public MqttCallback(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Verbindung verloren
     *
     * @param cause Ausnahme
     */
    @Override
    public void connectionLost(Throwable cause) {

        LoggerUtil.getLogger(getClass()).warning("Verbindung zum MQTT Broker verloren");
        MessageEditor.addMessage(new Message("automation", Message.Type.error, "Verbindung zum MQTT Broker verloren"));
    }

    /**
     * Meldung eingegangen
     *
     * @param topic Topic
     * @param message Medung
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        LoggerUtil.getLogger(getClass()).finest("MQTT Topic \"" + topic + "\" am " + LocalDateTime.now() + " empfangen");

        topic = topic.toLowerCase();

        //vollständige Topics
        if(topicMap.containsKey(topic)) {

            TopicListener listener = topicMap.get(topic);
            listener.messageArrived(topic, message);
            executor.execute(listener);
        }

        //Topic Muster
        for(Map.Entry<String, TopicListener> entry : topicMapWildcard.entrySet()) {

            if(topic.startsWith(entry.getKey())) {

                TopicListener listener = entry.getValue();
                listener.messageArrived(topic, message);
                executor.execute(listener);
            }
        }
    }

    /**
     * Topic gesendet
     *
     * @param token Topic Token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        //not used
    }

    /**
     * fügt einen neuen Topic Listener hinzu
     *
     * @param listener Topic Listener
     * @return Erfolgsmeldung
     */
    public boolean addTopicListener(TopicListener listener) {

        if(!topicListeners.contains(listener)) {

            topicListeners.add(listener);
            updateTopicMap();
            return true;
        }
        return false;
    }

    /**
     * entfernt einen Topic Listener
     *
     * @param listener Topic Listener
     * @return Erfolgsmeldung
     */
    public boolean removeTopicListener(TopicListener listener) {

        return topicListeners.remove(listener);
    }

    /**
     * aktualisiert die Abbildungen der Topics auf die Listener
     */
    private void updateTopicMap() {

        topicMap.clear();
        for(TopicListener listener : topicListeners) {

            List<String> topics = listener.listListenTopics();
            for(String topic : topics) {

                if(!topic.startsWith(MqttService.MAIN_TOPIC)) {

                    topic = MqttService.MAIN_TOPIC + topic;
                }

                if(topic.endsWith("#")) {

                    topic = topic.toLowerCase().substring(0, topic.length() - 1);
                    topicMapWildcard.put(topic, listener);
                } else {

                    topic = topic.toLowerCase();
                    topicMap.put(topic, listener);
                }
            }
        }
    }
}
