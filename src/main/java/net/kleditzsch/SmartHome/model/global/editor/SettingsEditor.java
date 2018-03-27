package net.kleditzsch.SmartHome.model.global.editor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.global.settings.*;
import net.kleditzsch.SmartHome.model.global.settings.Interface.Setting;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SettingsEditor extends AbstractDatabaseEditor {

    /**
     * Lock objekt
     */
    private volatile ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static final String DATABASE_KEY = "smarthome:global:settings";

    //Globale Einstellungen
    public static final String SERVER_ADDRESS = "APPLICATION_SERVER_ADDRESS";
    public static final String SERVER_PORT = "APPLICATION_SERVER_PORT";

    //Smarthome Einstellungen
    public static final String SUNRISE_OFFSET = "SUNRISE_OFFSET";
    public static final String SUNSET_OFFSET = "SUNSET_OFFSET";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String FB_ACTIVE = "FB_ACTIVE";
    public static final String FB_ADDRESS = "FB_ADDRESS";
    public static final String FB_USER = "FB_USER";
    public static final String FB_PASSWORD = "FB_PASSWORD";
    public static final String FB_5GHZ_WLAN_ENABLED = "FB_5GHZ_WLAN_ENABLED";
    public static final String ENERGY_ELECTRIC_PRICE = "ENERGY_ELECTRIC_PRICE";
    public static final String ENERGY_WATER_PRICE = "ENERGY_WATER_PRICE";
    public static final String ENERGY_GAS_PRICE = "ENERGY_GAS_PRICE";

    /**
     * Einstellungen
     */
    private List<Setting> settings = new ArrayList<>();

    /**
     * Bekannte Einstellungen
     */
    private List<Setting> knownSettings = new ArrayList<>();

    public SettingsEditor() {

        //Globale Einstellungen
        StringSetting applicationServerAddress = new StringSetting(SERVER_ADDRESS, "localhost", "localhost");
        knownSettings.add(applicationServerAddress);
        IntegerSetting applicationServerPort = new IntegerSetting(SERVER_PORT, 8080, 8080);
        knownSettings.add(applicationServerPort);

        //Smarthome Einstellungen
        IntegerSetting sunriseOffset = new IntegerSetting(SUNRISE_OFFSET, 0, 0);
        knownSettings.add(sunriseOffset);
        IntegerSetting sunsetOffset = new IntegerSetting(SUNSET_OFFSET, 0, 0);
        knownSettings.add(sunsetOffset);
        DoubleSetting latitude = new DoubleSetting(LATITUDE, 50.0, 50.0);
        knownSettings.add(latitude);
        DoubleSetting longitude = new DoubleSetting(LONGITUDE, 12.0, 12.0);
        knownSettings.add(longitude);

        BooleanSetting fritzboxActive = new BooleanSetting(FB_ACTIVE, false, false);
        knownSettings.add(fritzboxActive);
        StringSetting fritzboxAddress = new StringSetting(FB_ADDRESS, "fritz.box", "fritz.box");
        knownSettings.add(fritzboxAddress);
        StringSetting fritzboxUser = new StringSetting(FB_USER, "", "");
        knownSettings.add(fritzboxUser);
        StringSetting fritzboxPassowrd = new StringSetting(FB_PASSWORD, "", "");
        knownSettings.add(fritzboxPassowrd);
        BooleanSetting fritzbox5GHzWlan = new BooleanSetting(FB_5GHZ_WLAN_ENABLED, false, false);
        knownSettings.add(fritzbox5GHzWlan);

        DoubleSetting energyElectricPrice = new DoubleSetting(ENERGY_ELECTRIC_PRICE, 0.0, 0.0);
        knownSettings.add(energyElectricPrice);
        DoubleSetting energyWaterPrice = new DoubleSetting(ENERGY_WATER_PRICE, 0.0, 0.0);
        knownSettings.add(energyWaterPrice);
        DoubleSetting energyGasPrice = new DoubleSetting(ENERGY_GAS_PRICE, 0.0, 0.0);
        knownSettings.add(energyGasPrice);
    }

    /**
     * Einstellungen aus der Danenbank laden
     */
    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        JsonParser jp = new JsonParser();
        Gson gson = Application.getInstance().getGson();

        List<String> settingsList = db.lrange(DATABASE_KEY, 0, -1);
        settings.clear();
        for(String settingJson : settingsList) {

            JsonObject jo = jp.parse(settingJson).getAsJsonObject();
            Class clazz = getTypeClass(jo.get("type").getAsString());
            Setting setting = (Setting) gson.fromJson(settingJson, clazz);
            settings.add(setting);
        }

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
    }



    /**
     * Einstellungen in der Datenbank speichern
     */
    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);
        for(Setting setting : settings) {

            pipeline.lpush(DATABASE_KEY, gson.toJson(setting));
        }
        pipeline.sync();
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
