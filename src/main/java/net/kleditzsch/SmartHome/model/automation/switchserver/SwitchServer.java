package net.kleditzsch.SmartHome.model.automation.switchserver;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;

/**
 * Schaltserver
 */
public class SwitchServer extends Element {

    /**
     * IP Adresse
     */
    private String ipAddress;

    /**
     * Port
     */
    private int port;

    /**
     * Timeout in ms
     */
    private int timeout = 500;

    /**
     * aktiviert?
     */
    private boolean disabled = false;

    /**
     * @param id ID
     * @param name Name
     * @param ipAddress IP Adresse
     * @param port Port
     * @param disabled Schaltserver aktiviert
     */
    public SwitchServer(ID id, String name, String ipAddress, int port, boolean disabled) {

        setId(id);
        setName(name);
        setIpAddress(ipAddress);
        setPort(port);
        setDisabled(disabled);
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
        setChangedData();
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
        setChangedData();
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
        setChangedData();
    }

    /**
     * gibt an ob der Schaltserver aktiviert/deaktiviert ist
     *
     * @return aktiviert/deaktiviert
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * aktiviert/deaktiviert den Schaltserver
     *
     * @param disabled aktiviert/deaktiviert
     */
    public void setDisabled(boolean disabled) {

        this.disabled = disabled;
        setChangedData();
    }
}
