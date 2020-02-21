package net.kleditzsch.smarthome.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.database.DatabaseEditor;
import net.kleditzsch.smarthome.model.settings.*;
import net.kleditzsch.smarthome.model.settings.Interface.Setting;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class SettingsEditor implements DatabaseEditor {

    public static final String COLLECTION = "global.settings";

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
        IntegerSetting applicationServerPort = new IntegerSetting(Settings.SERVER_PORT, 8080, 8080);
        knownSettings.add(applicationServerPort);
        IntegerSetting applicationServerSecurePort = new IntegerSetting(Settings.SERVER_SECURE_PORT, 8081, 8081);
        knownSettings.add(applicationServerSecurePort);
        StringSetting keyStorePassword = new StringSetting(Settings.SERVER_KEY_STORE_PASSWORD, "", "");
        knownSettings.add(keyStorePassword);

        BooleanSetting enableAutoBackup = new BooleanSetting(Settings.BACKUP_ENABLE_AUTO_BACKUP, true, true);
        knownSettings.add(enableAutoBackup);
        IntegerSetting backupAutoCleanupDays = new IntegerSetting(Settings.BACKUP_AUTO_CLEANUP_DAYS, 10, 10);
        knownSettings.add(backupAutoCleanupDays);

        BooleanSetting enableBackupFtpUpload = new BooleanSetting(Settings.BACKUP_FTP_UPLOAD, false, false);
        knownSettings.add(enableBackupFtpUpload);
        StringSetting ftpHost = new StringSetting(Settings.BACKUP_FTP_UPLOAD_HOST, "", "");
        knownSettings.add(ftpHost);
        IntegerSetting ftpPort = new IntegerSetting(Settings.BACKUP_FTP_UPLOAD_PORT, 21, 21);
        knownSettings.add(ftpPort);
        StringSetting ftpSecureType = new StringSetting(Settings.BACKUP_FTP_UPLOAD_SECURE_TYPE, "NONE", "NONE");
        knownSettings.add(ftpSecureType);
        StringSetting ftpUser = new StringSetting(Settings.BACKUP_FTP_UPLOAD_USER, "", "");
        knownSettings.add(ftpUser);
        StringSetting ftpPassword = new StringSetting(Settings.BACKUP_FTP_UPLOAD_PASSWORD, "", "");
        knownSettings.add(ftpPassword);
        StringSetting ftpUploadDir = new StringSetting(Settings.BACKUP_FTP_UPLOAD_DIRECTORY, "/smartHomeBackup", "/smartHomeBackup");
        knownSettings.add(ftpUploadDir);

        BooleanSetting enableBackupSuccessMail = new BooleanSetting(Settings.BACKUP_AUTO_SUCCESS_MAIL, false, false);
        knownSettings.add(enableBackupSuccessMail);
        BooleanSetting enableBackupErrorMail = new BooleanSetting(Settings.BACKUP_AUTO_ERROR_MAIL, true, true);
        knownSettings.add(enableBackupErrorMail);

        StringSetting mailHost = new StringSetting(Settings.MAIL_HOST, "", "");
        knownSettings.add(mailHost);
        IntegerSetting mailPort = new IntegerSetting(Settings.MAIL_PORT, 465, 465);
        knownSettings.add(mailPort);
        StringSetting mailSecureType = new StringSetting(Settings.MAIL_SECURE_TYPE, "SSL", "SSL");
        knownSettings.add(mailSecureType);
        StringSetting mailUser = new StringSetting(Settings.MAIL_USER, "", "");
        knownSettings.add(mailUser);
        StringSetting mailPassword = new StringSetting(Settings.MAIL_PASSWORD, "", "");
        knownSettings.add(mailPassword);
        StringSetting mailReceiverAddress = new StringSetting(Settings.MAIL_RECEIVER_ADDRESS, "", "");
        knownSettings.add(mailReceiverAddress);

        //Smarthome Einstellungen
        IntegerSetting sunriseOffset = new IntegerSetting(Settings.AUTOMATION_SUNRISE_OFFSET, 0, 0);
        knownSettings.add(sunriseOffset);
        IntegerSetting sunsetOffset = new IntegerSetting(Settings.AUTOMATION_SUNSET_OFFSET, 0, 0);
        knownSettings.add(sunsetOffset);
        DoubleSetting latitude = new DoubleSetting(Settings.AUTOMATION_LATITUDE, 50.0, 50.0);
        knownSettings.add(latitude);
        DoubleSetting longitude = new DoubleSetting(Settings.AUTOMATION_LONGITUDE, 12.0, 12.0);
        knownSettings.add(longitude);

        BooleanSetting fritzboxActive = new BooleanSetting(Settings.AUTOMATION_FB_ACTIVE, false, false);
        knownSettings.add(fritzboxActive);
        StringSetting fritzboxAddress = new StringSetting(Settings.AUTOMATION_FB_ADDRESS, "fritz.box", "fritz.box");
        knownSettings.add(fritzboxAddress);
        StringSetting fritzboxUser = new StringSetting(Settings.AUTOMATION_FB_USER, "", "");
        knownSettings.add(fritzboxUser);
        StringSetting fritzboxPassowrd = new StringSetting(Settings.AUTOMATION_FB_PASSWORD, "", "");
        knownSettings.add(fritzboxPassowrd);

        DoubleSetting energyElectricPrice = new DoubleSetting(Settings.AUTOMATION_ENERGY_ELECTRIC_PRICE, 0.1, 0.1);
        knownSettings.add(energyElectricPrice);
        DoubleSetting energyWaterPrice = new DoubleSetting(Settings.AUTOMATION_ENERGY_WATER_PRICE, 0.1, 0.1);
        knownSettings.add(energyWaterPrice);

        IntegerSetting paginationElementsAtPage = new IntegerSetting(Settings.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE, 10, 10);
        knownSettings.add(paginationElementsAtPage);

        BooleanSetting mqttActive = new BooleanSetting(Settings.AUTOMATION_MQTT_ACTIVE, false, false);
        knownSettings.add(mqttActive);
        StringSetting mqttBrokerAddress = new StringSetting(Settings.AUTOMATION_MQTT_BROKER_ADDRESS, "", "");
        knownSettings.add(mqttBrokerAddress);
        IntegerSetting mqttBrokerPort = new IntegerSetting(Settings.AUTOMATION_MQTT_BROKER_PORT, 1883, 1883);
        knownSettings.add(mqttBrokerPort);
        StringSetting mqttBrokerClientId = new StringSetting(Settings.AUTOMATION_MQTT_BROKER_CLIENT_ID, "SmartHome Server", "SmartHome Server");
        knownSettings.add(mqttBrokerClientId);
        StringSetting mqttUsername = new StringSetting(Settings.AUTOMATION_MQTT_BROKER_USERNAME, "", "");
        knownSettings.add(mqttUsername);
        StringSetting mqttPassword = new StringSetting(Settings.AUTOMATION_MQTT_BROKER_PASSWORD, "", "");
        knownSettings.add(mqttPassword);

        //Filme Einstellungen
        IntegerSetting paginationElementsAtPageAdmin = new IntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE, 25, 25);
        knownSettings.add(paginationElementsAtPageAdmin);
        IntegerSetting paginationElementsAtPageUser = new IntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(paginationElementsAtPageUser);
        IntegerSetting newestMoviesCount = new IntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT, 50, 50);
        knownSettings.add(newestMoviesCount);
        IntegerSetting bestMoviesCount = new IntegerSetting(Settings.MOVIE_BEST_MOVIES_COUNT, 50, 50);
        knownSettings.add(bestMoviesCount);
        StringSetting tmdbApiKey = new StringSetting(Settings.MOVIE_TMDB_API_KEY, "", "");
        knownSettings.add(tmdbApiKey);

        //Netzwerk Einstellungen
        StringSetting printerStateIp = new StringSetting(Settings.NETWORK_PRINTER_STATE_IP, "0.0.0.0", "0.0.0.0");
        knownSettings.add(printerStateIp);
        StringSetting nasStateIp = new StringSetting(Settings.NETWORK_NAS_STATE_IP, "0.0.0.0", "0.0.0.0");
        knownSettings.add(nasStateIp);

        //Rezepte Einstellungen
        IntegerSetting recipePaginationElementsAtPageAdmin = new IntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE, 25, 25);
        knownSettings.add(recipePaginationElementsAtPageAdmin);
        IntegerSetting recipePaginationElementsAtPageUser = new IntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(recipePaginationElementsAtPageUser);
        IntegerSetting newestRecipeCount = new IntegerSetting(Settings.RECIPE_NEWEST_RECIPE_COUNT, 10, 10);
        knownSettings.add(newestRecipeCount);
        StringSetting shoppingListId = new StringSetting(Settings.RECIPE_SHOPPING_LIST_ID, "", "");
        knownSettings.add(shoppingListId);

        //Kontakte Einstellungen
        IntegerSetting contactPaginationElementsAtPageUser = new IntegerSetting(Settings.CONTACT_PAGINATION_ELEMENTS_AT_USER_PAGE, 20, 20);
        knownSettings.add(contactPaginationElementsAtPageUser);
    }

    /**
     * Einstellungen aus der Danenbank laden
     */
    @Override
    public void load() {

        MongoCollection<Document> settingsCollection = SmartHome.i().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = settingsCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        settings.clear();
        for(Document document : iterator) {

            switch (Setting.Type.valueOf(document.getString("type"))) {

                case STRING:

                    StringSetting stringSetting = new StringSetting();
                    stringSetting.setName(Settings.valueOf(document.getString("name")));
                    stringSetting.setValue(document.getString("value"));
                    stringSetting.setDefaultValue(document.getString("defaultValue"));
                    stringSetting.resetChangedData();
                    settings.add(stringSetting);
                    break;
                case BOOLEAN:

                    BooleanSetting booleanSetting = new BooleanSetting();
                    booleanSetting.setName(Settings.valueOf(document.getString("name")));
                    booleanSetting.setValue(document.getBoolean("value"));
                    booleanSetting.setDefaultValue(document.getBoolean("defaultValue"));
                    booleanSetting.resetChangedData();
                    settings.add(booleanSetting);
                    break;
                case INTEGER:

                    IntegerSetting integerSetting = new IntegerSetting();
                    integerSetting.setName(Settings.valueOf(document.getString("name")));
                    integerSetting.setValue(document.getInteger("value"));
                    integerSetting.setDefaultValue(document.getInteger("defaultValue"));
                    integerSetting.resetChangedData();
                    settings.add(integerSetting);
                    break;
                case DOUBLE:

                    DoubleSetting doubleSetting = new DoubleSetting();
                    doubleSetting.setName(Settings.valueOf(document.getString("name")));
                    doubleSetting.setValue(document.getDouble("value"));
                    doubleSetting.setDefaultValue(document.getDouble("defaultValue"));
                    doubleSetting.resetChangedData();
                    settings.add(doubleSetting);
                    break;
                case LIST:

                    ListSetting listSetting = new ListSetting();
                    listSetting.setName(Settings.valueOf(document.getString("name")));
                    listSetting.getValue().addAll((Collection<? extends String>) document.get("value"));
                    listSetting.getDefaultValue().addAll((Collection<? extends String>) document.get("defaultValue"));
                    listSetting.resetChangedData();
                    settings.add(listSetting);
                    break;
            }
        };

        //mit bekannten Einstellungen falls nötig auffüllen
        for(Setting knownSetting : knownSettings) {

            Settings knownSettingName = knownSetting.getName();
            boolean found = false;
            for(Setting setting : settings) {

                if(setting.getName() == knownSettingName) {

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
    public Optional<Setting> getSetting(Settings name) {

        return settings.stream()
                .filter(setting -> setting.getName() == name)
                .findFirst();
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public StringSetting getStringSetting(Settings name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent()) {

            if(setting.get() instanceof StringSetting) {

                return (StringSetting) setting.get();
            }
            throw new IllegalStateException("Die Einstellung entspricht nicht dem angeforderten Typ");
        }
        throw new IllegalStateException("Die Einstellung ist nicht vorhanden oder konnte nicht geladen werden");
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public IntegerSetting getIntegerSetting(Settings name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent()) {

            if(setting.get() instanceof IntegerSetting) {

                return (IntegerSetting) setting.get();
            }
            throw new IllegalStateException("Die Einstellung entspricht nicht dem angeforderten Typ");
        }
        throw new IllegalStateException("Die Einstellung ist nicht vorhanden oder konnte nicht geladen werden");
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public DoubleSetting getDoubleSetting(Settings name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent()) {

            if(setting.get() instanceof DoubleSetting) {

                return (DoubleSetting) setting.get();
            }
            throw new IllegalStateException("Die Einstellung entspricht nicht dem angeforderten Typ");
        }
        throw new IllegalStateException("Die Einstellung ist nicht vorhanden oder konnte nicht geladen werden");
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public BooleanSetting getBooleanSetting(Settings name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent()) {

            if(setting.get() instanceof BooleanSetting) {

                return (BooleanSetting) setting.get();
            }
            throw new IllegalStateException("Die Einstellung entspricht nicht dem angeforderten Typ");
        }
        throw new IllegalStateException("Die Einstellung ist nicht vorhanden oder konnte nicht geladen werden");
    }

    /**
     * gibt eine Einstellung zurück
     *
     * @param name Name der Einstellung
     * @return Einstellung
     */
    public ListSetting getListSetting(Settings name) {

        Optional<Setting> setting = getSetting(name);
        if(setting.isPresent()) {

            if(setting.get() instanceof ListSetting) {

                return (ListSetting) setting.get();
            }
            throw new IllegalStateException("Die Einstellung entspricht nicht dem angeforderten Typ");
        }
        throw new IllegalStateException("Die Einstellung ist nicht vorhanden oder konnte nicht geladen werden");
    }

    /**
     * Einstellungen in der Datenbank speichern
     */
    @Override
    public void dump() {

        MongoCollection<Document> settingsCollection = SmartHome.i().getDatabaseCollection(COLLECTION);
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
