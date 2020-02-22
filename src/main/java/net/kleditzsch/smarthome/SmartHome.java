package net.kleditzsch.smarthome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import net.kleditzsch.smarthome.application.Application;
import net.kleditzsch.smarthome.application.ApplicationRegistry;
import net.kleditzsch.smarthome.controller.CliBackupRestore;
import net.kleditzsch.smarthome.controller.CliConfiguration;
import net.kleditzsch.smarthome.controller.DataDumpTask;
import net.kleditzsch.smarthome.controller.backup.BackupCleanupTask;
import net.kleditzsch.smarthome.controller.backup.BackupTask;
import net.kleditzsch.smarthome.controller.webserver.JettyServerStarter;
import net.kleditzsch.smarthome.database.DatabaseManager;
import net.kleditzsch.smarthome.database.exception.DatabaseException;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.json.Serializer.*;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;
import net.kleditzsch.smarthome.view.admin.GlobalAdminIndexServlet;
import net.kleditzsch.smarthome.view.admin.GlobalRebootServlet;
import net.kleditzsch.smarthome.view.admin.GlobalShutdownServlet;
import net.kleditzsch.smarthome.view.admin.backup.GlobalBackupServlet;
import net.kleditzsch.smarthome.view.admin.backup.GlobalDeleteBackupServlet;
import net.kleditzsch.smarthome.view.admin.backup.GlobalDownloadBackupServlet;
import net.kleditzsch.smarthome.view.admin.backup.GlobalRunBackupServlet;
import net.kleditzsch.smarthome.view.admin.info.GlobalServerInfoServlet;
import net.kleditzsch.smarthome.view.admin.message.GlobalMessageServlet;
import net.kleditzsch.smarthome.view.admin.settings.GlobalSettingsServlet;
import net.kleditzsch.smarthome.view.user.GlobalIndexServlet;
import net.kleditzsch.smarthome.view.user.GlobalMobileViewServlet;
import net.kleditzsch.applications.automation.AutomationAppliaction;
import net.kleditzsch.applications.automation.model.room.Room;
import net.kleditzsch.applications.contact.ContactApplication;
import net.kleditzsch.applications.contract.ContractApplication;
import net.kleditzsch.applications.knowledge.KnowledgeApplication;
import net.kleditzsch.applications.movie.MovieApplication;
import net.kleditzsch.applications.network.NetworkApplication;
import net.kleditzsch.applications.recipe.RecipeApplication;
import net.kleditzsch.applications.shoppinglist.ShoppingListApplication;
import org.bson.Document;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
public class SmartHome {

    /**
     *
     * Server Version
     */
    public static final String VERSION = "1.2.0";

    /**
     * Singleton Instanz
     */
    private static SmartHome instance;

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
     * Anwendungsregistry
     */
    private volatile ApplicationRegistry applicationRegistry = new ApplicationRegistry();

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

