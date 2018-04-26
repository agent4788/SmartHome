package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
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
     * erstellt eine kopie vom Schaltbaren Element
     *
     * @param switchable Schaltbares Element
     * @return Kopie des Schaltbaren Elements
     */
    public Switchable copyOf(Switchable switchable) {

        if(switchable instanceof TPlinkSocket) {

            TPlinkSocket oldSocket = (TPlinkSocket) switchable;
            TPlinkSocket newSocket = new TPlinkSocket();

            //Allgemeine Daten
            newSocket.setId(ID.of(oldSocket.getId().get()));

            //Spezifische Daten
            newSocket.setIpAddress(oldSocket.getIpAddress());
            newSocket.setPort(oldSocket.getPort());
            newSocket.setSocketType(oldSocket.getSocketType());
            if(oldSocket.getCurrentSensor().isPresent()) newSocket.setCurrentSensor(ID.of(oldSocket.getCurrentSensor().get().get()));
            if(oldSocket.getVoltageSensor().isPresent()) newSocket.setVoltageSensor(ID.of(oldSocket.getVoltageSensor().get().get()));
            if(oldSocket.getEnergySensorId().isPresent()) newSocket.setEnergySensorId(ID.of(oldSocket.getEnergySensorId().get().get()));
            if(oldSocket.getPowerSensorId().isPresent()) newSocket.setPowerSensorId(ID.of(oldSocket.getPowerSensorId().get().get()));

            return newSocket;

        } else if(switchable instanceof AvmSocket) {

            AvmSocket oldSocket = (AvmSocket) switchable;
            AvmSocket newSocket = new AvmSocket();

            //Allgemeine Daten
            newSocket.setId(ID.of(oldSocket.getId().get()));

            //Spezifische Daten
            newSocket.setIdentifier(oldSocket.getIdentifier());
            newSocket.setTempSensorId(ID.of(oldSocket.getTempSensorId().get()));
            newSocket.setPowerSensorId(ID.of(oldSocket.getPowerSensorId().get()));
            newSocket.setEnergySensorId(ID.of(oldSocket.getEnergySensorId().get()));

            return newSocket;

        } else if(switchable instanceof Output) {

            Output oldOutput = (Output) switchable;
            Output newOutput = new Output();

            //Allgemeine Daten
            newOutput.setId(ID.of(oldOutput.getId().get()));

            //Spezifische Daten
            newOutput.setSwitchServerId(ID.of(oldOutput.getSwitchServerId().get()));
            newOutput.setPin(oldOutput.getPin());

            return newOutput;

        } else if(switchable instanceof ScriptDouble) {

            ScriptDouble oldScript = (ScriptDouble) switchable;
            ScriptDouble newScript = new ScriptDouble();

            //Allgemeine Daten
            newScript.setId(ID.of(oldScript.getId().get()));

            //Spezifische Daten
            newScript.setWorkingDir(oldScript.getWorkingDir());
            newScript.setOnCommand(oldScript.getOnCommand());
            newScript.setOffCommand(oldScript.getOffCommand());

            return newScript;

        } else if(switchable instanceof ScriptSingle) {

            ScriptSingle oldScript = (ScriptSingle) switchable;
            ScriptSingle newScript = new ScriptSingle();

            //Allgemeine Daten
            newScript.setId(ID.of(oldScript.getId().get()));

            //Spezifische Daten
            newScript.setWorkingDir(oldScript.getWorkingDir());
            newScript.setCommand(oldScript.getCommand());

            return newScript;

        } else if(switchable instanceof WakeOnLan) {

            WakeOnLan oldWol = (WakeOnLan) switchable;
            WakeOnLan newWol = new WakeOnLan();

            //Allgemeine Daten
            newWol.setId(ID.of(oldWol.getId().get()));

            //Spezifische Daten
            newWol.setIpAddress(oldWol.getIpAddress());
            newWol.setMac(oldWol.getMac());

            return newWol;
        }

        LoggerUtil.getLogger(this).severe("Das übergebene schaltbare Element " + switchable.getName() + " ist ein nicht bekannter Typ!");
        return null;
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

            case SWITCHABLE_TPLINK_SOCKET:

                return TPlinkSocket.class;
            case SWITCHABLE_AVM_SOCKET:

                return AvmSocket.class;
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
