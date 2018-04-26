package net.kleditzsch.SmartHome.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.automation.AutomationAppliaction;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.DatabaseManager;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.json.Serializer.*;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Kernklasse der Anwendung
 */
public class Application {

    /**
     * Server Version
     */
    public static final String VERSION = "0.1.0";
    /**
     * Server API LEVEL
     */
    public static final int API_LEVEL = 1;

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

        //Logger initaliiseren
        LoggerUtil.setLogLevel(Level.INFO);
        LoggerUtil.setLogFileLevel(Level.OFF);
        logger = LoggerUtil.getLogger(Application.class);

        try {

            //prüfen ob die Datenbankkonfiguration vorhanden ist
            databaseManager = new DatabaseManager();
            if (!Files.exists(databaseManager.getDbConfigFile())) {

                logger.severe("Keine Datenbankkonfiguration vorhanden");
                System.exit(1);
            }

            //Anwendung initalisieren
            instance = new Application();
            instance.init();

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
     * gibt die Datenbankverbindung zurück
     *
     * @return Datenbankverbindung
     */
    public Jedis getDatabaseConnection() {
        return databaseManager.getConnection();
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

        getAutomation().start();
        System.out.println("Anwendung erfolgreich gestartet");

        SwitchTimer switchTimer = new SwitchTimer();
        switchTimer.setId(ID.create());
        switchTimer.setName("Test ein");
        switchTimer.getMinute().addAll(IntStream.iterate(0, i -> i + 2).limit(30).collect(TreeSet::new, TreeSet::add, TreeSet::addAll));
        switchTimer.getCommands().add(new SwitchCommand(ID.of("07fd8826-ffdb-4601-917f-cfcdf8751085"), SwitchCommands.on));

        SwitchTimer switchTimer1 = new SwitchTimer();
        switchTimer1.setId(ID.create());
        switchTimer1.setName("Test aus");
        switchTimer1.getMinute().addAll(IntStream.iterate(1, i -> i + 2).limit(30).collect(TreeSet::new, TreeSet::add, TreeSet::addAll));
        switchTimer1.getCommands().add(new SwitchCommand(ID.of("07fd8826-ffdb-4601-917f-cfcdf8751085"), SwitchCommands.off));

        SwitchTimerEditor ste = getAutomation().getSwitchTimerEditor();
        ReentrantReadWriteLock.WriteLock lock = ste.writeLock();
        lock.lock();

        ste.getData().add(switchTimer);
        ste.getData().add(switchTimer1);

        lock.unlock();

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
