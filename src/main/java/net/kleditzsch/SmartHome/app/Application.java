package net.kleditzsch.SmartHome.app;

import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.util.logging.Level;

/**
 * Startpunkt der Anwendung
 *
 * @author Oliver Kleditzsch
 */
public class Application {

    public static void main(String[] args) {

        //Logger Konfigurieren
        LoggerUtil.setLogLevel(Level.FINEST);
        LoggerUtil.setLogFileLevel(Level.OFF);


    }
}
