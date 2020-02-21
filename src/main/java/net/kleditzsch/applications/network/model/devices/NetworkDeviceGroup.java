package net.kleditzsch.applications.network.model.devices;

import net.kleditzsch.smarthome.model.base.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Gruppe von Netzwerk Geräten
 */
public class NetworkDeviceGroup extends Element {

    /**
     * Liste der Netzwerkgeräte
     */
    private List<NetworkDevice> devices = new ArrayList<>();

    /**
     * gibt die Liste der Netzwerkgeräte zurück
     *
     * @return Liste der Netzwerkgeräte
     */
    public List<NetworkDevice> getDevices() {
        return devices;
    }
}
