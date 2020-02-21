package net.kleditzsch.smarthome.controller;

import net.kleditzsch.smarthome.SmartHome;

/**
 * Speichert alle Anwendungsdaten
 */
public class DataDumpTask implements Runnable {

    @Override
    public void run() {

        SmartHome.getInstance().dump();
    }
}
