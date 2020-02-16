package net.kleditzsch.SmartHome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import net.kleditzsch.SmartHome.view.admin.GlobalAdminIndexServlet;
import net.kleditzsch.SmartHome.view.admin.GlobalRebootServlet;
import net.kleditzsch.SmartHome.view.admin.GlobalShutdownServlet;
import net.kleditzsch.apps.automation.AutomationAppliaction;
import net.kleditzsch.apps.contract.ContractApplication;
import net.kleditzsch.apps.contact.ContactApplication;
import net.kleditzsch.apps.knowledge.KnowledgeApplication;
import net.kleditzsch.apps.movie.MovieApplication;
import net.kleditzsch.apps.network.NetworkApplication;
import net.kleditzsch.apps.recipe.RecipeApplication;
import net.kleditzsch.apps.shoppinglist.ShoppingListApplication;
import net.kleditzsch.SmartHome.controller.CliConfiguration;
import net.kleditzsch.SmartHome.controller.DataDumpTask;
import net.kleditzsch.SmartHome.controller.backup.BackupCleanupTask;
import net.kleditzsch.SmartHome.controller.CliBackupRestore;
import net.kleditzsch.SmartHome.controller.backup.BackupTask;
import net.kleditzsch.SmartHome.controller.webserver.JettyServerStarter;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.database.DatabaseManager;
import net.kleditzsch.SmartHome.database.exception.DatabaseException;
import net.kleditzsch.apps.automation.model.room.Room;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.model.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.SmartHome.utility.json.Serializer.*;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;
import net.kleditzsch.SmartHome.view.admin.backup.GlobalBackupServlet;
import net.kleditzsch.SmartHome.view.admin.backup.GlobalDeleteBackupServlet;
import net.kleditzsch.SmartHome.view.admin.backup.GlobalDownloadBackupServlet;
import net.kleditzsch.SmartHome.view.admin.backup.GlobalRunBackupServlet;
import net.kleditzsch.SmartHome.view.admin.info.GlobalServerInfoServlet;
import net.kleditzsch.SmartHome.view.admin.message.GlobalMessageServlet;
import net.kleditzsch.SmartHome.view.admin.settings.GlobalSettingsServlet;
import net.kleditzsch.SmartHome.view.user.GlobalIndexServlet;
import net.kleditzsch.SmartHome.view.user.GlobalMobileViewServlet;
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
     * Hauptklasse der Automatisierung
     */
    private volatile AutomationAppliaction automationAppliaction;

    /**
     * Hauptklasse des Kalenders
     */
    private volatile ContractApplication contractApplication;

    /**
     * Hauptklasse der Verträgeverwaltung
     */
    private volatile ContactApplication contactApplication;

    /**
     * Hauptklasse der Filmdatenbank
     */
    private volatile MovieApplication movieApplication;

    /**
     * Hauptklasse der Musikverwaltung
     */
    private volatile NetworkApplication networkApplication;

    /**
     * Hauptklasse der Rezepteverwaltung
     */
    private volatile RecipeApplication recipeApplication;

    /**
     * Hauptklasse der Einkaufsliste
     */
    private volatile ShoppingListApplication shoppingListApplication;

    /**
     * Hauptklasse der Wissensdatenbank
     */
    private volatile KnowledgeApplication knowledgeApplication;

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

        //Automatisierung initalisieren
        automationAppliaction = new AutomationAppliaction();
        automationAppliaction.init();

        //Verträge initalisieren
        contractApplication = new ContractApplication();
        contractApplication.init();

        //Kontakte initalisieren
        contactApplication = new ContactApplication();
        contactApplication.init();

        //Filmdatenbank initalisieren
        movieApplication = new MovieApplication();
        movieApplication.init();

        //Netzwerk initalisieren
        networkApplication = new NetworkApplication();
        networkApplication.init();

        //Rezeptedatenbank initalisieren
        recipeApplication = new RecipeApplication();
        recipeApplication.init();

        //Einkaufsliste initalisieren
        shoppingListApplication = new ShoppingListApplication();
        shoppingListApplication.init();

        //Wissensdatenbank initalisieren
        knowledgeApplication = new KnowledgeApplication();
        knowledgeApplication.init();

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

        int port = se.getIntegerSetting(SettingsEditor.SERVER_PORT).get().getValue();
        int securePort = se.getIntegerSetting(SettingsEditor.SERVER_SECURE_PORT).get().getValue();
        String keyStorePassword = se.getStringSetting(SettingsEditor.SERVER_KEY_STORE_PASSWORD).get().getValue();

        lock.unlock();

        try (JettyServerStarter serverStarter = new JettyServerStarter(port, securePort, Paths.get("KeyStore.jks").toAbsolutePath().toUri().toURL(), keyStorePassword)) {

            serverStarter.getRegisterFunctions().add(contextHandler -> {

                contextHandler.addServlet(GlobalIndexServlet.class, "/");
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
                automationAppliaction.initWebContext(contextHandler);
                contactApplication.initWebContext(contextHandler);
                contractApplication.initWebContext(contextHandler);
                movieApplication.initWebContext(contextHandler);
                networkApplication.initWebContext(contextHandler);
                recipeApplication.initWebContext(contextHandler);
                shoppingListApplication.initWebContext(contextHandler);
                knowledgeApplication.initWebContext(contextHandler);
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
        return automationAppliaction;
    }

    /**
     * gibt die Kontakteverwaltung zurück
     *
     * @return Kontakteverwaltung
     */
    public ContactApplication getContactApplication() {
        return contactApplication;
    }

    /**
     * gibt die Verträgeverwaltung zurück
     *
     * @return Verträgeverwaltung
     */
    public ContractApplication getContractApplication() {
        return contractApplication;
    }

    /**
     * gibt die Filmdatenbank zurück
     *
     * @return Filmdatenbank
     */
    public MovieApplication getMovieApplication() {
        return movieApplication;
    }

    /**
     * gibt die Musikdatenbank zurück
     *
     * @return Musikdatenbank
     */
    public NetworkApplication getNetworkApplication() {
        return networkApplication;
    }

    /**
     * gibt die Rezepteverwaltung zurück
     *
     * @return Rezepteverwaltung
     */
    public RecipeApplication getRecipeApplication() {
        return recipeApplication;
    }

    /**
     * gibt die Einkaufsliste zurück
     *
     * @return Einkaufsliste
     */
    public ShoppingListApplication getShoppingListApplication() {
        return shoppingListApplication;
    }

    /**
     * gibt die Wissensdatenbank zurück
     *
     * @return Wissensdatenbank
     */
    public KnowledgeApplication getKnowledgeApplication() {
        return knowledgeApplication;
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

        Optional<BooleanSetting> enableAutoBackupOptional = settings.getBooleanSetting(SettingsEditor.BACKUP_ENABLE_AUTO_BACKUP);
        if(enableAutoBackupOptional.isPresent()) {

            enableAutoBackup = enableAutoBackupOptional.get().getValue();
        }
        Optional<IntegerSetting> autoCleanupDaysOptional = settings.getIntegerSetting(SettingsEditor.BACKUP_AUTO_CLEANUP_DAYS);
        if(autoCleanupDaysOptional.isPresent()) {

            autoCleanupDays = autoCleanupDaysOptional.get().getValue();
        }
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
        automationAppliaction.start();
        contactApplication.start();
        contractApplication.start();
        movieApplication.start();
        networkApplication.start();
        recipeApplication.start();
        shoppingListApplication.start();

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

        automationAppliaction.dump();
        contactApplication.dump();
        contractApplication.dump();
        movieApplication.dump();
        networkApplication.dump();
        recipeApplication.dump();
        shoppingListApplication.dump();
    }

    /**
     * beendet alle Dienste der Anwendung
     */
    public void stop() throws Throwable {

        //Daten speichern
        dump();

        //Anwendungen stoppen
        automationAppliaction.stop();
        contactApplication.stop();
        contractApplication.stop();
        movieApplication.stop();
        networkApplication.stop();
        recipeApplication.stop();
        shoppingListApplication.stop();

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
