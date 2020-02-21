package net.kleditzsch.smarthome.utility.logger;



import net.kleditzsch.smarthome.utility.logger.Handler.ConsoleHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilfsklasse zum Konfigurieren der Logger
 *
 * @author Oliver Kleditzsch
 */
public abstract class LoggerUtil {

    /**
     * Log Level
     */
    private static Level logLevel = Level.INFO;

    /**
     * Log Datei
     */
    private static Path logDir = null;
    private static Level logFileLevel = Level.SEVERE;
    private static FileHandler logFileHandler = null;

    /**
     * Konsolen Log Handler
     */
    private static ConsoleHandler consoleHandler = new ConsoleHandler();

    /**
     * erzeugt einen Logger zu einem Klassenobjekt
     *
     * @param type Klasse
     * @return Logger
     */
    public static Logger getLogger(Class type) {

        return config(Logger.getLogger(type.getName()));
    }

    /**
     * erzeugt einen Logger zu einem Objekt
     *
     * @param type Objekt
     * @return Logger
     */
    public static Logger getLogger(Object type) {

        return config(Logger.getLogger(type.getClass().getName()));
    }

    /**
     * erzeugt einen Logger zu einem Objekt
     *
     * @return Logger
     */
    public static Logger getRootLogger() {

        return config(Logger.getLogger("root"));
    }

    /**
     * gibt das Log Level zurück
     *
     * @return Log Level
     */
    public static Level getLogLevel() {
        return logLevel;
    }

    /**
     * setzt das Log Level
     *
     * @param logLevel Log Level
     */
    public static void setLogLevel(Level logLevel) {
        LoggerUtil.logLevel = logLevel;
    }

    /**
     * gibt den Pfad zu den Log Dateien zurück (null wenn keine Datei erstellt werden soll)
     *
     * @return Pfad zur Log Datei
     */
    public static Path getLogDir() {
        return logDir;
    }

    /**
     * setzt den Pfad zu den Log Dateien
     *
     * @param logDir Pfad zur Log Datei
     */
    public static void setLogDir(Path logDir) {
        LoggerUtil.logDir = logDir;
    }

    /**
     * gibt das Log Level für die Log Datei zurück
     *
     * @return Log Level
     */
    public static Level getLogFileLevel() {
        return logFileLevel;
    }

    /**
     * setzt das Log Level für die Log Datei
     *
     * @param logFileLevel Log Level
     */
    public static void setLogFileLevel(Level logFileLevel) {
        LoggerUtil.logFileLevel = logFileLevel;
    }

    /**
     * konfiguriert einen Logger
     *
     * @param logger Logger
     * @return Logger
     */
    private static Logger config(Logger logger) {

        //Datei Handler
        if(logDir != null && logFileHandler == null) {

            try {

                if(!Files.exists(logDir)) {

                    Files.createDirectories(logDir);
                }
                logFileHandler = new FileHandler(logDir.resolve("logdata_%g").toString(), 1000, 10, true);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        if(logFileHandler != null) {

            logFileHandler.setLevel(logFileLevel);
            logger.addHandler(logFileHandler);
        }

        //Konsolen Handler
        if(logLevel != Level.OFF) {

            consoleHandler.setLevel(logLevel);
            logger.addHandler(consoleHandler);
        }

        //Allgemeine Konfiguration
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);
        return logger;
    }

    /**
     * gibt eine Exception über den Logger mit dem SERVE Level aus
     *
     * @param logger Logger
     * @param exception Exception
     */
    public static void serveException(Logger logger, Throwable exception) {

        logger.log(Level.SEVERE, exception.getLocalizedMessage(), exception);
    }

    /**
     * gibt eine Exception über den Logger mit dem SERVE Level aus
     *
     * @param logger Logger
     * @param message Meldung
     * @param exception Exception
     */
    public static void serveException(Logger logger, String message, Throwable exception) {

        logger.log(Level.SEVERE, message, exception);
    }
}
