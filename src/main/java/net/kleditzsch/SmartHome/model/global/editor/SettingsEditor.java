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

    private static final String COLLECTION = "global.settings";

    //Globale Einstellungen
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String SERVER_SECURE_PORT = "SERVER_SECURE_PORT";
    public static final String SERVER_KEY_STORE_PASSWORD = "SERVER_KEY_STORE_PASSWORD";
    public static final String SERVER_KEY_MANAGER_PASSWORD = "SERVER_KEY_MANAGER_PASSWORD";

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

    //Filme Einstellungen
    public static final String MOVIE_PAGNATION_ELEMENTS_AT_ADMIN_PAGE = "MOVIE_PAGNATION_ELEMENTS_AT_ADMIN_PAGE";
    public static final String MOVIE_PAGNATION_ELEMENTS_AT_USER_PAGE = "MOVIE_PAGNATION_ELEMENTS_AT_USER_PAGE";

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
        StringSetting keyManagerPassword = new StringSetting(SERVER_KEY_MANAGER_PASSWORD, "", "");
        knownSettings.add(keyManagerPassword);

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

        //Filme Einstellungen
        IntegerSetting paginationElementsAtPageAdmin = new IntegerSetting(MOVIE_PAGNATION_ELEMENTS_AT_ADMIN_PAGE, 25, 25);
        knownSettings.add(paginationElementsAtPageAdmin);
        IntegerSetting paginationElementsAtPageUser = new IntegerSetting(MOVIE_PAGNATION_ELEMENTS_AT_USER_PAGE, 25, 25);
        knownSettings.add(paginationElementsAtPageUser);
    }

    /**
     * Einstellungen aus der Danenbank laden
     */
    @Override
    public void load() {

        MongoCollection settingsCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = settingsCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        settings.clear();
        iterator.forEach((Block<Document>) document -> {

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
        });

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
