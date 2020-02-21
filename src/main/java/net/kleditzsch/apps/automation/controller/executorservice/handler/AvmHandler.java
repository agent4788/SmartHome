package net.kleditzsch.apps.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.model.options.SwitchCommands;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;
import net.kleditzsch.apps.automation.api.avm.Device.Components.Switch;
import net.kleditzsch.apps.automation.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.apps.automation.api.avm.Exception.AuthException;
import net.kleditzsch.apps.automation.controller.avmservice.AvmEditor;
import net.kleditzsch.apps.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.apps.automation.model.device.actor.Interface.Switchable;
import net.kleditzsch.apps.automation.model.device.actor.switchable.AvmSocket;
import net.kleditzsch.apps.automation.model.editor.ActorEditor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AvmHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private AvmSocket socket;

    /**
     * Schaltbefehl
     */
    private SwitchCommands switchCommand;

    /**
     * @param socket schaltbares Element
     * @param switchCommand Schaltbefehl
     */
    public AvmHandler(AvmSocket socket, SwitchCommands switchCommand) {

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

        Switch.STATE newState;
        Optional<SmarthomeDevice> deviceOptional = Optional.empty();

        AvmEditor avmEditor = SmartHome.getInstance().getAutomation().getAvmEditor();
        if(!avmEditor.isActive()) {

            LoggerUtil.getLogger(this.getClass()).info("AVM Support nicht aktiviert");
            return;
        }

        ReentrantReadWriteLock.ReadLock lock = avmEditor.readLock();
        lock.lock();

        deviceOptional = avmEditor.getDeviceByIdentifier(socket.getIdentifier());

        lock.unlock();

        //Schaltbefehl ausführen
        ReentrantReadWriteLock.WriteLock lock1 = null;
        try {

            if(deviceOptional.isPresent() && deviceOptional.get().isSwitchSocket()) {

                SmarthomeDevice device = deviceOptional.get();
                if((switchCommand == SwitchCommands.on && !socket.isInverse())
                        || (switchCommand == SwitchCommands.off && socket.isInverse())) {

                    //Einschalten
                    try {

                        newState = device.getSwitch().get().switchOn();
                    } catch (AuthException e) {

                        //nochmal probieren
                        newState = device.getSwitch().get().switchOn();
                    }
                } else if((switchCommand == SwitchCommands.off && !socket.isInverse())
                        || (switchCommand == SwitchCommands.on && socket.isInverse())) {

                    //Uasschalten
                    try {

                        newState = device.getSwitch().get().switchOff();
                    } catch (AuthException e) {

                        //nochmal probieren
                        newState = device.getSwitch().get().switchOff();
                    }
                } else {

                    //umschalten
                    try {

                        newState = device.getSwitch().get().switchToggle();
                    } catch (AuthException e) {

                        //nochmal probieren
                        newState = device.getSwitch().get().switchToggle();
                    }
                }

                //Status speichern
                final Switch.STATE finalNewState = newState;
                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
                lock1 = actorEditor.writeLock();
                lock1.lock();

                Optional<Actor> actorOptional = actorEditor.getById(socket.getId());
                if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                    Switchable switchable = (Switchable) actorOptional.get();
                    switchable.setState(finalNewState == Switch.STATE.ON ? Switchable.State.ON : Switchable.State.OFF);
                    switchable.setLastToggleTime(LocalDateTime.now());
                }
            } else {

                LoggerUtil.getLogger(this.getClass()).warning("Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht gefunden werden");
                MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden"));
            }
        } catch (IOException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden", e));
        } finally {

            if(lock1 != null && lock1.isHeldByCurrentThread()) {

                lock1.unlock();
            }
        }
    }
}
