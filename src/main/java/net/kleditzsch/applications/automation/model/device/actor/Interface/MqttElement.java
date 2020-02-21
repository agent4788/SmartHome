package net.kleditzsch.applications.automation.model.device.actor.Interface;

public interface MqttElement {

    /**
     * gibt den MQTT Name des Gerätes zurück
     *
     * @return MQTT Name des Gerätes
     */
    String getMqttName();

    /**
     * setzt den MQTT Name des Gerätes
     *
     * @param mqttName MQTT Name des Gerätes
     */
    void setMqttName(String mqttName);
}
