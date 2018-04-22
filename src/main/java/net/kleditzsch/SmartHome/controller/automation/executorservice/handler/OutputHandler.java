package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import net.kleditzsch.SmartHome.model.automation.device.switchable.Output;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

import javax.swing.*;

public class OutputHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private Output output;

    /**
     * Schaltbefehl
     */
    private SwitchCommands switchCommand;

    /**
     * @param output schaltbares Element
     * @param switchCommand Schaltbefehl
     */
    public OutputHandler(Output output, SwitchCommands switchCommand) {
        this.output = output;
        this.switchCommand = switchCommand;
    }

    /**
     * führt den Befehl aus
     */
    @Override
    public void run() {

        //TODO implementieren wenn Schnittstelle klar ist
    }
}
