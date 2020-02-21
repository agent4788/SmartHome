package net.kleditzsch.applications.automation.controller.executorservice.handler;

import net.kleditzsch.applications.automation.model.device.actor.switchable.WakeOnLan;

/**
 * sendet WakeOnLan befehle über das Netzwerk
 */
public class WakeOnLanHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private WakeOnLan wol;

    /**
     * @param wol schaltbares Element
     */
    public WakeOnLanHandler(WakeOnLan wol) {
        this.wol = wol;
    }

    /**
     * führt den Befehl aus
     */
    @Override
    public void run() {

        //Befehle für deaktivierte Geräte ignorieren
        if(wol.isDisabled()) {

            return;
        }

        net.kleditzsch.applications.automation.api.wol.WakeOnLan.send(wol.getIpAddress(), wol.getMac());
    }
}
