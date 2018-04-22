package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.api.tplink.HS100;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

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
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(socket.getId());
            switchableOptional.ifPresent(switchable -> {

                switchable.setState(newSate);
                switchable.setLastToggleTime(LocalDateTime.now());
            });

            lock.unlock();
        } catch (IOException e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), "Schalten der TP-Link Steckdose nicht möglich", e);
        }
    }
}
