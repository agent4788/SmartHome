package net.kleditzsch.SmartHome.controller.automation.tplinkservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TpLinkUpdateService implements Runnable {

    @Override
    public void run() {

        //Status der TP-Link Steckdosen aktualisieren
        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
        ReentrantReadWriteLock.ReadLock lock = actorEditor.readLock();
        lock.lock();

        ExecutorService executorService = Application.getInstance().getAutomation().getExecutorService();
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
