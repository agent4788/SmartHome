package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;

public class TPLinkSensorHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private TPlinkSocket socket;

    /**
     * @param socket schaltbares Element
     */
    public TPLinkSensorHandler(TPlinkSocket socket) {

        Preconditions.checkNotNull(socket);
        this.socket = socket;
    }

    @Override
    public void run() {

        //TODO implementieren
    }
}
