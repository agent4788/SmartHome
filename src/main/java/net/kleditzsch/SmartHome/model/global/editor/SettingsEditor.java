package net.kleditzsch.SmartHome.model.global.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.DatabaseEditor;
import net.kleditzsch.SmartHome.model.global.settings.*;
import net.kleditzsch.SmartHome.model.global.settings.Interface.Setting;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SettingsEditor implements DatabaseEditor {

    public static final String COLLECTION = "global.settings";

    //Globale Einstellungen
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String SERVER_SECURE_PORT = "SERVER_SECURE_PORT";
    public static final String SERVER_KEY_STORE_PASSWORD = "SERVER_KEY_STORE_PASSWORD";
    public static final String BACKUP_ENABLE_AUTO_BACKUP = "BACKUP_ENABLE_AUTO_BACKUP";
    public static final String BACKUP_AUTO_CLEANUP_DAYS = "BACKUP_AUTO_CLEANUP_DAYS";
    public static final String BACKUP_AUTO_SUCCESS_MAIL = "BACKUP_AUTO_SUCCESS_MAIL";
    public static final String BACKUP_AUTO_ERROR_MAIL = "BACKUP_AUTO_ERROR_MAIL";
    public static final String BACKUP_FTP_UPLOAD = "BACKUP_FTP_UPLOAD";
    public static final String BACKUP_FTP_UPLOAD_HOST = "BACKUP_FTP_UPLOAD_HOST";
    public static final String BACKUP_FTP_UPLOAD_PORT = "BACKUP_FTP_UPLOAD_PORT";
    public static final String BACKUP_FTP_UPLOAD_SECURE_TYPE = "BACKUP_FTP_UPLOAD_SECURE_TYPE";
    public static final String BACKUP_FTP_UPLOAD_USER = "BACKUP_FTP_UPLOAD_USER";
    public static final String BACKUP_FTP_UPLOAD_PASSWORD = "BACKUP_FTP_UPLOAD_PASSWORD";
    public static final String BACKUP_FTP_UPLOAD_DIRECTORY = "BACKUP_FTP_UPLOAD_DIRECTORY";
    public static final String MAIL_HOST = "MAIL_HOST";
    public static final String MAIL_PORT = "MAIL_PORT";
    public static final String MAIL_SECURE_TYPE = "MAIL_SECURE_TYPE";
    public static final String MAIL_USER = "MAIL_USER";
    public static final String MAIL_PASSWORD = "MAIL_PASSWORD";
    public static final String MAIL_RECEIVER_ADDRESS = "MAIL_RECEIVER_ADDRESS";

    //Smarthome Einstellungen
    public static final String AUTOMATION_SUNRISE_OFFSET = "AUTOMATION_SUNRISE_OFFSET";
    public static final String AUTOMATION_SUNSET_OFFSET = "AUTOMATION_SUNSET_OFFSET";
    public static final String AUTOMATION_LATITUDE = "AUTOMATION_LATITUDE";
    public static final String AUTOMATION_LONGITUDE = "AUTOMATION_LONGITUDE";
    public static final String AUTOMATION_FB_ACTIVE = "AUTOMATION_FB_ACTIVE";
    public static final String AUTOMATION_FB_ADDRESS = "AUTOMATION_FB_ADDRESS";
    public static final String AUTOMATION_FB_USER = "AUTOMATION_FB_USER";
    public static final String AUTOMATION_FB_PASSWORD = "AUTOMATION_FB_PASSWORD";
    public static final String AUTOMATION_ENERGY_ELECTRIC_PRICE = "AUTOMATION_ENERGY_ELECTRIC_PRICE";
    public static final String AUTOMATION_ENERGY_WATER_PRICE = "AUTOMATION_ENERGY_WATER_PRICE";
    public static final String AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE = "AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE";
    public static final String AUTOMATION_MQTT_ACTIVE = "AUTOMATION_MQTT_ACTIVE";
    public static final String AUTOMATION_MQTT_BROKER_ADDRESS = "AUTOMATION_MQTT_BROKER_ADDRESS";
    public static final String AUTOMATION_MQTT_BROKER_PORT = "AUTOMATION_MQTT_BROKER_PORT";
    public static final String AUTOMATION_MQTT_BROKER_CLIENT_ID = "AUTOMATION_MQTT_BROKER_CLIENT_ID";
    public static final String AUTOMATION_MQTT_BROKER_USERNAME = "AUTOMATION_MQTT_BROKER_USERNAME";
    public static final String AUTOMATION_MQTT_BROKER_PASSWORD = "AUTOMATION_MQTT_BROKER_PASSWORD";

    //Filme Einstellungen
    public static final String MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE = "MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE";
    public static final String MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE = "MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE";
    public static final String MOVIE_NEWEST_MOVIES_COUNT = "MOVIE_NEWEST_MOVIES_COUNT";
    public static final String MOVIE_BEST_MOVIES_COUNT = "MOVIE_BEST_MOVIES_COUNT";
    public static final String MOVIE_TMDB_API_KEY = "MOVIE_TMDB_API_KEY";

    //Netzwerk Einstellungen
    public static final String NETWORK_PRINTER_STATE_IP = "NETWORK_PRINTER_STATE_IP";
    public static final String NETWORK_NAS_STATE_IP = "NETWORK_NAS_STATE_IP";

    //Rezepte Einstellungen
    public static final String RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE = "RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE";
    public static final String RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE = "RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE";
    public static final String RECIPE_NEWEST_RECIPE_COUNT = "RECIPE_NEWEST_RECIPE_COUNT";
    public static final String RECIPE_SHOPPING_LIST_ID = "RECIPE_SHOPPING_LIST_ID";

    //Kontakte
    public static final String CONTACT_PAGINATION_ELEMENTS_AT_USER_PAGE = "CONTACT_PAGINATION_ELEMENTS_AT_USER_PAGE";

    /**
     * Lock objekt
     */
    private volatile ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * Einstellungen
     */
    private List<Setting> settings = new ArrayList<>(50);

    /**
     * Bekannte Einstellungen
     */
    private List<Setting> knownSettings = new ArrayList<>(50);

    public SettingsEditor() {

        //Webserver Einstellungen
        IntegerSetting applicationServerPort = new IntegerSetting(SERVER_PORT, 8080, 8080);
        knownSettings.add(applicationServerPort);
        IntegerSetting applicationServerSecurePort = new IntegerSetting(SERVER_SECURE_PORT, 8081, 8081);
        knownSettings.add(applicationServerSecurePort);
        StringSetting keyStorePassword = new StringSetting(SERVER_KEY_STORE_PASSWORD, "", "");
        knownSettings.add(keyStorePassword);

        BooleanSetting enableAutoBackup = new BooleanSetting(BACKUP_ENABLE_AUTO_BACKUP, true, true);
        knownSettings.add(enableAutoBackup);
        IntegerSetting backupAutoCleanupDays = new IntegerSetting(BACKUP_AUTO_CLEANUP_DAYS, 10, 10);
        knownSettings.add(backupAutoCleanupDays);

        BooleanSetting enableBackupFtpUpload = new BooleanSetting(BACKUP_FTP_UPLOAD, false, false);
        knownSettings.add(enableBackupFtpUpload);
        StringSetting ftpHost = new StringSetting(BACKUP_FTP_UPLOAD_HOST, "", "");
        knownSettings.add(ftpHost);
        IntegerSetting ftpPort = new IntegerSetting(BACKUP_FTP_UPLOAD_PORT, 21, 21);
        knownSettings.add(ftpPort);
        StringSetting ftpSecureType = new StringSetting(BACKUP_FTP_UPLOAD_SECURE_TYPE, "NONE", "NONE");
        knownSettings.add(ftpSecureType);
        StringSetting ftpUser = new StringSetting(BACKUP_FTP_UPLOAD_USER, "", "");
        knownSettings.add(ftpUser);
        StringSetting ftpPassword = new StringSetting(BACKUP_FTP_UPLOAD_PASSWORD, "", "");
        knownSettings.add(ftpPassword);
        StringSetting ftpUploadDir = new StringSetting(BACKUP_FTP_UPLOAD_DIRECTORY, "/smartHomeBackup", "/smartHomeBackup");
        knownSettings.add(ftpUploadDir);

        BooleanSetting enableBackupSuccessMail = new BooleanSetting(BACKUP_AUTO_SUCCESS_MAIL, false, false);
        knownSettings.add(enableBackupSuccessMail);
        BooleanSetting enableBackupErrorMail = new BooleanSetting(BACKUP_AUTO_ERROR_MAIL, true, true);
        knownSettings.add(enableBackupErrorMail);

        StringSetting mailHost = new StringSetting(MAIL_HOST, "", "");
        knownSettings.add(mailHost);
        IntegerSetting mailPort = new IntegerSetting(MAIL_PORT, 465, 465);
        knownSettings.add(mailPort);
        StringSetting mailSecureType = new StringSetting(MAIL_SECURE_TYPE, "SSL", "SSL");
        knownSettings.add(mailSecureType);
        StringSetting mailUser = new StringSetting(MAIL_USER, "", "");
        knownSettings.add(mailUser);
        StringSetting mailPassword = new StringSetting(MAIL_PASSWORD, "", "");
        knownSettings.add(mailPassword);
        StringSetting mailReceiverAddress = new StringSetting(MAIL_RECEIVER_ADDRESS, "", "");
        knownSettings.add(mailReceiverAddress);

        //Smarthome Einstellungen
        IntegerSetting sunriseOffset = new IntegerSetting(AUTOMATION_SUNRISE_OFFSET, 0, 0);
        knownSettings.add(sunriseOffset);
        IntegerSetting sunsetOffset = new IntegerSetting(AUTOMATION_SUNSET_OFFSET, 0, 0);
        knownSettings.add(sunsetOffset);
        DoubleSetting latitude = new DoubleSetting(AUTOMATION_LATITUDE, 50.0, 50.0);
        knownSettings.add(latitude);
        DoubleSetting longitude = new DoubleSetting(AUTOMATION_LONGITUDE, 12.0, 12.0);
        knownSettings.add(longitude);

        BooleanSetting fritzboxActive = new BooleanSetting(AUTOMATION_FB_ACTIVE, false, false);
        knownSettings.add(fritzboxActive);
        StringSetting fritzboxAddress = new StringSetting(AUTOMATION_FB_ADDRESS, "fritz.box", "fritz.box");
        knownSettings.add(fritzboxAddress);
        StringSetting fritzboxUser = new StringSetting(AUTOMATION_FB_USER, "", "");
        knownSettings.add(fritzboxUser);
        StringSetting fritzboxPassowrd = new StringSetting(AUTOMATION_FB_PASSWORD, "", "");
        knownSettings.add(fritzboxPassowrd);

        DoubleSetting energyElectricPrice = new DoubleSetting(AUTOMATION_ENERGY_ELECTRIC_PRICE, 0.1, 0.1);
        knownSettings.add(energyElectricPrice);
        DoubleSetting energyWaterPrice = new DoubleSetting(AUTOMATION_ENERGY_WATER_PRICE, 0.1, 0.1);
        knownSettings.add(energyWaterPrice);

        IntegerSetting paginationElementsAtPage = new IntegerSetting(AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE, 10, 10);
        knownSettings.add(paginationElementsAtPage);

        BooleanSetting mqttActive = new BooleanSetting(AUTOMATION_MQTT_ACTIVE, false, false);
        knownSettings.add(mqttActive);
        StringSetting mqttBrokerAddress = new StringSetting(AUTOMATION_MQTT_BROKER_ADDRESS, "", "");
        knownSettings.add(mqttBrokerAddress);
        IntegerSetting mqttBrokerPort = new IntegerSetting(AUTOMATION_MQTT_BROKER_PORT, 1883, 1883);
        knownSettings.add(mqttBrokerPort);
        StringSetting mqttBrokerClientId = new StringSetting(AUTOMATION_MQTT_BROKER_CLIENT_ID, "SmartHome Server", "SmartHome Server");
        knownSettings.add(mqttBrokerClientId);
        StringSetting mqttUsername = new StringSetting(AUTOMATION_MQTT_BROKER_USERNAME, "", "");
        knownSettings.add(mqttUsername);
        StringSetting mqttPassword = new StringSetting(AUTOMATION_MQTT_BROKER_PASSWORD, "", "");
        knownSettings.add(mqttPassword);

        //Filme Einstellungen
        IntegerSetting paginationElementsAtPageAdmin = new IntegerSetting(MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE, 25, 25);
        knownSettings.add(paginationElementsAtPageAdmin);
        IntegerSetting paginationElementsAtPageUser = new IntegerSetting(MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(paginationElementsAtPageUser);
        IntegerSetting newestMoviesCount = new IntegerSetting(MOVIE_NEWEST_MOVIES_COUNT, 50, 50);
        knownSettings.add(newestMoviesCount);
        IntegerSetting bestMoviesCount = new IntegerSetting(MOVIE_BEST_MOVIES_COUNT, 50, 50);
        knownSettings.add(bestMoviesCount);
        StringSetting tmdbApiKey = new StringSetting(MOVIE_TMDB_API_KEY, "", "");
        knownSettings.add(tmdbApiKey);

        //Netzwerk Einstellungen
        StringSetting printerStateIp = new StringSetting(NETWORK_PRINTER_STATE_IP, "0.0.0.0", "0.0.0.0");
        knownSettings.add(printerStateIp);
        StringSetting nasStateIp = new StringSetting(NETWORK_NAS_STATE_IP, "0.0.0.0", "0.0.0.0");
        knownSettings.add(nasStateIp);

        //Rezepte Einstellungen
        IntegerSetting recipePaginationElementsAtPageAdmin = new IntegerSetting(RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE, 25, 25);
        knownSettings.add(recipePaginationElementsAtPageAdmin);
        IntegerSetting recipePaginationElementsAtPageUser = new IntegerSetting(RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(recipePaginationElementsAtPageUser);
        IntegerSetting newestRecipeCount = new IntegerSetting(RECIPE_NEWEST_RECIPE_COUNT, 10, 10);
        knownSettings.add(newestRecipeCount);
        StringSetting shoppingListId = new StringSetting(RECIPE_SHOPPING_LIST_ID, "", "");
        knownSettings.add(shoppingListId);

        //Kontakte Einstellungen
        IntegerSetting contactPaginationElementsAtPageUser = new IntegerSetting(CONTACT_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(contactPaginationElementsAtPageUser);
    }

    /**
     * Einstellungen aus der Danenbank laden
     */
    @Override
    public void load() {

        MongoCollection settingsCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = settingsCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        settings.clear();
        for(Document document : iterator) {

            switch (Setting.Type.valueOf(document.getString("type"))) {

                case STRING:

                    StringSetting stringSetting = new StringSetting();
                    stringSetting.setName(document.getString("name"));
                    stringSetting.setValue(document.getString("value"));
                    stringSetting.setDefaultValue(document.getString("defaultValue"));
                    stringSetting.resetChangedData();
                    settings.add(stringSetting);
                    break;
                case BOOLEAN:

                    BooleanSetting booleanSetting = new BooleanSetting();
                    booleanSetting.setName(document.getString("name"));
                    booleanSetting.setValue(document.getBoolean("value"));
                    booleanSetting.setDefaultValue(document.getBoolean("defaultValue"));
                    booleanSetting.resetChangedData();
                    settings.add(booleanSetting);
                    break;
                case INTEGER:

                    IntegerSetting integerSetting = new IntegerSetting();
                    integerSetting.setName(document.getString("name"));
                    integerSetting.setValue(document.getInteger("value"));
                    integerSetting.setDefaultValue(document.getInteger("defaultValue"));
                    integerSetting.resetChangedData();
                    settings.add(integerSetting);
                    break;
                case DOUBLE:

                    DoubleSetting doubleSetting = new DoubleSetting();
                    doubleSetting.setName(document.getString("name"));
                    doubleSetting.setValue(document.getDouble("value"));
                    doubleSetting.setDefaultValue(document.getDouble("defaultValue"));
                    doubleSetting.resetChangedData();
                    settings.add(doubleSetting);
                    break;
                case LIST:

                    ListSetting listSetting = new ListSetting();
                    listSetting.setName(document.getString("name"));
                    listSetting.getValue().addAll((Collection<? extends String>) document.get("value"));
                    listSetting.getDefaultValue().addAll((Collection<? extends String>) document.get("defaultValue"));
                    listSetting.resetChangedData();
                    settings.add(listSetting);
                    break;
            }
        };

        //mit bekannten Einstellungen falls nötig auffüllen
        for(Setting knownSetting : knownSettings) {

            String knownSettingName = knownSetting.getName();
            boolean found = false;
            for(Setting setting : settings) {

                if(setting.getName().equalsIgnoreCase(knownSettingName)) {

                    found = true;
                }
            }
            if(!found) {

                settings.add(knownSetting);
            }
        }

        lock.unlock();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<Setting> getSetting(String name) {

        return settings.stream().filter(setting -> setting.getName().equals(name)).findFirst();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<StringSetting> getStringSetting(String name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent() && setting.get() instanceof StringSetting) {

            return Optional.of((StringSetting) setting.get());
        }
        return Optional.empty();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<IntegerSetting> getIntegerSetting(String name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent() && setting.get() instanceof IntegerSetting) {

            return Optional.of((IntegerSetting) setting.get());
        }
        return Optional.empty();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<DoubleSetting> getDoubleSetting(String name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent() && setting.get() instanceof DoubleSetting) {

            return Optional.of((DoubleSetting) setting.get());
        }
        return Optional.empty();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<BooleanSetting> getBooleanSetting(String name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent() && setting.get() instanceof BooleanSetting) {

            return Optional.of((BooleanSetting) setting.get());
        }
        return Optional.empty();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public Optional<ListSetting> getListSetting(String name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent() && setting.get() instanceof ListSetting) {

            return Optional.of((ListSetting) setting.get());
        }
        return Optional.empty();
    }

    /**
     * Einstellungen in der Datenbank speichern
     */
    @Override
    public void dump() {

        MongoCollection settingsCollection = Application.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        for(Setting setting : settings) {

            if(setting.isChangedData()) {

                switch (setting.getType()) {

                    case STRING:

                        settingsCollection.updateOne(
                                eq("name", setting.getName()),
                                combine(
                                        set("type", setting.getType().toString()),
                                        set("name", setting.getName()),
                                        set("value", ((StringSetting)setting).getValue()),
                                        set("defaultValue", ((StringSetting)setting).getDefaultValue())

                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case BOOLEAN:

                        settingsCollection.updateOne(
                                eq("name", setting.getName()),
                                combine(
                                        set("type", setting.getType().toString()),
                                        set("name", setting.getName()),
                                        set("value", ((BooleanSetting)setting).getValue()),
                                        set("defaultValue", ((BooleanSetting)setting).getDefaultValue())

                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case INTEGER:

                        settingsCollection.updateOne(
                                eq("name", setting.getName()),
                                combine(
                                        set("type", setting.getType().toString()),
                                        set("name", setting.getName()),
                                        set("value", ((IntegerSetting)setting).getValue()),
                                        set("defaultValue", ((IntegerSetting)setting).getDefaultValue())

                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case DOUBLE:

                        settingsCollection.updateOne(
                                eq("name", setting.getName()),
                                combine(
                                        set("type", setting.getType().toString()),
                                        set("name", setting.getName()),
                                        set("value", ((DoubleSetting)setting).getValue()),
                                        set("defaultValue", ((DoubleSetting)setting).getDefaultValue())

                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case LIST:

                        settingsCollection.updateOne(
                                eq("name", setting.getName()),
                                combine(
                                        set("type", setting.getType().toString()),
                                        set("name", setting.getName()),
                                        set("value", ((ListSetting)setting).getValue()),
                                        set("defaultValue", ((ListSetting)setting).getDefaultValue())

                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                }

                setting.resetChangedData();
            }
        }

        lock.unlock();
    }

    /**
     * gibt das Lockobjekt zurück
     *
     * @return Lockobjekt
     */
    @Override
    public ReentrantReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Leselock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.ReadLock readLock() {
        return readWriteLock.readLock();
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Schreiblock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.WriteLock writeLock() {
        return readWriteLock.writeLock();
    }

    /**
     * gibt das Class Objekt zum Typ zurück
     *
     * @param typeStr Typ String
     * @return Class Objekt
     */
    public Class getTypeClass(String typeStr) {

        Setting.Type type = Setting.Type.valueOf(typeStr);
        switch(type) {

            case BOOLEAN:

                return BooleanSetting.class;
            case DOUBLE:

                return DoubleSetting.class;
            case INTEGER:

                return IntegerSetting.class;
            case LIST:

                return ListSetting.class;
            case STRING:

                return StringSetting.class;
            default:
                throw new IllegalStateException("Ungültiger Typ");
        }
    }
}
