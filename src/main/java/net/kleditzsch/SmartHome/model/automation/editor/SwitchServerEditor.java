package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Verwaltung Schaltserver
 */
public class SwitchServerEditor extends AbstractDatabaseEditor<SwitchServer> {

    private static final String DATABASE_KEY = "smarthome:automation:switchserver";

    /**
     * Schaltserver aus der Datenbank laden
     */
    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();
        List<String> switchServerList = db.lrange(DATABASE_KEY, 0, -1);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SwitchServer> data = getData();
        data.clear();
        for(String switschServerJson : switchServerList) {

            data.add(gson.fromJson(switschServerJson, SwitchServer.class));
        }

        lock.unlock();
    }

    /**
     * Schaltserver in die Datenbank speichern
     */
    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SwitchServer> data = getData();
        for(SwitchServer switchServer : data) {

            pipeline.lpush(DATABASE_KEY, gson.toJson(switchServer));
        }

        lock.unlock();

        pipeline.sync();
    }
}
