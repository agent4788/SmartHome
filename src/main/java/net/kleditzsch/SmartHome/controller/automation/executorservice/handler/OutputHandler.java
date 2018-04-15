package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Output;

public class OutputHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private Output output;

    /**
     * Schaltbefehl
     */
    private Command.SWITCH_COMMAND switchCommand;

    /**
     * @param output schaltbares Element
     * @param switchCommand Schaltbefehl
     */
    public OutputHandler(Output output, Command.SWITCH_COMMAND switchCommand) {
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
