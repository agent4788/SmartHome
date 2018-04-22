package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RoomEditor extends AbstractDatabaseEditor<Room> {

    private static final String DATABASE_KEY = "smarthome:automation:room";

    /**
     * läd die Räume aus der Datenbank
     */
    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();
        List<String> roomList = db.lrange(DATABASE_KEY, 0, -1);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Room> data = getData();
        data.clear();
        for(String roomJson : roomList) {

            data.add(gson.fromJson(roomJson, Room.class));
        }

        lock.unlock();
    }

    /**
     * gibt eine Liste aller Räume zurück, sortiert nach Sortierungs ID
     *
     * @return Liste aller Räume
     */
    public List<Room> getRoomsSorted() {
        return super.getData().stream()
                .sorted(Comparator.comparingInt(Room::getOrderId))
                .collect(Collectors.toList());
    }

    /**
     * speichert die Räume in die Datenbank
     */
    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<Room> data = getData();
        for(Room sensorValue : data) {

            pipeline.lpush(DATABASE_KEY, gson.toJson(sensorValue));
        }

        lock.unlock();

        pipeline.sync();
    }
}
