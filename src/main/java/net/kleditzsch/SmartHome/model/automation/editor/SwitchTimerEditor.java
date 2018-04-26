package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.util.datetime.CornjobCalculator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Timer Datenverwaltung
 */
public class SwitchTimerEditor extends AbstractDatabaseEditor<SwitchTimer> {

    private static final String DATABASE_KEY = "smarthome:automation:switchtimer";

    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();
        List<String> switchTimerList = db.lrange(DATABASE_KEY, 0, -1);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        data.clear();
        for(String switschTimerJson : switchTimerList) {

            data.add(gson.fromJson(switschTimerJson, SwitchTimer.class));
        }

        lock.unlock();
    }

    /**
     * berechnet die nächste Schaltzeit
     *
     * @param switchTimer Schalt Timer
     * @return nächste Schaltzeit
     */
    public LocalDateTime calculateNextExecutionTime(SwitchTimer switchTimer) {

        return CornjobCalculator.calculateNextRunTime(
                switchTimer.getMinute(),
                switchTimer.getHour(),
                switchTimer.getDay(),
                switchTimer.getWeekday(),
                switchTimer.getMonth()
        );
    }

    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        for(SwitchTimer switchTimer : data) {

            pipeline.lpush(DATABASE_KEY, gson.toJson(switchTimer));
        }

        lock.unlock();

        pipeline.sync();
    }
}
