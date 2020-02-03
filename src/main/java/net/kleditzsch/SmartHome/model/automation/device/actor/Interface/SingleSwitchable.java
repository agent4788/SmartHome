package net.kleditzsch.SmartHome.model.automation.device.actor.Interface;

import java.time.LocalDateTime;

/**
 * schaltbares Element mit einer Schaltmögkichkeit
 */
public abstract class SingleSwitchable extends Switchable {

    /**
     * Aktion die bei Betätigung des "an" Buttons ausgeführt wird
     */
    public void updateTriggerOn() {

        setLastToggleTime(LocalDateTime.now());
        setChangedData();
    }

    /**
     * gibt an ob das Element ein schaltbares Element ist
     *
     * @return schaltbares Element
     */
    public boolean isSwitchable() {

        return true;
    }

    /**
     * gibt an ob das Element ein einfaches schaltbares Element ist
     *
     * @return einfaches schaltbares Element
     */
    public boolean isSingleSwitchable() {

        return true;
    }

    /**
     * gibt an ob das Element ein doppeltes schaltbares Element ist
     *
     * @return doppeltes schaltbares Element
     */
    public boolean isDoubleSwitchable() {

        return false;
    }

    /**
     * gibt an ob das Element ein niveau Element ist
     *
     * @return niveau Element
     */
    public boolean isLevel() {

        return false;
    }
}
