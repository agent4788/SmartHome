package net.kleditzsch.applications.automation.model.device.actor.switchable;

import net.kleditzsch.applications.automation.model.device.actor.Interface.MqttElement;
import net.kleditzsch.applications.automation.model.device.actor.Interface.SingleSwitchable;

public class MqttSingle extends SingleSwitchable implements MqttElement {

    private Type type = Type.SWITCHABLE_MQTT_SINGLE;

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
