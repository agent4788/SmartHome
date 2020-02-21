package net.kleditzsch.applications.automation.model.device.actor.switchable;

import net.kleditzsch.applications.automation.model.device.actor.Interface.DoubleSwitchable;
import net.kleditzsch.applications.automation.model.device.actor.Interface.MqttElement;

public class MqttDouble extends DoubleSwitchable implements MqttElement {

    private Type type = Type.SWITCHABLE_MQTT_DOUBLE;

    /**
     * MQTT Name des Gerätes
     */
    private String mqttName = "";

    /**
     * gibt den MQTT Name des Gerätes zurück
     *
     * @return MQTT Name des Gerätes
     */
    public String getMqttName() {
        return mqttName;
    }

    /**
     * setzt den MQTT Name des Gerätes
     *
     * @param mqttName MQTT Name des Gerätes
     */
    public void setMqttName(String mqttName) {

        this.mqttName = mqttName;
        setChangedData();
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