        //Backup wiederherstellen
        if(arguments.contains("-r") || arguments.contains("--restore")) {

            CliBackupRestore.restoreBackup();
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
        logger = LoggerUtil.getLogger(SmartHome.class);

        try {

            //prüfen ob die Datenbankkonfiguration vorhanden ist
            databaseManager = new DatabaseManager();
            if(arguments.contains("-db") || arguments.contains("--Database") || !Files.exists(databaseManager.getDbConfigFile())) {

                CliConfiguration.startDatabaseConfig();
                return;
            }

            //Anwendung initalisieren
            instance = new SmartHome();
            instance.init();

            //Einstellungen
            if(arguments.contains("-c") || arguments.contains("--config")) {

                CliConfiguration.startApplicationConfig();
                return;
            }

            //Shutdown Funktion
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {

                    try {

                        SmartHome.getInstance().stop();
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

        applicationRegistry.registerApplication(new AutomationAppliaction());
        applicationRegistry.registerApplication(new ContractApplication());
        applicationRegistry.registerApplication(new ContactApplication());
        applicationRegistry.registerApplication(new MovieApplication());
        applicationRegistry.registerApplication(new NetworkApplication());
        applicationRegistry.registerApplication(new RecipeApplication());
        applicationRegistry.registerApplication(new ShoppingListApplication());
        applicationRegistry.registerApplication(new KnowledgeApplication());

        applicationRegistry.init();

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

        //Automatisierung
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
        } catch(DatabaseException e) {

            LoggerUtil.serveException(logger, "Verbindung zur Datenbank ist Fehlgeschlagen", e);
            System.exit(1);
        }
    }

    /**
     * Daten initalisieren
     */
    private void initData() {

        //Meldungs Collection als Chapped Collection anlegen
        if(!databaseManager.getCollectionNames().contains(MessageEditor.COLLECTION)) {

            databaseManager.getDatabase().createCollection(MessageEditor.COLLECTION, new CreateCollectionOptions().capped(true).maxDocuments(1000).sizeInBytes(5 * 1024 * 1024));
        }

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

        int port = se.getIntegerSetting(Settings.SERVER_PORT).getValue();
        int securePort = se.getIntegerSetting(Settings.SERVER_SECURE_PORT).getValue();
        String keyStorePassword = se.getStringSetting(Settings.SERVER_KEY_STORE_PASSWORD).getValue();

        lock.unlock();

        try (JettyServerStarter serverStarter = new JettyServerStarter(port, securePort, Paths.get("KeyStore.jks").toAbsolutePath().toUri().toURL(), keyStorePassword)) {

            serverStarter.getRegisterFunctions().add(contextHandler -> {

                contextHandler.addServlet(GlobalIndexServlet.class, "/index");
                contextHandler.addServlet(GlobalMobileViewServlet.class, "/mobileview");
                contextHandler.addServlet(GlobalAdminIndexServlet.class, "/admin/index");
                contextHandler.addServlet(GlobalSettingsServlet.class, "/admin/settings");
                contextHandler.addServlet(GlobalServerInfoServlet.class, "/admin/info");
                contextHandler.addServlet(GlobalMessageServlet.class, "/admin/message");
                contextHandler.addServlet(GlobalBackupServlet.class, "/admin/backup");
                contextHandler.addServlet(GlobalDownloadBackupServlet.class, "/admin/downloadbackup");
                contextHandler.addServlet(GlobalDeleteBackupServlet.class, "/admin/deletebackup");
                contextHandler.addServlet(GlobalRunBackupServlet.class, "/admin/runbackup");
                contextHandler.addServlet(GlobalRebootServlet.class, "/admin/reboot");
                contextHandler.addServlet(GlobalShutdownServlet.class, "/admin/shutdown");

                //Webseiten der Anwendungen registrieren
                applicationRegistry.initWebContext(contextHandler);
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
    public MongoDatabase getDatabaseConnection() {
        return databaseManager.getDatabase();
    }

    /**
     * gibt das Objekt der angeforderten Collection zurück
     *
     * @param collectionName Name der angeforderten Collection
     * @return Objekt der angeforderten Collection
     */
    public MongoCollection<Document> getDatabaseCollection(String collectionName) {
        return databaseManager.getCollection(collectionName);
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
        return applicationRegistry.getApplication(AutomationAppliaction.class);
    }

    /**
     * gibt die Kontakteverwaltung zurück
     *
     * @return Kontakteverwaltung
     */
    public ContactApplication getContactApplication() {
        return applicationRegistry.getApplication(ContactApplication.class);
    }

    /**
     * gibt die Verträgeverwaltung zurück
     *
     * @return Verträgeverwaltung
     */
    public ContractApplication getContractApplication() {
        return applicationRegistry.getApplication(ContractApplication.class);
    }

    /**
     * gibt die Filmdatenbank zurück
     *
     * @return Filmdatenbank
     */
    public MovieApplication getMovieApplication() {
        return applicationRegistry.getApplication(MovieApplication.class);
    }

    /**
     * gibt die Musikdatenbank zurück
     *
     * @return Musikdatenbank
     */
    public NetworkApplication getNetworkApplication() {
        return applicationRegistry.getApplication(NetworkApplication.class);
    }

    /**
     * gibt die Rezepteverwaltung zurück
     *
     * @return Rezepteverwaltung
     */
    public RecipeApplication getRecipeApplication() {
        return applicationRegistry.getApplication(RecipeApplication.class);
    }

    /**
     * gibt die Einkaufsliste zurück
     *
     * @return Einkaufsliste
     */
    public ShoppingListApplication getShoppingListApplication() {
        return applicationRegistry.getApplication(ShoppingListApplication.class);
    }

    /**
     * gibt die Wissensdatenbank zurück
     *
     * @return Wissensdatenbank
     */
    public KnowledgeApplication getKnowledgeApplication() {
        return applicationRegistry.getApplication(KnowledgeApplication.class);
    }

    /**
     * gibt eine Liste mit allen bekannten Anwendungen zurück
     *
     * @return Liste mit allen bekannten Anwendungen
     */
    public List<Application> listApplications() {

        return applicationRegistry.listApplications();
    }

    /**
     * startet alle Dienste der Anwendung
     */
    public void start() {

        //Einstellungen laden
        boolean enableAutoBackup = true;
        int autoCleanupDays = 10;
        ReentrantReadWriteLock.ReadLock lock = settings.readLock();
        lock.lock();

        enableAutoBackup = settings.getBooleanSetting(Settings.BACKUP_ENABLE_AUTO_BACKUP).getValue();
        autoCleanupDays = settings.getIntegerSetting(Settings.BACKUP_AUTO_CLEANUP_DAYS).getValue();

        lock.unlock();

        //Scheduler Threadpool
        timerExecutor = Executors.newScheduledThreadPool(5);

        //Speicherdienst aktivieren
        timerExecutor.scheduleAtFixedRate(new DataDumpTask(), 30, 30, TimeUnit.SECONDS);

        //Backupdienst aktivieren
        if (enableAutoBackup) {

            long firstRunDelayBackup = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay().plusHours(1), ChronoUnit.MINUTES);
            long firstRunDelayCleanup = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay().plusHours(2), ChronoUnit.MINUTES);
            timerExecutor.scheduleAtFixedRate(new BackupTask(), firstRunDelayBackup, 1440, TimeUnit.MINUTES);
            timerExecutor.scheduleAtFixedRate(new BackupCleanupTask(autoCleanupDays), firstRunDelayCleanup, 1440, TimeUnit.MINUTES);
        }

        //Anwendungen starten
        applicationRegistry.start();

        //Webserver starten
        try {

            server.start();

            //Meldung
            System.out.println("Anwendung erfolgreich gestartet");
            MessageEditor.addMessage(new Message("global", Message.Type.info, "Anwendung erfolgreich gestartet"));

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

        applicationRegistry.dump();
    }

    /**
     * beendet alle Dienste der Anwendung
     */
    public void stop() throws Throwable {

        //Daten speichern
        dump();

        //Anwendungen stoppen
        applicationRegistry.stop();

        //Scheduler stoppen
        timerExecutor.shutdown();

        //Meldung
        MessageEditor.addMessage(new Message("global", Message.Type.info, "Anwendung erfolgreich beendet"));

        //Datenbankverbindung aufheben
        if(databaseManager != null) {

            databaseManager.disconnectDatabase();
        }
    }

    /**
     * gibt an ob die Anwendung aus einer JAR Datei heraus gestartet wurde
     *
     * @return true wenn JAR
     */
    public static boolean runFromJar() {

        String protocol = SmartHome.class.getResource("").getProtocol();
        if(Objects.equals(protocol, "jar")) {

            return true;
        }
        return false;
    }

    /**
     * gibt die Instanz der Haupklasse zurück
     *
     * @return Application
     */
    public static SmartHome getInstance() {
        return instance;
    }

    /**
     * gibt die Instanz der Haupklasse zurück
     *
     * @return Application
     */
    public static SmartHome i() {
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
