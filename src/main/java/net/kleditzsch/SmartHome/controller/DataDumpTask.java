package net.kleditzsch.SmartHome.controller;

import net.kleditzsch.SmartHome.SmartHome;

/**
 * Speichert alle Anwendungsdaten
 */
public class DataDumpTask implements Runnable {

    @Override
    public void run() {

        SmartHome.getInstance().dump();
    }
}
