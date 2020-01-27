package net.kleditzsch.SmartHome.model.automation.device.switchable;

import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;

public class MqttSingle extends SingleSwitchable {

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
     * detzt den MQTT Name des Gerätes
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
