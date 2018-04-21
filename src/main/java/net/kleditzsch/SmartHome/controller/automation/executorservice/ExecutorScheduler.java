package net.kleditzsch.SmartHome.controller.automation.executorservice;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.StopCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.handler.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class ExecutorScheduler implements Runnable {

    private BlockingQueue<Command> queue;

    private ExecutorService executor;

    public ExecutorScheduler(BlockingQueue<Command> queue, ExecutorService executor) {

        this.queue = queue;
        this.executor = executor;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                //Auf schaltbefehle Warten
                Command command = queue.take();

                if(command instanceof SwitchCommand) {

                    SwitchCommand switchCommand = ((SwitchCommand) command);
                    if(switchCommand.getSwitchable() instanceof TPlinkSocket) {

                        //TP Link Steckdosen
                        if(switchCommand.getSwitchCommand() == Command.SWITCH_COMMAND.ON || switchCommand.getSwitchCommand() == Command.SWITCH_COMMAND.OFF) {

                            TpLinkSwitchHandler handler = new TpLinkSwitchHandler((TPlinkSocket) switchCommand.getSwitchable(), switchCommand.getSwitchCommand());
                            executor.execute(handler);
                        } else if(switchCommand.getSwitchCommand() == Command.SWITCH_COMMAND.UPDATE) {

                            TpLinkStateHandler handler = new TpLinkStateHandler((TPlinkSocket) switchCommand.getSwitchable());
                            executor.execute(handler);
                        } else if(switchCommand.getSwitchCommand() == Command.SWITCH_COMMAND.UPDATE_SENSOR) {

                            TPLinkSensorHandler handler = new TPLinkSensorHandler((TPlinkSocket) switchCommand.getSwitchable());
                            executor.execute(handler);
                        }
                    } else if(switchCommand.getSwitchable() instanceof AvmSocket) {

                        //AVM Steckdose
                        AvmHandler handler = new AvmHandler((AvmSocket) switchCommand.getSwitchable(), switchCommand.getSwitchCommand());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof ScriptSingle) {

                        //Einfaches Script
                        ScriptHandler handler = new ScriptHandler((ScriptSingle) switchCommand.getSwitchable());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof ScriptDouble) {

                        //Doppeltes Script
                        ScriptHandler handler = new ScriptHandler((ScriptDouble) switchCommand.getSwitchable(), switchCommand.getSwitchCommand());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof WakeOnLan) {

                        //WakeOnLan
                        WakeOnLanHandler handler = new WakeOnLanHandler((WakeOnLan) switchCommand.getSwitchable());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof Output) {

                        //Ausgang
                        OutputHandler handler = new OutputHandler((Output) switchCommand.getSwitchable(), switchCommand.getSwitchCommand());
                        executor.execute(handler);
                    }
                } else if(command instanceof StopCommand) {

                    //Thread beenden
                    Thread.currentThread().interrupt();
                }

            } catch (InterruptedException e) {

                //Thread beenden
                Thread.currentThread().interrupt();
            }
        }
    }
}
