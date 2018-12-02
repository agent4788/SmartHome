package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.util.api.tplink.HS100;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.io.IOException;
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

        try {

            HS100 hs100 = new HS100(socket.getIpAddress(), socket.getPort());
            int state = hs100.readState();

            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(socket.getId());
            if(switchableOptional.isPresent()) {

                if(state == 1 && !socket.isInverse() || state == 0 && socket.isInverse()) {

                    switchableOptional.get().setState(Switchable.State.ON);
                } else {

                    switchableOptional.get().setState(Switchable.State.OFF);
                }
                LoggerUtil.getLogger(this.getClass()).finest("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " wurde erfolgreich aktualisiert");
            }

            lock.unlock();

        } catch (IOException e) {

            //Steckdose nicht erreichbar
            LoggerUtil.getLogger(this.getClass()).finer("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden", e));
        }
    }
}
