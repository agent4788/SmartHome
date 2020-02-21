package net.kleditzsch.apps.automation.controller.tplinkservice;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.options.SwitchCommands;
import net.kleditzsch.apps.automation.controller.executorservice.ExecutorService;
import net.kleditzsch.apps.automation.controller.executorservice.command.SwitchCommand;
import net.kleditzsch.apps.automation.model.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.apps.automation.model.editor.ActorEditor;

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
