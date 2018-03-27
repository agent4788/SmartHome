package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;

/**
 * Fritz!BOx WLan Funktionen
 */
public class FritzBoxWirelessLan extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_FRITZ_BOX_WLAN;

    enum Wlan {

        @SerializedName("_2GHZ")
        _2GHZ,
        @SerializedName("_5GHZ")
        _5GHZ,
        @SerializedName("GUEST")
        GUEST
    }

    /**
     * Funktion
     */
    private Wlan wlan = Wlan.GUEST;

    public FritzBoxWirelessLan() {}

    /**
     * @param id ID
     * @param name Name
     */
    public FritzBoxWirelessLan(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt das Wlan zurück
     *
     * @return Wlan
     */
    public Wlan getWlan() {
        return wlan;
    }

    /**
     * setzt das Wlan Funktion
     *
     * @param wlan Wlan
     */
    public void setWlan(Wlan wlan) {

        Preconditions.checkNotNull(wlan);
        this.wlan = wlan;
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
