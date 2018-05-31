package net.kleditzsch.SmartHome.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.automation.AutomationAppliaction;
import net.kleditzsch.SmartHome.controller.global.CliConfigurator;
import net.kleditzsch.SmartHome.controller.global.DataDumpTask;
import net.kleditzsch.SmartHome.controller.global.webserver.JettyServerStarter;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.DatabaseManager;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.util.json.Serializer.*;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import net.kleditzsch.SmartHome.view.global.admin.*;
import net.kleditzsch.SmartHome.view.global.admin.backup.GlobalBackupServlet;
import net.kleditzsch.SmartHome.view.global.admin.info.GlobalServerInfoServlet;
import net.kleditzsch.SmartHome.view.global.admin.settings.GlobalSettingsServlet;
import net.kleditzsch.SmartHome.view.global.user.GlobalIndexServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kernklasse der Anwendung
 */
public class Application {

    /**
     * Server Version
     */
    public static final String VERSION = "1.0.0";

    /**
     * Singleton Instanz
     */
    private static Application instance;

    /**
     * Logger
     */
    private static Logger logger;

    /**
     * Liste der Kommandozeilenparameter
     */
    private static Set<String> arguments = new HashSet<>();

    /**
     * Datenbank Manager
     */
    private static DatabaseManager databaseManager;

    /**
     * Gson Builder
     */
    private GsonBuilder builder;

    /**
     * Einstellungs Verwaltung
     */
    private volatile SettingsEditor settings;

    /**
     * Datenverwaltung der Automatisierung
     */
    private volatile AutomationAppliaction automationAppliaction;

    /**
     * Scheduler
     */
    private volatile ScheduledExecutorService timerExecutor;

    /**
     * Webserver
     */
    private volatile JettyServerStarter server;

    /**
     * Einstieg in die Anwendung
     *
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {

        //Kommandozeilen Parameter in ein SET übernehmen
        Collections.addAll(arguments, args);

        //Version anzeigen
        if(arguments.contains("-v") || arguments.contains("--version")) {

            System.out.println("Version: " + VERSION);
            return;
        }

        //Logger Konfigurieren
        if(arguments.contains("-d") || arguments.contains("--debug")) {

            //Standard Log Level
            LoggerUtil.setLogLevel(Level.CONFIG);
            LoggerUtil.setLogFileLevel(Level.CONFIG);
        } else if(arguments.contains("-df") || arguments.contains("--debug-fine")) {

            //Alle Log Daten ausgeben
            LoggerUtil.setLogLevel(Level.FINEST);
            LoggerUtil.setLogFileLevel(Level.FINEST);
        } else {

            //Fehler in Log Datei Schreiben
            LoggerUtil.setLogLevel(Level.OFF);
            LoggerUtil.setLogFileLevel(Level.WARNING);
            LoggerUtil.setLogDir(Paths.get("log"));
        }
        logger = LoggerUtil.getLogger(Application.class);

        try {

            //prüfen ob die Datenbankkonfiguration vorhanden ist
            databaseManager = new DatabaseManager();
            if(arguments.contains("-db") || arguments.contains("--Database") || !Files.exists(databaseManager.getDbConfigFile())) {

                CliConfigurator.startDatabaseConfig();
                return;
            }

            //Anwendung initalisieren
            instance = new Application();
            instance.init();

            //Einstellungen
            if(arguments.contains("-c") || arguments.contains("--config")) {

                CliConfigurator.startApplicationConfig();
                return;
            }

            //Shutdown Funktion
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {

                    try {

                        Application.getInstance().stop();
                    } catch (Throwable throwable) {

                        LoggerUtil.serveException(logger, throwable);
                    }
                }
            });

            //Anwendung starten
            instance.start();

        } catch (Throwable throwable) {

            LoggerUtil.serveException(logger, "Allgemeiner Anwendungsfehler!", throwable);
        }
    }

    /**
     * initalisiert die Anwendung
     */
    public void init() {

        initGson();
        initDatabase();
        initData();

        //Automatisierung initalisieren
        automationAppliaction = new AutomationAppliaction();
        automationAppliaction.init();

        initWebserver();
    }

