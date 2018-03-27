package net.kleditzsch.SmartHome.model.automation.device.switchable;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;

/**
 * Ausgang
 */
public class Output extends DoubleSwitchable {

    private Type type = Type.SWITCHABLE_OUTPUT;

    /**
     * Schaltserver ID
     */
    private ID switchServerId;

    /**
     * Pin
     */
    private int pin;

    public Output() {}

    /**
     * @param id ID
     * @param name Name
     */
    public Output(ID id, String name) {

        setId(id);
        setName(name);
    }

    /**
     * gibt die Schaltserver ID zurück
     *
     * @return Schaltserver ID
     */
    public ID getSwitchServerId() {
        return switchServerId;
    }

    /**
     * setzt die Schaltserver ID
     *
     * @param switchServerId Schaltserver ID
     */
    public void setSwitchServerId(ID switchServerId) {

        Preconditions.checkNotNull(switchServerId);
        this.switchServerId = switchServerId;
    }

    /**
     * gibt den GPIO Pin zurück
     *
     * @return GPIO Pin
     */
    public int getPin() {
        return pin;
    }

    /**
     * setzt den GPIO Pin
     *
     * @param pin GPIO Pin
     */
    public void setPin(int pin) {

        Preconditions.checkNotNull(pin);
        Preconditions.checkArgument(pin >= 0 && pin <= 100, "Ungültiger Pin %i", pin);
        this.pin = pin;
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
