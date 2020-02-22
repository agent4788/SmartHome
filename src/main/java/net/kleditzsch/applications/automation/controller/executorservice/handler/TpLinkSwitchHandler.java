package net.kleditzsch.applications.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.applications.automation.model.options.SwitchCommands;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;
import net.kleditzsch.applications.automation.api.tplink.HS100;
import net.kleditzsch.applications.automation.model.device.AutomationElement;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Switchable;
import net.kleditzsch.applications.automation.model.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * führt die Schaltbefehle für TP-Link Steckdosen aus
 */
public class TpLinkSwitchHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private TPlinkSocket socket;

    /**
     * Schaltbefehl
     */
    private SwitchCommands switchCommand;

    /**
     * @param socket schaltbares Element
     * @param switchCommand Schaltbefehl
     */
    public TpLinkSwitchHandler(TPlinkSocket socket, SwitchCommands switchCommand) {

        Preconditions.checkNotNull(socket);
        Preconditions.checkNotNull(switchCommand);
        this.socket = socket;
        this.switchCommand = switchCommand;
    }

    /**
     * führt den Befehl aus (Nebenläufig)
     */
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

                //Steckdosenobjekt erzeugen
                HS100 hs100 = new HS100(socket.getIpAddress(), socket.getPort());

                final Switchable.State newSate;
                if ((switchCommand == SwitchCommands.on && !socket.isInverse())
                        || (switchCommand == SwitchCommands.off && socket.isInverse())) {

                    //Steckdose einschalten
                    hs100.switchOn();
                    newSate = AutomationElement.State.ON;

                } else if (switchCommand == SwitchCommands.off && !socket.isInverse()
                        || (switchCommand == SwitchCommands.on && socket.isInverse())) {

                    //Steckdose ausschalten
                    hs100.switchOff();
                    newSate = AutomationElement.State.OFF;

                } else {

                    //Steckdose Umschalten
                    int state = hs100.readState();
                    if(state == 1) {

                        hs100.switchOff();
                        newSate = AutomationElement.State.OFF;
                    } else {

                        hs100.switchOn();
                        newSate = AutomationElement.State.ON;
                    }
                }

                //Status speichern
                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
                lock = actorEditor.writeLock();
                lock.lock();

                Optional<Actor> actorOptional = actorEditor.getById(socket.getId());
                if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                    Switchable switchable = (Switchable) actorOptional.get();
                    switchable.setState(newSate);
                    switchable.setLastToggleTime(LocalDateTime.now());
                }

                success = true;
            } catch (IOException e) {

                exception = e;
            } finally {

                if(lock != null && lock.isHeldByCurrentThread()) {

                    lock.unlock();
                }
            }
        }

        if(!success) {

            //Steckdose nicht erreichbar
            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), "Schalten der TP-Link Steckdose \"" + socket.getIpAddress() + "\" nicht möglich", exception);
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Schalten der TP-Link Steckdose \"" + socket.getIpAddress() + "\" nicht möglich", exception));
        }
    }
}
