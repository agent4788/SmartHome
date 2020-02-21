package net.kleditzsch.applications.automation.controller.tplinkservice;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.options.SwitchCommands;
import net.kleditzsch.applications.automation.controller.executorservice.ExecutorService;
import net.kleditzsch.applications.automation.controller.executorservice.command.SwitchCommand;
import net.kleditzsch.applications.automation.model.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TpLinkUpdateService implements Runnable {

    @Override
    public void run() {

        //Status der TP-Link Steckdosen aktualisieren
        ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
        ReentrantReadWriteLock.ReadLock lock = actorEditor.readLock();
        lock.lock();

        ExecutorService executorService = SmartHome.getInstance().getAutomation().getExecutorService();
        actorEditor.getData().stream()
                .filter(e -> e instanceof TPlinkSocket && !e.isDisabled())
                .map(e -> (TPlinkSocket) actorEditor.copyOf(e))
                .forEach(e -> {

                    executorService.putCommand(new SwitchCommand(e, SwitchCommands.updateState));
                    if(e.getSocketType() == TPlinkSocket.SOCKET_TYPE.HS110) {

                        executorService.putCommand(new SwitchCommand(e, SwitchCommands.updateSensor));
                    }
                });

        lock.unlock();
    }
}
