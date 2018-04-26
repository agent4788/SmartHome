package net.kleditzsch.SmartHome.controller.automation.tplinkservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TpLinkUpdateService implements Runnable {

    @Override
    public void run() {

        //Status der TP-Link Steckdosen aktualisieren
        SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
        ReentrantReadWriteLock.ReadLock lock = switchableEditor.readLock();
        lock.lock();

        ExecutorService executorService = Application.getInstance().getAutomation().getExecutorService();
        switchableEditor.getData().stream()
                .filter(e -> e instanceof TPlinkSocket && !e.isDisabled())
                .map(e -> {

                    return (TPlinkSocket) switchableEditor.copyOf(e);
                })
                .forEach(e -> {

                    executorService.putCommand(new SwitchCommand(e, SwitchCommands.updateState));
                    if(e.getSocketType() == TPlinkSocket.SOCKET_TYPE.HS110) {

                        executorService.putCommand(new SwitchCommand(e, SwitchCommands.updateSensor));
                    }
                });

        lock.unlock();
    }
}
