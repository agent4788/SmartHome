package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Verwaltung schaltbare Elemente
 */
public class SwitchableEditor extends AbstractDatabaseEditor<Switchable> {

    private static final String DATABASE_KEY = "smarthome:automation:switchable";

    /**
     * schaltbare Elemente aus der Datenbank laden
     */
    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();
        JsonParser jp = new JsonParser();
        List<String> switchableList = db.lrange(DATABASE_KEY, 0, -1);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Switchable> data = getData();
        data.clear();
        for(String switchableJson : switchableList) {

            JsonObject jo = jp.parse(switchableJson).getAsJsonObject();
            Class clazz = getTypeClass(jo.get("type").getAsString());
            Switchable sensorValue = (Switchable) gson.fromJson(switchableJson, clazz);
            data.add(sensorValue);
        }

        lock.unlock();
    }

    /**
     * gibt eine Liste mit den Elementen der IDs zurück (gefiltert nach einem Typ)
     *
     * @param type Typ filtern
     * @return Liste mit Elementen
     */
    public List<Switchable> getSublistByType(Class type) {

        return getData().stream()
                .filter(e -> type.isInstance(e))
                .collect(Collectors.toList());
    }

    /**
     * schaltbare Elemente in die Datenbank speichern
     */
    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<Switchable> data = getData();
        for(Switchable sensorValue : data) {

            pipeline.lpush(DATABASE_KEY, gson.toJson(sensorValue));
        }

        lock.unlock();

        pipeline.sync();
    }

    /**
     * gibt das Class Objekt zum Typ zurück
     *
     * @param typeStr Typ String
     * @return Class Objekt
     */
    public Class getTypeClass(String typeStr) {

        AutomationElement.Type type = AutomationElement.Type.valueOf(typeStr);
        switch(type) {

            case SWITCHABLE_AVM_SOCKET:

                return AvmSocket.class;
            case SWITCHABLE_FRITZ_BOX_REBOOT_RECONNECT:

                return FritzBoxRebootReconnect.class;
            case SWITCHABLE_FRITZ_BOX_WLAN:

                return FritzBoxWirelessLan.class;
            case SWITCHABLE_OUTPUT:

                return Output.class;
            case SWITCHABLE_SCRIPT_DOUBLE:

                return ScriptDouble.class;
            case SWITCHABLE_SCRIPT_SINGLE:

                return ScriptSingle.class;
            case SWITCHABLE_WAKE_ON_LAN:

                return WakeOnLan.class;
            default:
                throw new IllegalStateException("Ungültiger Typ");
        }
    }
}
