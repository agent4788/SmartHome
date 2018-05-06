package net.kleditzsch.SmartHome.controller.global;

import net.kleditzsch.SmartHome.app.Application;

/**
 * Speichert alle Anwendungsdaten
 */
public class DataDumpTask implements Runnable {

    @Override
    public void run() {

        Application.getInstance().dump();
    }
}
