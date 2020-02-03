package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.ScriptDouble;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.ScriptSingle;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
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
            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            Optional<Actor> actorOptional = actorEditor.getById(scriptSingle.getId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                Switchable switchable = (Switchable) actorOptional.get();
                switchable.setLastToggleTime(LocalDateTime.now());
            }

            lock.unlock();
        } else if (scriptDouble != null && successfull) {

            //Doppeltes Script
            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            Optional<Actor> actorOptional = actorEditor.getById(scriptDouble.getId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                Switchable switchable = (Switchable) actorOptional.get();
                switchable.setState(finalNewState);
                switchable.setLastToggleTime(LocalDateTime.now());
            }

            lock.unlock();
        }
    }
}
