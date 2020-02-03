package net.kleditzsch.SmartHome.controller.automation.switchtimerservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
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

                        //prüfen ob schaltzeit schon mehr als 10 Minuten vorbei
                        if(nextExecutionTime == null || nextExecutionTime.isBefore(now.minusMinutes(10))) {

                            //nächste Schaltzeit neu berechnen
                            LocalDateTime newNextExecutionTime = ste.calculateNextExecutionTime(st);
                            nextExecutionTime = newNextExecutionTime;
                            st.setNextExecutionTime(newNextExecutionTime);
                        }

                        //Prüfen ob Schaltzeit erreicht
                        return nextExecutionTime.isBefore(now) || nextExecutionTime.isEqual(now);
                    })
                    .forEach(st -> {

                        //Schaltbefehle an den Scheduler übergeben
                        ActorEditor se = Application.getInstance().getAutomation().getActorEditor();
                        ReentrantReadWriteLock.ReadLock seLock = se.readLock();
                        seLock.lock();

                        st.getCommands().forEach(switchCommand -> {

                            //prüfen ob schaltbares Element vorhanden
                            Optional<Actor> actorOptional = se.getById(switchCommand.getSwitchableId());
                            if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                                //Schaltbefehl an den Schaduler übergeben
                                Actor actor = se.copyOf(actorOptional.get());
                                if(!actor.isDisabled()) {

                                    executorService.putCommand(new SwitchCommand((Switchable) actor, switchCommand.getCommand()));
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
