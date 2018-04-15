package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import net.kleditzsch.SmartHome.model.automation.device.switchable.WakeOnLan;

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

        net.kleditzsch.SmartHome.util.api.wol.WakeOnLan.send(wol.getIpAddress(), wol.getMac());
    }
}
