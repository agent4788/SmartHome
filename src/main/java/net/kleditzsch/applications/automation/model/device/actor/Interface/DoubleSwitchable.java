package net.kleditzsch.applications.automation.model.device.actor.Interface;

import java.time.LocalDateTime;

/**
 * schaltbares Element mit 2 Schaltmöglichkeiten (an/aus)
 */
public abstract class DoubleSwitchable extends Switchable {

    /**
     * Invertierung
     */
    private boolean inverse = false;

    /**
     * gibt an ob die Invertierung aktiviert/deaktiviert ist
     *
     * @return aktiviert/deaktiviert
     */
    public boolean isInverse() {
        return inverse;
    }

    /**
     * aktiviert/deaktiviert die Invertierung
     *
     * @param inverse aktiviert/deaktiviert
     */
    public void setInverse(boolean inverse) {

        this.inverse = inverse;
        setChangedData();
    }

    /**
     * Aktion die bei Betätigung des "an" Buttons ausgeführt wird
     */
    public void updateTriggerOn() {

        setState(inverse ? State.OFF : State.ON);
        setLastToggleTime(LocalDateTime.now());
        setChangedData();
    }

    /**
     * Aktion die bei Betätigung des "aus" Buttons ausgeführt wird
     */
    public void updateTriggerOff() {

        setState(inverse ? State.ON : State.OFF);
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

        return false;
    }

    /**
     * gibt an ob das Element ein doppeltes schaltbares Element ist
     *
     * @return doppeltes schaltbares Element
     */
    public boolean isDoubleSwitchable() {

        return true;
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
