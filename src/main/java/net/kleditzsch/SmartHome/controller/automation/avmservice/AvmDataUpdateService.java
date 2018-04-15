package net.kleditzsch.SmartHome.controller.automation.avmservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.util.TimerTask;

public class AvmDataUpdateService extends TimerTask {

    @Override
    public void run() {

        try {

            AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();
            avmEditor.load();
        } catch (AuthException e) {

            LoggerUtil.getLogger(this.getClass()).warning("Die AVM Daten konnten nicht aktualisiert werden");
        }
    }
}
