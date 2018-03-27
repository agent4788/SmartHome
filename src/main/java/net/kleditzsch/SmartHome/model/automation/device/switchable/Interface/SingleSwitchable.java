package net.kleditzsch.SmartHome.model.automation.device.switchable.Interface;

import net.kleditzsch.SmartHome.global.base.ID;

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
    }
}
