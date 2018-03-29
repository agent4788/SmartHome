package net.kleditzsch.SmartHome.model.automation.editor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.sensor.*;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.virtual.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Verwaltung Sensoren
 */
public class SensorEditor extends AbstractDatabaseEditor<SensorValue> {

    private static final String DATABASE_KEY = "smarthome:automation:sensor";

    /**
     * läd die Sensoren aus der Datenbank
     */
    @Override
    public void load() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();
        JsonParser jp = new JsonParser();
        List<String> sensorValueList = db.lrange(DATABASE_KEY, 0, -1);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SensorValue> data = getData();
        data.clear();
        for(String sensorValueJson : sensorValueList) {

            JsonObject jo = jp.parse(sensorValueJson).getAsJsonObject();
            Class clazz = getTypeClass(jo.get("type").getAsString());
            SensorValue sensorValue = (SensorValue) gson.fromJson(sensorValueJson, clazz);
            data.add(sensorValue);
        }

        lock.unlock();
    }

    /**
     * sucht nach einem Element mit der übergebenen ID
     *
     * @param identifier Identifizierer
     * @return Element
     */
    public Optional<SensorValue> getById(String identifier) {

        return getData().stream()
                .filter(e -> e.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst();
    }

    /**
     * gibt eine Subliste mit allen Objekten des Typs zurück
     *
     * @param clazz Typ
     * @return Liste mit den Sensorwerten
     */
    public List<SensorValue> filterByType(Class<? extends SensorValue> clazz) {

        return getData().stream()
                .filter(e -> clazz.isInstance(e))
                .collect(Collectors.toList());
    }

    /**
     * speichert die Sensorenn in der Datenbank
     */
    @Override
    public void dump() {

        Jedis db = Application.getInstance().getDatabaseConnection();
        Gson gson = Application.getInstance().getGson();

        Pipeline pipeline = db.pipelined();
        pipeline.del(DATABASE_KEY);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();

        List<SensorValue> data = getData();
        for(SensorValue sensorValue : data) {

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

            case SENSORVALUE_ACTUAL_POWER:

                return ActualPowerValue.class;
            case SENSORVALUE_AIR_PRESSURE:

                return AirPressureValue.class;
            case SENSORVALUE_ALTITUDE:

                return AltitudeValue.class;
            case SENSORVALUE_BATTERY_LEVEL:

                return BatteryLevelValue.class;
            case SENSORVALUE_CURRENT:

                return CurrentValue.class;
            case SENSORVALUE_DISTANCE:

                return DistanceValue.class;
            case SENSORVALUE_DURATION:

                return DurationValue.class;
            case SENSORVALUE_ENERGY:

                return EnergyValue.class;
            case SENSORVALUE_GAS_AMOUNT:

                return GasAmountValue.class;
            case SENSORVALUE_HUMIDITY:

                return HumidityValue.class;
            case SENSORVALUE_INPUT:

                return InputValue.class;
            case SENSORVALUE_LIGHT_INTENSITY:

                return LightIntensityValue.class;
            case SENSORVALUE_LIVE_BIT:

                return LiveBitValue.class;
            case SENSORVALUE_MOISTURE:

                return MoistureValue.class;
            case SENSORVALUE_STRING:

                return StringValue.class;
            case SENSORVALUE_TEMPERATURE:

                return TemperatureValue.class;
            case SENSORVALUE_USER_AT_HOME:

                return UserAtHomeValue.class;
            case SENSORVALUE_VOLTAGE:

                return VoltageValue.class;
            case SENSORVALUE_WATER_AMOUNT:

                return WaterAmountValue.class;
            case VIRTUALSENSORVALUE_ACTUAL_POWER:

                return VirtualActualPowerValue.class;
            case VIRTUALSENSORVALUE_ENERGY:

                return VirtualEnergyValue.class;
            case VIRTUALSENSORVALUE_GAS_AMOUNT:

                return VirtualGasAmountValue.class;
            case VIRTUALSENSORVALUE_LIGHT_INTENSITY:

                return VirtualLightIntensityValue.class;
            case VIRTUALSENSORVALUE_TEMPERATURE:

                return VirtualTemperatureValue.class;
            case VIRTUALSENSORVALUE_WATER_AMOUNT:

                return VirtualWaterAmountValue.class;
            default:
                throw new IllegalStateException("Ungültiger Typ");
        }
    }
}
