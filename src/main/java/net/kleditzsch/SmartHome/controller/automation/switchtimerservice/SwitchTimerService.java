package net.kleditzsch.SmartHome.controller.automation.switchtimerservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.MoveShutterCommand;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.automation.global.MoveCommand;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Timer Service
 */
public class SwitchTimerService implements Runnable {

    @Override
    public void run() {

        try {

            SwitchTimerEditor ste = Application.getInstance().getAutomation().getSwitchTimerEditor();
            ReentrantReadWriteLock.WriteLock lock = ste.writeLock();
            lock.lock();

            //alle Timer durchlaufen und prüfen ob Schaltzeit erreicht
            ExecutorService executorService = Application.getInstance().getAutomation().getExecutorService();
            ste.getData().stream()
                    .filter(st -> !st.isDisabled())
                    .filter(st -> {

                        //prüfen ob schaltzeit erreicht
                        LocalDateTime nextExecutionTime = st.getNextExecutionTime();
                        LocalDateTime now = LocalDateTime.now();

                        //prüfen ob schaltzeit schon mehr als 15 Minuten vorbei
                        if(nextExecutionTime == null || nextExecutionTime.isBefore(now.minusMinutes(15))) {

                            //nächste Schaltzeit berechnen
                            LocalDateTime newNextExecutionTime = ste.calculateNextExecutionTime(st);
                            nextExecutionTime = newNextExecutionTime;
                            st.setNextExecutionTime(newNextExecutionTime);
                        }

                        //Prüfen ob Schaltzeit erreicht
                        return nextExecutionTime.isBefore(now) || nextExecutionTime.isEqual(now);
                    })
                    .forEach(st -> {

                        //Schaltbefehle an den Scheduler übergeben
                        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
                        ReentrantReadWriteLock.ReadLock seLock = actorEditor.readLock();
                        seLock.lock();

                        //schaltbefehle
                        st.getCommands().stream()
                                .filter(e -> e instanceof net.kleditzsch.SmartHome.model.automation.global.SwitchCommand)
                                .map(e -> (net.kleditzsch.SmartHome.model.automation.global.SwitchCommand) e)
                                .forEach(switchCommand -> {

                                    //prüfen ob schaltbares Element vorhanden
                                    Optional<Actor> actorOptional = actorEditor.getById(switchCommand.getSwitchableId());
                                    if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                                        //Schaltbefehl an den Schaduler übergeben
                                        Actor actor = actorEditor.copyOf(actorOptional.get());
                                        if(!actor.isDisabled()) {

                                            executorService.putCommand(new SwitchCommand((Switchable) actorEditor.copyOf(actor), switchCommand.getCommand()));
                                        }
                                    }
                                });

                        //Rollläden
                        st.getCommands().stream()
                                .filter(e -> e instanceof MoveCommand)
                                .map(e -> (MoveCommand) e)
                                .forEach(moveCommand -> {

                                    //prüfen ob Rolladen vorhanden
                                    Optional<Actor> actorOptional = actorEditor.getById(moveCommand.getShutterId());
                                    if(actorOptional.isPresent() && actorOptional.get() instanceof Shutter) {

                                        //Schaltbefehl an den Schaduler übergeben
                                        Actor actor = actorEditor.copyOf(actorOptional.get());
                                        if(!actor.isDisabled()) {

                                            executorService.putCommand(new MoveShutterCommand((Shutter) actorEditor.copyOf(actor), moveCommand.getTargetLevel()));
                                        }
                                    }
                                });

                        //nächste Schaltzeit berechnen
                        st.setNextExecutionTime(ste.calculateNextExecutionTime(st));
                        LoggerUtil.getLogger(this).finest("Timer \"" + st.getName() + "\" geschaltet; nächste Ausführung: " + st.getNextExecutionTime());
                        MessageEditor.addMessage(new Message("automation", Message.Type.info, "Timer \"" + st.getName() + "\" geschaltet; nächste Ausführung: " + st.getNextExecutionTime()));

                        seLock.unlock();
                    });

            lock.unlock();
        } catch (Exception e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this), "Fehler beim Ausführen des Timers", e);
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Fehler beim Ausführen des Timers", e));
        }
    }
}
