package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;

/**
 * WAKE On Lan
 */
public class WakeOnLan extends SingleSwitchable {

    private Type type = Type.SWITCHABLE_WAKE_ON_LAN;

    /**
     * MAC Adresse
     */
    private String mac;

    /**
     * Broadcast IP Adresse
     */
    private String ipAddress;

    public WakeOnLan() {}

    /**
     * @param id ID
     * @param name Name
     */
    public WakeOnLan(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt die MAC Adresse zurück
     *
     * @return MAC Adresse
     */
    public String getMac() {
        return mac;
    }

    /**
     * setzt die MAC Adresse
     *
     * @param mac MAC Adresse
     */
    public void setMac(String mac) {

        Preconditions.checkNotNull(mac);
        Preconditions.checkArgument(mac.matches("^[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}$"), "Ungültige MAC Adresse %s", mac);
        this.mac = mac;
        setChangedData();
    }

    /**
     * gibt die Broadcast IP Adresse zurück
     *
     * @return Broadcast IP Adresse
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * setzt die Broadcast IP Adresse
     *
     * @param ipAddress Broadcast IP Adresse
     */
    public void setIpAddress(String ipAddress) {

        Preconditions.checkNotNull(ipAddress);
        Preconditions.checkArgument(InetAddresses.isInetAddress(ipAddress), "Ungültige IP Adresse %s", ipAddress);
        this.ipAddress = ipAddress;
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
