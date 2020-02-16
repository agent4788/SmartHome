package net.kleditzsch.apps.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.apps.automation.model.device.AutomationElement;
import net.kleditzsch.apps.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.apps.automation.model.device.actor.Interface.Switchable;
import net.kleditzsch.apps.automation.model.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.apps.automation.model.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.apps.automation.api.tplink.HS100;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TpLinkStateHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private TPlinkSocket socket;

    /**
     * @param socket schaltbares Element
     */
    public TpLinkStateHandler(TPlinkSocket socket) {

        Preconditions.checkNotNull(socket);
        this.socket = socket;
    }

    @Override
    public void run() {

        //Befehle für deaktivierte Geräte ignorieren
        if(socket.isDisabled()) {

            return;
        }

        ReentrantReadWriteLock.WriteLock lock = null;
        boolean success = false;
        Exception exception = null;
        for(int i = 0; i < 3; i++) {

            try {

                HS100 hs100 = new HS100(socket.getIpAddress(), socket.getPort());
                int state = hs100.readState();

                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
                lock = actorEditor.writeLock();
                lock.lock();

                Optional<Actor> actorOptional = actorEditor.getById(socket.getId());
                if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                    Switchable switchable = (Switchable) actorOptional.get();
                    if(state == 1 && !socket.isInverse() || state == 0 && socket.isInverse()) {

                        if(switchable.getState() != AutomationElement.State.ON) {

                            switchable.setState(Switchable.State.ON);
                            switchable.setLastToggleTime(LocalDateTime.now());
                        }
                    } else {

                        if(switchable.getState() != AutomationElement.State.OFF) {

                            switchable.setState(Switchable.State.OFF);
                            switchable.setLastToggleTime(LocalDateTime.now());
                        }
                    }
                    LoggerUtil.getLogger(this.getClass()).finest("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " wurde erfolgreich aktualisiert");
                }
                success = true;
            } catch (IOException e) {

                exception = e;
            } finally {

                if(lock != null && lock.isHeldByCurrentThread()) {

                    lock.unlock();
                }
            }

            //bei misserfolg 2,5 Sekunden warten und erneut probieren
            if (!success) {

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {

                    return;
                }
            }
        }

        if(!success) {

            //Steckdose nicht erreichbar
            LoggerUtil.getLogger(this.getClass()).finer("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden", exception));
        }
    }
}
