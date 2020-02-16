package net.kleditzsch.apps.automation.model.device.actor.shutter;

import com.google.common.base.Preconditions;
import net.kleditzsch.apps.automation.model.device.actor.Interface.Shutter;
import net.kleditzsch.apps.automation.model.device.actor.Interface.MqttElement;

public class MqttShutter extends Shutter implements MqttElement {

    private Type type = Type.SHUTTER_MQTT;

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
     * gibt den Status des Rolladens zurück
     *
     * @return Status des Rolladens
     */
    public int getLevel() {
        return super.getLevel();
    }

    /**
     * setzt den Status des Rolladens
     *
     * @param level Status des Rolladens
     */
    public void setLevel(int level) {

        Preconditions.checkArgument(level >= 0 && level <= 100);
        super.setLevel(level);
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
