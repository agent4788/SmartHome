package net.kleditzsch.SmartHome.controller.automation.mqttservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.listener.SwitchableStateListener;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service zum verarbeiten von MQTT Informationen
 */
public class MqttService {

    /**
     * Haupttopic (alle Topics unter diesem Haupttopic werden durch die Anwendung verarbeitet)
     */
    public static final String MAIN_TOPIC = "smartHome/";

    /**
     * MQTT Service aktiv
     */
    private boolean active = true;

    /**
     * Broker Adresse
     */
    private String brokerAddress = "";

    /**
     * Broker Port
     */
    private int brokerPort = 1883;

    /**
     * Client Name am Server
     */
    private String clientId = "SmartHome Server";

    /**
     * Benutzername
     */
    private String username = "";

    /**
     * Passwort
     */
    private String password = "";

    /**
     * MQTT Clent
     */
    private MqttClient client;

    /**
     * Threadpool
     */
    private ExecutorService executor;

    /**
     * Mqtt Callback
     */
    private MqttCallback callback;

    public MqttService() {

        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = settingsEditor.readLock();
        lock.lock();

        Optional<BooleanSetting> activeOptional = settingsEditor.getBooleanSetting(SettingsEditor.AUTOMATION_MQTT_ACTIVE);
        active = activeOptional.isPresent() && activeOptional.get().getValue();
        Optional<StringSetting> addressOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_ADDRESS);
        brokerAddress = addressOptional.isPresent() ? addressOptional.get().getValue() : "";
        Optional<IntegerSetting> portOptional = settingsEditor.getIntegerSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PORT);
        brokerPort = portOptional.isPresent() ? portOptional.get().getValue() : 1883;
        Optional<StringSetting> clientIdOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_CLIENT_ID);
        clientId = clientIdOptional.isPresent() ? clientIdOptional.get().getValue() : "";
        Optional<StringSetting> userOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_USERNAME);
        username = userOptional.isPresent() ? userOptional.get().getValue() : "";
        Optional<StringSetting> passwordOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PASSWORD);
        password = passwordOptional.isPresent() ? passwordOptional.get().getValue() : "";

        lock.unlock();
    }

    /**
     * @param brokerAddress Broker Adresse
     */
    public MqttService(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param brokerPort Broker Port
     */
    public MqttService(String brokerAddress, int brokerPort) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param clientId Client Name am Broker
     */
    public MqttService(String brokerAddress, String clientId) {
        this.brokerAddress = brokerAddress;
        this.clientId = clientId;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param brokerPort Broker Port
     * @param clientId Client Name am Broker
     */
    public MqttService(String brokerAddress, int brokerPort, String clientId) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param username Benutzername
     * @param password Passwort
     */
    public MqttService(String brokerAddress, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.username = username;
        this.password = password;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param brokerPort Broker Port
     * @param username Benutzername
     * @param password Passwort
     */
    public MqttService(String brokerAddress, int brokerPort, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.username = username;
        this.password = password;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param clientId Client Name am Broker
     * @param username Benutzername
     * @param password Passwort
     */
    public MqttService(String brokerAddress, String clientId, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    /**
     * @param brokerAddress Broker Adresse
     * @param brokerPort Broker Port
     * @param clientId Client Name am Broker
     * @param username Benutzername
     * @param password Passwort
     */
    public MqttService(String brokerAddress, int brokerPort, String clientId, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    /**
     * gibt an ob der MQTT Service aktiv ist
     *
     * @return MQTT Service aktiv
     */
    public boolean isActive() {

        return active;
    }

    /**
     * verbindet den MQTT Broker
     */
    public void startService() {

        if(!active) {
            return;
        }

        //Executor Starten
        executor = Executors.newCachedThreadPool();
        callback = new MqttCallback(executor);

        try {

            //Konfiguration
            MqttConnectOptions options = new MqttConnectOptions();
            if(!username.equals("") && !password.equals("")) {

                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setMaxInflight(50);
            options.setWill("shmartHome/connectionLost", "verbindung verloren".getBytes(), 2, false);

            //Client erstellen
            client = new MqttClient("tcp://" + brokerAddress + ":" + brokerPort, clientId);
            client.setCallback(callback);

            // verbinden
            client.connect(options);

            //für alle SmartHomeTopics anmelden
            client.subscribe(MAIN_TOPIC + "#");

            callback.addTopicListener(new SwitchableStateListener());

        } catch (MqttException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Fehler beim Verbindungsaufbau zum MQTT Broker \"" + brokerAddress + "\"");
            MessageEditor.addMessage(new Message("automation", Message.Type.error, "Fehler beim Verbindungsaufbau zum MQTT Broker \"" + brokerAddress + "\""));
        }
    }

    /**
     * sendet ein Topic an den Broker
     *
     * @param topic Topic
     * @param payload Inhalt
     * @return Erfolgsmeldung
     */
    public synchronized boolean publish(String topic, byte[] payload) {

        if(!active) {
            return false;
        }

        try {

            client.publish(topic, payload, 0, false);
            return true;
        } catch (MqttException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode());
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode()));
            return  false;
        }
    }

    /**
     * sendet ein Topic an den Broker
     *
     * @param topic Topic
     * @param payload Inhalt
     * @param qos QoS
     * @return Erfolgsmeldung
     */
    public synchronized boolean publish(String topic, byte[] payload, int qos) {

        if(!active) {
            return false;
        }

        try {

            client.publish(topic, payload, qos, false);
            return true;
        } catch (MqttException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode());
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode()));
            return  false;
        }
    }

    /**
     * sendet ein Topic an den Broker
     *
     * @param topic Topic
     * @param payload Inhalt
     * @param retained Retained Nachricht
     * @return Erfolgsmeldung
     */
    public synchronized boolean publish(String topic, byte[] payload, boolean retained) {

        if(!active) {
            return false;
        }

        try {

            client.publish(topic, payload, 0, retained);
            return true;
        } catch (MqttException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode());
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode()));
            return  false;
        }
    }

    /**
     * sendet ein Topic an den Broker
     *
     * @param topic Topic
     * @param payload Inhalt
     * @param qos QoS
     * @param retained Retained Nachricht
     * @return Erfolgsmeldung
     */
    public synchronized boolean publish(String topic, byte[] payload, int qos, boolean retained) {

        if(!active) {
            return false;
        }

        try {

            client.publish(topic, payload, qos, retained);
            return true;
        } catch (MqttException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode());
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das MQTT Topic \"" + topic + "\" konnte nicht gesendet werden, Grund:" + e.getReasonCode()));
            return  false;
        }
    }

    /**
     * fügt einen neuen Topic Listener hinzu
     *
     * @param listener Topic Listener
     * @return Erfolgsmeldung
     */
    public boolean addTopicListener(TopicListener listener) {

        if(!active) {
            return false;
        }

        return callback.addTopicListener(listener);
    }

    /**
     * entfernt einen Topic Listener
     *
     * @param listener Topic Listener
     * @return Erfolgsmeldung
     */
    public boolean removeTopicListener(TopicListener listener) {

        if(!active) {
            return false;
        }

        return callback.addTopicListener(listener);
    }

    /**
     * beendet die Verbindung zum Broker
     */
    public void stopService() throws InterruptedException {

        if(client != null && client.isConnected()) {

            try {

                client.disconnect();
                client = null;

                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (MqttException e) {

                LoggerUtil.getLogger(this.getClass()).warning("Fehler beim Verbindungsabbau zum MQTT Broker \"" + brokerAddress + "\"");
            }
        }
    }
}
