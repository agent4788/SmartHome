package net.kleditzsch.SmartHome.controller.automation.avmservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.device.switchable.AvmSocket;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.Switch;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AvmDataUpdateService implements Runnable {

    @Override
    public void run() {

        try {

            AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();

            //aktuelle Daten von der FritzBox holen
            avmEditor.load();
            LoggerUtil.getLogger(this.getClass()).finest("Die AVM Daten wurden erfolgreich aktualisiert");

            //Status der AVM Steckdosen aktualisieren
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            List<AvmSocket> avmSockets = switchableEditor.getData().stream()
                    .filter(e -> e instanceof AvmSocket && !e.isDisabled())
                    .map(e -> ((AvmSocket) e))
                    .collect(Collectors.toList());

            ReentrantReadWriteLock.ReadLock avmLock = avmEditor.readLock();
            avmLock.lock();

            //Schaltstatus aktualisieren
            avmSockets.forEach(e -> {

                Optional<SmarthomeDevice> deviceOptional = avmEditor.getDeviceByIdentifier(e.getIdentifier());
                if(deviceOptional.isPresent() && deviceOptional.get().getSwitch().isPresent()) {

                    Switch.STATE state = deviceOptional.get().getSwitch().get().getState();
                    if((state == Switch.STATE.ON && !e.isInverse()) || (state == Switch.STATE.OFF && e.isInverse())) {

                        e.setState(Switchable.State.ON);
                    } else {

                        e.setState(Switchable.State.OFF);
                    }
                }
            });

            //Sensordaten aktualisieren
            //TODO Sensordaten aktualisierung implementieren

            avmLock.unlock();
            lock.unlock();

        } catch (AuthException e) {

            LoggerUtil.getLogger(this.getClass()).finer("Die AVM Daten konnten nicht aktualisiert werden");
        }
    }
}
