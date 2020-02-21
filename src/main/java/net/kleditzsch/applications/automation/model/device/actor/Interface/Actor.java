package net.kleditzsch.applications.automation.model.device.actor.Interface;

import net.kleditzsch.applications.automation.model.device.AutomationElement;

/**
 * Schnittstelle zur Markierung als Aktor
 */
public abstract class Actor extends AutomationElement {

    /**
     * gibt an ob das Element ein schaltbares Element ist
     *
     * @return schaltbares Element
     */
    public abstract boolean isSwitchable();

    /**
     * gibt an ob das Element ein einfaches schaltbares Element ist
     *
     * @return einfaches schaltbares Element
     */
    public abstract boolean isSingleSwitchable();

    /**
     * gibt an ob das Element ein doppeltes schaltbares Element ist
     *
     * @return doppeltes schaltbares Element
     */
    public abstract boolean isDoubleSwitchable();

    /**
     * gibt an ob das Element ein niveau Element ist
     *
     * @return niveau Element
     */
    public abstract boolean isLevel();
}
