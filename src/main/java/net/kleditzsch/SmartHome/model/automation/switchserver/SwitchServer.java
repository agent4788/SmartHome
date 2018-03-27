package net.kleditzsch.SmartHome.model.automation.switchserver;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateIpAddress;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

/**
 * Schaltserver
 */
public class SwitchServer extends Element {

    /**
     * IP Adresse
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateIpAddress(errorCode = 1003, message = "Ungültige IP Adresse %s")
    private String ipAddress;

    /**
     * Port
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10004, message = "Ungültiger Port %s")
    @ValidateMax(value = 65535, errorCode = 10004, message = "Ungültiger Port %s")
    private int port;

    /**
     * Timeout in ms
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10005, message = "Ungültiger Timeout %s, der Timeout ist kleiner als %sms")
    @ValidateMax(value = 10_000, errorCode = 10005, message = "Ungültiger Timeout %s, der Timeout ist größer als %sms")
    private int timeout = 500;

    /**
     * aktiviert?
     */
    private boolean enabled = true;

    /**
     * System Schaltserver (Kann nicht gelöscht werden)
     */
    private boolean isSystemSwitchServer = false;

    /**
     * @param id ID
     * @param name Name
     * @param ipAddress IP Adresse
     * @param port Port
     * @param isSystemSwitchServer System Schaltserver
     */
    public SwitchServer(ID id, String name, String ipAddress, int port, boolean isSystemSwitchServer) {

        setId(id);
        setName(name);
        setIpAddress(ipAddress);
        setPort(port);
        this.isSystemSwitchServer = isSystemSwitchServer;

    }

    /**
     * @param id ID
     * @param name Name
     * @param ipAddress IP Adresse
     * @param port Port
     * @param isSystemSwitchServer System Schaltserver
     * @param enabled Schaltserver aktiviert
     */
    public SwitchServer(ID id, String name, String ipAddress, int port, boolean isSystemSwitchServer, boolean enabled) {

        setId(id);
        setName(name);
        setIpAddress(ipAddress);
        setPort(port);
        setEnabled(enabled);
        this.isSystemSwitchServer = isSystemSwitchServer;

    }

    /**
     * gibt die IP Adresse zurück
     *
     * @return IP Adresse
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * setzt die IP Adresse
     *
     * @param ipAddress IP Adresse
     */
    public void setIpAddress(String ipAddress) {

        this.ipAddress = ipAddress;
    }

    /**
     * gibt den Port zurück
     *
     * @return Port
     */
    public int getPort() {
        return port;
    }

    /**
     * setzt den Port
     *
     * @param port Port
     */
    public void setPort(int port) {

        this.port = port;
    }

    /**
     * gibt den Timeout in ms zurück
     *
     * @return Timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * setzt den Timeout in ms
     *
     * @param timeout Timeout
     */
    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    /**
     * gibt an ob der Schaltserver aktiviert/deaktiviert ist
     *
     * @return aktiviert/deaktiviert
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * aktiviert/deaktiviert den Schaltserver
     *
     * @param enabled aktiviert/deaktiviert
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * gibt an ob der Schaltserver ein System Schaltserver ist
     *
     * @return Systemschaltserver
     */
    public boolean isSystemSwitchServer() {
        return isSystemSwitchServer;
    }
}