    /**
     * initialisiert die Google Gson API
     */
    private void initGson() {

        builder = new GsonBuilder();

        //Serialisierer und Deserialisierer anmelden
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer());
        builder.registerTypeAdapter(ID.class, new IdSerializer());
        builder.registerTypeAdapter(Room.class, new RoomSerializer());
    }

    /**
     * Datenbak initalisieren
     */
    private void initDatabase() {

        if(databaseManager == null) {

            databaseManager = new DatabaseManager();
        }

        try {

            databaseManager.readDatabaseConfigFile();
            databaseManager.connectDatabase();
        } catch (IllegalStateException se) {

            logger.severe("Die Datenbankkonfiguration konnte nicht gelesen werden");
            System.exit(1);
        } catch(JedisConnectionException e) {

            LoggerUtil.serveException(logger, "Verbindung zur Datenbank ist Fehlgeschlagen", e);
            System.exit(1);
        }
    }

    /**
     * Daten initalisieren
     */
    private void initData() {

        settings = new SettingsEditor();
        settings.load();
    }

    /**
     * Webserver initalisieren
     */
    private void initWebserver() {

        //Einstellungen laden
        SettingsEditor se = settings;
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        int securePort = se.getIntegerSetting(SettingsEditor.SERVER_SECURE_PORT).get().getValue();
        String keyStorePassword = se.getStringSetting(SettingsEditor.SERVER_KEY_STORE_PASSWORD).get().getValue();

        lock.unlock();

        try (JettyServerStarter serverStarter = new JettyServerStarter(securePort, Paths.get("KeyStore.jks").toAbsolutePath().toUri().toURL(), keyStorePassword)) {

            serverStarter.getRegisterFunctions().add(contextHandler -> {

                contextHandler.addServlet(GlobalIndexServlet.class, "/");
                contextHandler.addServlet(GlobalIndexServlet.class, "/index");
                contextHandler.addServlet(GlobalAdminIndexServlet.class, "/admin/index");
                contextHandler.addServlet(GlobalSettingsServlet.class, "/admin/settings");
                contextHandler.addServlet(GlobalServerInfoServlet.class, "/admin/info");
                contextHandler.addServlet(GlobalBackupServlet.class, "/admin/backup");
                contextHandler.addServlet(GlobalRebootServlet.class, "/admin/reboot");
                contextHandler.addServlet(GlobalShutdownServlet.class, "/admin/shutdown");

                getAutomation().initWebContext(contextHandler);
            });

            serverStarter.config();
            this.server = serverStarter;
        } catch (Exception e) {

            LoggerUtil.serveException(logger, e);
        }
    }

    /**
     * gibt die Datenbankverbindung zurück
     *
     * @return Datenbankverbindung
     */
    public Jedis getDatabaseConnection() {
        return databaseManager.getConnection();
    }

    /**
     * gibt die Datenbankverwaltung zurück
     *
     * @return Datenbankverwaltung
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * gibt den ThreadPool Scheduler zurück
     *
     * @return ThreadPool Scheduler
     */
    public ScheduledExecutorService getTimerExecutor() {
        return timerExecutor;
    }

    /**
     * gibt den Einstellungs-Editor zurück
     *
     * @return Einstellungs-Editor
     */
    public SettingsEditor getSettings() {
        return settings;
    }

    /**
     * erzeugt ein Gson Objekt aus dem Builder heraus
     *
     * @return Gson Objekt
     */
    public Gson getGson() {

        return builder.create();
    }

    /**
     * gibt die Automatisierungsverwaltung zurück
     *
     * @return Automatisierungsverwaltung
     */
    public AutomationAppliaction getAutomation() {

        return automationAppliaction;
    }

    /**
     * startet alle Dienste der Anwendung
     */
    public void start() {

        //Scheduler Threadpool
        timerExecutor = Executors.newScheduledThreadPool(5);

        //Speicherdienst aktivieren
        timerExecutor.scheduleAtFixedRate(new DataDumpTask(), 30, 30, TimeUnit.SECONDS);

        //Module starten
        getAutomation().start();

        //Webserver starteb
        try {

            server.start();
            System.out.println("Anwendung erfolgreich gestartet");
            server.join();

        } catch (Exception e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(getClass()), e);
        }
    }

    /**
     * speichert alle Daten der Anwendung
     */
    public void dump() {

        settings.dump();

        automationAppliaction.dump();
    }

    /**
     * beendet alle Dienste der Anwendung
     */
    public void stop() throws Throwable {

        //Editoren speichern
        automationAppliaction.dump();
        dump();

        //Anwendungen stoppen
        automationAppliaction.stop();

        //Scheduler stoppen
        timerExecutor.shutdown();

        //Datenbankverbindung aufheben
        if(databaseManager != null && databaseManager.isConnected()) {

            databaseManager.disconnectDatabase();
        }
    }

    /**
     * gibt die Instanz der Haupklasse zurück
     *
     * @return Application
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * gibt ein Set mit den übergebenen Kommandozeileparametern zurück
     *
     * @return Kommandozeilen Parameter
     */
    public static Set<String> getArguments() {
        return arguments;
    }
}
