package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.ScriptDouble;
import net.kleditzsch.SmartHome.model.automation.device.switchable.ScriptSingle;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScriptHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private ScriptSingle scriptSingle;

    /**
     * schaltbares Element
     */
    private ScriptDouble scriptDouble;

    /**
     * Schaltbefehl
     */
    private SwitchCommands switchCommand;

    /**
     * @param scriptSingle schaltbares Element
     */
    public ScriptHandler(ScriptSingle scriptSingle) {

        Preconditions.checkNotNull(scriptSingle);
        this.scriptSingle = scriptSingle;
    }

    /**
     * @param scriptDouble schaltbares Element
     */
    public ScriptHandler(ScriptDouble scriptDouble, SwitchCommands switchCommand) {

        Preconditions.checkNotNull(scriptDouble);
        Preconditions.checkNotNull(switchCommand);
        this.scriptDouble = scriptDouble;
        this.switchCommand = switchCommand;
    }

    /**
     * führt den Befehl aus (Nebenläufig)
     */
    @Override
    public void run() {

        List<String> cliCommand;
        Switchable.State newState = Switchable.State.OFF;
        boolean successfull = false;
        String workingDir;

        if(scriptSingle != null) {

            //Befehle für deaktivierte Geräte ignorieren
            if(scriptSingle.isDisabled()) {

                return;
            }

            //Einfacher Schaltbefehl
            cliCommand = scriptSingle.getCommand();
            workingDir = scriptSingle.getWorkingDir();

        } else {

            //Befehle für deaktivierte Geräte ignorieren
            if(scriptDouble.isDisabled()) {

                return;
            }

            //Doppelter Schaltbefehl
            if((switchCommand == SwitchCommands.on && !scriptDouble.isInverse())
                    || (switchCommand == SwitchCommands.off && scriptDouble.isInverse())) {

                //Einschalten
                cliCommand = scriptDouble.getOnCommand();
                newState = Switchable.State.ON;

            } else if (switchCommand == SwitchCommands.toggle) {

                //Umschalten
                if(scriptDouble.getState() == Switchable.State.ON) {

                    cliCommand = scriptDouble.getOffCommand();
                    newState = Switchable.State.OFF;
                } else {

                    cliCommand = scriptDouble.getOnCommand();
                    newState = Switchable.State.ON;
                }
            } else {

                //Ausschalten
                cliCommand = scriptDouble.getOffCommand();
                newState = Switchable.State.OFF;
            }
            workingDir = scriptDouble.getWorkingDir();
        }

        //Schaltbefehl ausführen
        ProcessBuilder builder = new ProcessBuilder(cliCommand);
        if(workingDir != null) {

            builder.directory(new File(workingDir));
        }

        try {

            Process process = builder.start();
            process.waitFor(30, TimeUnit.SECONDS);
            if(process.isAlive()) {

                //Nicht in 30 Sekunden abgeschlossen
                process.destroy();
                LoggerUtil.getLogger(this.getClass()).info("Der Befehl \"" + cliCommand + "\" wurde nach 30 Sekunden abgebrochen");
                MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Der Befehl \"" + cliCommand + "\" wurde nach 30 Sekunden abgebrochen"));

            } else {

                //Ausführung abgeschlossen
                if(process.exitValue() == 0) {

                    successfull = true;
                } else {

                    LoggerUtil.getLogger(this.getClass()).info("Der Befehl \"" + cliCommand + "\" wurde mit dem Exitcode " + process.exitValue() + " beendet");
                    MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Der Befehl \"" + cliCommand + "\" wurde mit dem Exitcode " + process.exitValue() + " beendet"));
                }
            }

        } catch (IOException e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Fehler beim Ausführen des Scriptes", e));
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        //neuen Status speichern (wenn erfolgreich ausgeführt)
        final Switchable.State finalNewState = newState;
        if(scriptSingle != null && successfull) {

            //Einfaches Script
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(scriptSingle.getId());
            switchableOptional.ifPresent(switchable -> {

                switchable.setLastToggleTime(LocalDateTime.now());
            });

            lock.unlock();
        } else if (scriptDouble != null && successfull) {

            //Doppeltes Script
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(scriptDouble.getId());
            switchableOptional.ifPresent(switchable -> {

                switchable.setState(finalNewState);
                switchable.setLastToggleTime(LocalDateTime.now());
            });

            lock.unlock();
        }
    }
}
