package net.kleditzsch.applications.network.model.devices;

import net.kleditzsch.smarthome.model.base.Element;

import java.util.Arrays;

/**
 * Netzwerkgeräte
 */
public class NetworkDevice extends Element implements Comparable<NetworkDevice> {

    /**
     * Verbindungs Typ
     */
    public enum LinkType {

        LAN,
        WLAN,
        DLAN,
        DECT
    }

    /**
     * Netzwerk Adresse
     */
    private String ipAddress = "";

    /**
     * Identifikator
     */
    private String ain = "";

    /**
     * Hardware Adresse
     */
    private String macAddress = "";

    /**
     * Verbindungs Typ
     */
    private LinkType linkType = LinkType.LAN;

    /**
     * Geschwindigkeit der Geräte Schnisttstelle
     */
    private String linkSpeed = "";

    /**
     * Host Name
     */
    private String hostName = "";

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
     * gibt den Identifikator zurück
     *
     * @return Identifikator
     */
    public String getAin() {
        return ain;
    }

    /**
     * setzt den Identifikator
     *
     * @param ain Identifikator
     */
    public void setAin(String ain) {

        this.ain = ain;
        setChangedData();
    }

    /**
     * gibt die Hardwareadresse zurück
     *
     * @return Hardwareadresse
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * setztd die Hardwareadresse
     *
     * @param macAddress Hardwareadresse
     */
    public void setMacAddress(String macAddress) {

        this.macAddress = macAddress;
        setChangedData();
    }

    /**
     * gibt den Verbindungstyp zurück
     *
     * @return Verbindungstyp
     */
    public LinkType getLinkType() {
        return linkType;
    }

    /**
     * setzt den Verbindungstyp
     *
     * @param linkType Verbindungstyp
     */
    public void setLinkType(LinkType linkType) {

        this.linkType = linkType;
        setChangedData();
    }

    /**
     * gibt die Schnittstellengeschwindigkeit des Gerätes zurück
     *
     * @return Schnittstellengeschwindigkeit des Gerätes
     */
    public String getLinkSpeed() {
        return linkSpeed;
    }

    /**
     * setzt die Schnittstellengeschwindigkeit des Gerätes
     *
     * @param linkSpeed Schnittstellengeschwindigkeit des Gerätes
     */
    public void setLinkSpeed(String linkSpeed) {

        this.linkSpeed = linkSpeed;
        setChangedData();
    }

    /**
     * gibt den Hostnamen des Gerätes zurück
     *
     * @return Hostname des Gerätes
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * setzt den Hostnamen des Gerätes
     *
     * @param hostName Hostname des Gerätes
     */
    public void setHostName(String hostName) {

        this.hostName = hostName;
        setChangedData();
    }

    /**
     * Sortierfunktion für IPv4 Adressen
     *
     * @param otherDevice Vergleichsobjekt
     * @return Sortierung
     */
    @Override
    public int compareTo(NetworkDevice otherDevice) {

        int[] ipParts = Arrays.stream(this.getIpAddress().split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] otherIpParts = Arrays.stream(otherDevice.getIpAddress().split("\\.")).mapToInt(Integer::parseInt).toArray();

        for(int i = 0; i < 4; i++) {

            if(ipParts[i] < otherIpParts[i]) {

                return -1;
            } else if(ipParts[i] > otherIpParts[i]) {

                return 1;
            }
        }
        return 0;
    }
}
