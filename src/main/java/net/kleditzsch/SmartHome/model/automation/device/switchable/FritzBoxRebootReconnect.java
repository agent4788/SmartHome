package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;

/**
 * Fritz!Box Neustart und Neu Verbinden
 */
public class FritzBoxRebootReconnect extends SingleSwitchable {

    private Type type = Type.SWITCHABLE_FRITZ_BOX_REBOOT_RECONNECT;

    enum Function {

        @SerializedName("REBOOT")
        REBOOT,
        @SerializedName("RECONNECT_WAN")
        RECONNECT_WAN
    }

    /**
     * Funktion
     */
    private Function function = Function.RECONNECT_WAN;

    public FritzBoxRebootReconnect() {}

    /**
     * @param id ID
     * @param name Name
     */
    public FritzBoxRebootReconnect(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt die Funktion zurück
     *
     * @return Funktion
     */
    public Function getFunction() {
        return function;
    }

    /**
     * setzt die Funktion
     *
     * @param function Funktion
     */
    public void setFunction(Function function) {

        Preconditions.checkNotNull(function);
        this.function = function;
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
