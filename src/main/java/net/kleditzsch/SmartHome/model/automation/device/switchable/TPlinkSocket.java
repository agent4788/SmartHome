package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;

/**
 * AVM Steckdose
 */
public class TPlinkSocket extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_TPLINK_SOCKET;

    /**
     * Steckdosen Typ
     */
    public enum SOCKET_TYPE {

        @SerializedName("HS100")
        HS100,
        @SerializedName("HS110")
        HS110
    }

    /**
     * IP Adresse
     */
    private String ipAddress = "";

    /**
     * Port
     */
    private int port = 9999;

    /**
     * Steckdosentyp
     */
    private SOCKET_TYPE socketType = SOCKET_TYPE.HS100;

    /**
     * Sensorwerte der Steckdose
     */
    private ID voltageSensor, currentSensor, powerSensorId, energySensorId;

    public TPlinkSocket() {}

    /**
     * @param id ID
     * @param name Name
     */
    public TPlinkSocket(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt die IP Adresse der Steckdose zurück
     *
     * @return IP Adresse
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * setzt die IP Adresse der Steckdose
     *
     * @param ipAddress IP Adresse
     */
    public void setIpAddress(String ipAddress) {

        Preconditions.checkNotNull(ipAddress);
        Preconditions.checkArgument(InetAddresses.isInetAddress(ipAddress));
        this.ipAddress = ipAddress;
    }

    /**
     * gibt den Port der Steckdose zurück
     *
     * @return Port
     */
    public int getPort() {
        return port;
    }

    /**
     * setzt den Port der Steckdose
     *
     * @param port Port
     */
    public void setPort(int port) {

        Preconditions.checkNotNull(port);
        Preconditions.checkArgument((port >= 0 && port <= 65535));
        this.port = port;
    }

    /**
     * gibt den Steckdosentyp zurück
     *
     * @return Steckdosentyp
     */
    public SOCKET_TYPE getSocketType() {
        return socketType;
    }

    /**
     * setzt den Steckdosentyp
     *
     * @param socketType Steckdosentyp
     */
    public void setSocketType(SOCKET_TYPE socketType) {
        this.socketType = socketType;
    }

    /**
     * gibt die ID des Spannungs Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getVoltageSensor() {
        return voltageSensor;
    }

    /**
     * setzt die ID des Spannungs Sensorwertes
     *
     * @param voltageSensor Sensor ID
     */
    public void setVoltageSensor(ID voltageSensor) {
        this.voltageSensor = voltageSensor;
    }

    /**
     * gibt die ID des Strom Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getCurrentSensor() {
        return currentSensor;
    }

    /**
     * setzt die ID des Strom Sensorwertes
     *
     * @param currentSensor Sensor ID
     */
    public void setCurrentSensor(ID currentSensor) {
        this.currentSensor = currentSensor;
    }

    /**
     * gibt die ID des Verbrauchs Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getPowerSensorId() {
        return powerSensorId;
    }

    /**
     * setzt die ID des Verbrauchs Sensorwertes
     *
     * @param powerSensorId Sensor ID
     */
    public void setPowerSensorId(ID powerSensorId) {

        Preconditions.checkNotNull(powerSensorId);
        this.powerSensorId = powerSensorId;
    }

    /**
     * gibt die ID des Energie Sensorwertes zurück
     *
     * @return Sensor ID
     */
    public ID getEnergySensorId() {
        return energySensorId;
    }

    /**
     * setzt die ID des Energie Sensorwertes
     *
     * @param energySensorId Sensor ID
     */
    public void setEnergySensorId(ID energySensorId) {

        Preconditions.checkNotNull(energySensorId);
        this.energySensorId = energySensorId;
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
