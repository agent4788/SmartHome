package net.kleditzsch.SmartHome.controller.automation.executorservice;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SensorValueCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.StopCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.handler.*;
import net.kleditzsch.SmartHome.model.automation.device.sensor.LiveBitValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.UserAtHomeValue;
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

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
                        if(switchCommand.getSwitchCommands() == SwitchCommands.on || switchCommand.getSwitchCommands() == SwitchCommands.off || switchCommand.getSwitchCommands() == SwitchCommands.toggle) {

                            TpLinkSwitchHandler handler = new TpLinkSwitchHandler((TPlinkSocket) switchCommand.getSwitchable(), switchCommand.getSwitchCommands());
                            executor.execute(handler);
                        } else if(switchCommand.getSwitchCommands() == SwitchCommands.updateState) {

                            TpLinkStateHandler handler = new TpLinkStateHandler((TPlinkSocket) switchCommand.getSwitchable());
                            executor.execute(handler);
                        } else if(switchCommand.getSwitchCommands() == SwitchCommands.updateSensor) {

                            TPLinkSensorHandler handler = new TPLinkSensorHandler((TPlinkSocket) switchCommand.getSwitchable());
                            executor.execute(handler);
                        }
                    } else if(switchCommand.getSwitchable() instanceof AvmSocket) {

                        //AVM Steckdose
                        AvmHandler handler = new AvmHandler((AvmSocket) switchCommand.getSwitchable(), switchCommand.getSwitchCommands());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof ScriptSingle) {

                        //Einfaches Script
                        ScriptHandler handler = new ScriptHandler((ScriptSingle) switchCommand.getSwitchable());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof ScriptDouble) {

                        //Doppeltes Script
                        ScriptHandler handler = new ScriptHandler((ScriptDouble) switchCommand.getSwitchable(), switchCommand.getSwitchCommands());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof WakeOnLan) {

                        //WakeOnLan
                        WakeOnLanHandler handler = new WakeOnLanHandler((WakeOnLan) switchCommand.getSwitchable());
                        executor.execute(handler);
                    } else if(switchCommand.getSwitchable() instanceof Output) {

                        //Ausgang
                        OutputHandler handler = new OutputHandler((Output) switchCommand.getSwitchable(), switchCommand.getSwitchCommands());
                        executor.execute(handler);
                    }
                } else if(command instanceof SensorValueCommand) {

                    //Sensorwerte aktualisieren
                    SensorValueCommand sensorValueCommand = (SensorValueCommand) command;
                    if(sensorValueCommand.getSensorValue() instanceof LiveBitValue) {

                        //Live Bit aktualisieren
                        LiveBitSensorHandler handler = new LiveBitSensorHandler((LiveBitValue) sensorValueCommand.getSensorValue());
                        executor.execute(handler);
                    } else if (sensorValueCommand.getSensorValue() instanceof UserAtHomeValue) {

                        //Benutzer zu Hause aktualisieren
                        UserAtHomeSensorHandler handler = new UserAtHomeSensorHandler((UserAtHomeValue) sensorValueCommand.getSensorValue());
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
