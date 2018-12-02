package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmEditor;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.switchable.AvmSocket;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.Switch;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

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

        AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();
        if(!avmEditor.isActive()) {

            LoggerUtil.getLogger(this.getClass()).info("AVM Support nicht aktiviert");
            return;
        }

        ReentrantReadWriteLock.ReadLock lock = avmEditor.readLock();
        lock.lock();

        deviceOptional = avmEditor.getDeviceByIdentifier(socket.getIdentifier());

        lock.unlock();

        //Schaltbefehl ausführen
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
                SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
                ReentrantReadWriteLock.WriteLock lock1 = switchableEditor.writeLock();
                lock1.lock();

                Optional<Switchable> switchableOptional = switchableEditor.getById(socket.getId());
                switchableOptional.ifPresent(switchable -> {

                    switchable.setState(finalNewState == Switch.STATE.ON ? Switchable.State.ON : Switchable.State.OFF);
                    switchable.setLastToggleTime(LocalDateTime.now());
                });

                lock1.unlock();
            } else {

                LoggerUtil.getLogger(this.getClass()).warning("Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht gefunden werden");
                MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden"));
            }
        } catch (IOException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Das Avm Gerät \"" + socket.getIdentifier() + "\" konnte nicht geschalten werden", e));
        }
    }
}
