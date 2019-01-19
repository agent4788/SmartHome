package net.kleditzsch.SmartHome.model.automation.editor;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.sensor.*;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.virtual.*;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Verwaltung Sensoren
 */
public class SensorEditor extends AbstractDatabaseEditor<SensorValue> {

    public static final String COLLECTION = "automation.sensor";

    /**
     * läd die Sensoren aus der Datenbank
     */
    @Override
    public void load() {

        MongoCollection sensorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = sensorCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SensorValue> data = getData();
        data.clear();
        for(Document document : iterator) {

                    switch (AutomationElement.Type.valueOf(document.getString("type"))) {

                        case SENSORVALUE_ACTUAL_POWER:

                            ActualPowerValue actualPowerValue = new ActualPowerValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            actualPowerValue.setSystemValue(document.getBoolean("systemValue"));
                            actualPowerValue.setDescription(document.getString("description"));
                            actualPowerValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            actualPowerValue.setActualPower(document.getDouble("actualPower"));

                            actualPowerValue.resetChangedData();
                            data.add(actualPowerValue);
                            break;
                        case SENSORVALUE_AIR_PRESSURE:

                            AirPressureValue airPressureValue = new AirPressureValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            airPressureValue.setSystemValue(document.getBoolean("systemValue"));
                            airPressureValue.setDescription(document.getString("description"));
                            airPressureValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            airPressureValue.setAirPressure(document.getDouble("airPressure"));

                            airPressureValue.resetChangedData();
                            data.add(airPressureValue);
                            break;
                        case SENSORVALUE_ALTITUDE:

                            AltitudeValue altitudeValue = new AltitudeValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            altitudeValue.setSystemValue(document.getBoolean("systemValue"));
                            altitudeValue.setDescription(document.getString("description"));
                            altitudeValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            altitudeValue.setAltitude(document.getDouble("altitude"));

                            altitudeValue.resetChangedData();
                            data.add(altitudeValue);
                            break;
                        case SENSORVALUE_BATTERY_LEVEL:

                            BatteryLevelValue batteryLevelValue = new BatteryLevelValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            batteryLevelValue.setSystemValue(document.getBoolean("systemValue"));
                            batteryLevelValue.setDescription(document.getString("description"));
                            batteryLevelValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            batteryLevelValue.setBatteryLevel(document.getDouble("batteryLevel"));

                            batteryLevelValue.resetChangedData();
                            data.add(batteryLevelValue);
                            break;
                        case SENSORVALUE_BI_STATE:

                            BiStateValue biStateValue = new BiStateValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            biStateValue.setSystemValue(document.getBoolean("systemValue"));
                            biStateValue.setDescription(document.getString("description"));
                            biStateValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            biStateValue.setState(document.getBoolean("state"));
                            biStateValue.setTrueText(document.getString("trueText"));
                            biStateValue.setFalseText(document.getString("falseText"));

                            biStateValue.resetChangedData();
                            data.add(biStateValue);
                            break;
                        case SENSORVALUE_COUNTER:

                            CounterValue counterValue = new CounterValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            counterValue.setSystemValue(document.getBoolean("systemValue"));
                            counterValue.setDescription(document.getString("description"));
                            counterValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            counterValue.setCounterValue(BigInteger.valueOf(document.getLong("counterValue")));

                            counterValue.resetChangedData();
                            data.add(counterValue);
                            break;
                        case SENSORVALUE_CURRENT:

                            CurrentValue currentValue = new CurrentValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            currentValue.setSystemValue(document.getBoolean("systemValue"));
                            currentValue.setDescription(document.getString("description"));
                            currentValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            currentValue.setCurrent(document.getDouble("current"));

                            currentValue.resetChangedData();
                            data.add(currentValue);
                            break;
                        case SENSORVALUE_DISTANCE:

                            DistanceValue distanceValue = new DistanceValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            distanceValue.setSystemValue(document.getBoolean("systemValue"));
                            distanceValue.setDescription(document.getString("description"));
                            distanceValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            distanceValue.setDistance(document.getDouble("distance"));
                            distanceValue.setOffset(document.getDouble("offset"));

                            distanceValue.resetChangedData();
                            data.add(distanceValue);
                            break;
                        case SENSORVALUE_DURATION:

                            DurationValue durationValue = new DurationValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            durationValue.setSystemValue(document.getBoolean("systemValue"));
                            durationValue.setDescription(document.getString("description"));
                            durationValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            durationValue.setDuration(document.getLong("duration"));

                            durationValue.resetChangedData();
                            data.add(durationValue);
                            break;
                        case SENSORVALUE_ENERGY:

                            EnergyValue energyValue = new EnergyValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            energyValue.setSystemValue(document.getBoolean("systemValue"));
                            energyValue.setDescription(document.getString("description"));
                            energyValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            energyValue.setEnergy(document.getDouble("energy"));

                            energyValue.resetChangedData();
                            data.add(energyValue);
                            break;
                        case SENSORVALUE_GAS_AMOUNT:

                            GasAmountValue gasAmountValue = new GasAmountValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            gasAmountValue.setSystemValue(document.getBoolean("systemValue"));
                            gasAmountValue.setDescription(document.getString("description"));
                            gasAmountValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            gasAmountValue.setGasAmount(document.getDouble("gasAmount"));

                            gasAmountValue.resetChangedData();
                            data.add(gasAmountValue);
                            break;
                        case SENSORVALUE_HUMIDITY:

                            HumidityValue humidityValue = new HumidityValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            humidityValue.setSystemValue(document.getBoolean("systemValue"));
                            humidityValue.setDescription(document.getString("description"));
                            humidityValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            humidityValue.setHumidity(document.getDouble("humidity"));

                            humidityValue.resetChangedData();
                            data.add(humidityValue);
                            break;
                        case SENSORVALUE_INPUT:

                            InputValue inputValue = new InputValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            inputValue.setSystemValue(document.getBoolean("systemValue"));
                            inputValue.setDescription(document.getString("description"));
                            inputValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            inputValue.setState(document.getBoolean("state"));

                            inputValue.resetChangedData();
                            data.add(inputValue);
                            break;
                        case SENSORVALUE_LIGHT_INTENSITY:

                            LightIntensityValue lightIntensityValue = new LightIntensityValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            lightIntensityValue.setSystemValue(document.getBoolean("systemValue"));
                            lightIntensityValue.setDescription(document.getString("description"));
                            lightIntensityValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            lightIntensityValue.setLightIntensity(document.getDouble("lightIntensity"));

                            lightIntensityValue.resetChangedData();
                            data.add(lightIntensityValue);
                            break;
                        case SENSORVALUE_LIVE_BIT:

                            LiveBitValue liveBitValue = new LiveBitValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            liveBitValue.setSystemValue(document.getBoolean("systemValue"));
                            liveBitValue.setDescription(document.getString("description"));
                            liveBitValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            liveBitValue.setState(document.getBoolean("state"));
                            liveBitValue.setTimeout(document.getLong("timeout"));
                            liveBitValue.setTrueText(document.getString("trueText"));
                            liveBitValue.setFalseText(document.getString("falseText"));

                            liveBitValue.resetChangedData();
                            data.add(liveBitValue);
                            break;
                        case SENSORVALUE_MOISTURE:

                            MoistureValue moistureValue = new MoistureValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            moistureValue.setSystemValue(document.getBoolean("systemValue"));
                            moistureValue.setDescription(document.getString("description"));
                            moistureValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            moistureValue.setMoisture(document.getDouble("moisture"));

                            moistureValue.resetChangedData();
                            data.add(moistureValue);
                            break;
                        case SENSORVALUE_STRING:

                            StringValue stringValue = new StringValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            stringValue.setSystemValue(document.getBoolean("systemValue"));
                            stringValue.setDescription(document.getString("description"));
                            stringValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            stringValue.setString(document.getString("string"));

                            stringValue.resetChangedData();
                            data.add(stringValue);
                            break;
                        case SENSORVALUE_TEMPERATURE:

                            TemperatureValue temperatureValue = new TemperatureValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            temperatureValue.setSystemValue(document.getBoolean("systemValue"));
                            temperatureValue.setDescription(document.getString("description"));
                            temperatureValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            temperatureValue.setTemperature(document.getDouble("temperature"));
                            temperatureValue.setOffset(document.getDouble("offset"));

                            temperatureValue.resetChangedData();
                            data.add(temperatureValue);
                            break;
                        case SENSORVALUE_USER_AT_HOME:

                            UserAtHomeValue userAtHomeValue = new UserAtHomeValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            userAtHomeValue.setSystemValue(document.getBoolean("systemValue"));
                            userAtHomeValue.setDescription(document.getString("description"));
                            userAtHomeValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            userAtHomeValue.setIpAddress(document.getString("ipAddress"));
                            userAtHomeValue.setAtHome(document.getBoolean("atHome"));
                            userAtHomeValue.setTimeout(document.getInteger("timeout"));
                            userAtHomeValue.setUseExternalDataSource(document.getBoolean("useExternalDataSource"));

                            userAtHomeValue.resetChangedData();
                            data.add(userAtHomeValue);
                            break;
                        case SENSORVALUE_VOLTAGE:

                            VoltageValue voltageValue = new VoltageValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            voltageValue.setSystemValue(document.getBoolean("systemValue"));
                            voltageValue.setDescription(document.getString("description"));
                            voltageValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            voltageValue.setVoltage(document.getDouble("voltage"));

                            voltageValue.resetChangedData();
                            data.add(voltageValue);
                            break;
                        case SENSORVALUE_WATER_AMOUNT:

                            WaterAmountValue waterAmountValue = new WaterAmountValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            waterAmountValue.setSystemValue(document.getBoolean("systemValue"));
                            waterAmountValue.setDescription(document.getString("description"));
                            waterAmountValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            waterAmountValue.setWaterAmount(document.getDouble("waterAmount"));

                            waterAmountValue.resetChangedData();
                            data.add(waterAmountValue);
                            break;
                        case VIRTUALSENSORVALUE_ACTUAL_POWER:

                            VirtualActualPowerValue virtualActualPowerValue = new VirtualActualPowerValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualActualPowerValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualActualPowerValue.setDescription(document.getString("description"));
                            virtualActualPowerValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualActualPowerValue.getSensorValues().clear();
                            virtualActualPowerValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualActualPowerValue);
                            break;
                        case VIRTUALSENSORVALUE_ENERGY:

                            VirtualEnergyValue virtualEnergyValue = new VirtualEnergyValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualEnergyValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualEnergyValue.setDescription(document.getString("description"));
                            virtualEnergyValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualEnergyValue.getSensorValues().clear();
                            virtualEnergyValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualEnergyValue);
                            break;
                        case VIRTUALSENSORVALUE_GAS_AMOUNT:

                            VirtualGasAmountValue virtualGasAmountValue = new VirtualGasAmountValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualGasAmountValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualGasAmountValue.setDescription(document.getString("description"));
                            virtualGasAmountValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualGasAmountValue.getSensorValues().clear();
                            virtualGasAmountValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualGasAmountValue);
                            break;
                        case VIRTUALSENSORVALUE_LIGHT_INTENSITY:

                            VirtualLightIntensityValue virtualLightIntensityValue = new VirtualLightIntensityValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualLightIntensityValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualLightIntensityValue.setDescription(document.getString("description"));
                            virtualLightIntensityValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualLightIntensityValue.getSensorValues().clear();
                            virtualLightIntensityValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualLightIntensityValue);
                            break;
                        case VIRTUALSENSORVALUE_TEMPERATURE:

                            VirtualTemperatureValue virtualTemperatureValue = new VirtualTemperatureValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualTemperatureValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualTemperatureValue.setDescription(document.getString("description"));
                            virtualTemperatureValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualTemperatureValue.getSensorValues().clear();
                            virtualTemperatureValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualTemperatureValue);
                            break;
                        case VIRTUALSENSORVALUE_WATER_AMOUNT:

                            VirtualWaterAmountValue virtualWaterAmountValue = new VirtualWaterAmountValue(
                                    ID.of(document.getString("_id")),
                                    document.getString("identifier"),
                                    document.getString("name")
                            );
                            virtualWaterAmountValue.setSystemValue(document.getBoolean("systemValue"));
                            virtualWaterAmountValue.setDescription(document.getString("description"));
                            virtualWaterAmountValue.setLastPushTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastPushTime")));

                            virtualWaterAmountValue.getSensorValues().clear();
                            virtualWaterAmountValue.getSensorValues().addAll((Collection<? extends String>) document.get("sensorValues"));
                            data.add(virtualWaterAmountValue);
                            break;
                    }
        };

        lock.unlock();
    }

    /**
     * sucht nach einem Element mit der übergebenen ID
     *
     * @param identifier Identifizierer
     * @return Element
     */
    public Optional<SensorValue> getByIdentifier(String identifier) {

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
     * löscht ein Element aus dem Datenbestand
     *
     * @param sensorValue ELement
     * @return erfolgsmeldung
     */
    public boolean delete(SensorValue sensorValue) {

        MongoCollection sensorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(sensorValue) && getData().remove(sensorValue)) {

            if(sensorCollection.deleteOne(eq("_id", sensorValue.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    /**
     * speichert die Sensorenn in der Datenbank
     */
    @Override
    public void dump() {

        MongoCollection sensorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SensorValue> data = getData();
        for(SensorValue sensorValue : data) {

            if (sensorValue.isChangedData()) {

                switch (sensorValue.getType()) {

                    case SENSORVALUE_ACTUAL_POWER:

                        ActualPowerValue actualPowerValue = (ActualPowerValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", actualPowerValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", actualPowerValue.getId().get()),
                                        set("name", actualPowerValue.getName()),
                                        set("description", actualPowerValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", actualPowerValue.getIdentifier()),
                                        set("systemValue", actualPowerValue.isSystemValue()),
                                        set("lastPushTime", actualPowerValue.getLastPushTime()),
                                        //Sensorwert
                                        set("actualPower", actualPowerValue.getActualPower()),
                                        setOnInsert("type", actualPowerValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_AIR_PRESSURE:

                        AirPressureValue airPressureValue = (AirPressureValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", airPressureValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", airPressureValue.getId().get()),
                                        set("name", airPressureValue.getName()),
                                        set("description", airPressureValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", airPressureValue.getIdentifier()),
                                        set("systemValue", airPressureValue.isSystemValue()),
                                        set("lastPushTime", airPressureValue.getLastPushTime()),
                                        //Sensorwert
                                        set("airPressure", airPressureValue.getAirPressure()),
                                        setOnInsert("type", airPressureValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_ALTITUDE:

                        AltitudeValue altitudeValue = (AltitudeValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", altitudeValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", altitudeValue.getId().get()),
                                        set("name", altitudeValue.getName()),
                                        set("description", altitudeValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", altitudeValue.getIdentifier()),
                                        set("systemValue", altitudeValue.isSystemValue()),
                                        set("lastPushTime", altitudeValue.getLastPushTime()),
                                        //Sensorwert
                                        set("altitude", altitudeValue.getAltitude()),
                                        setOnInsert("type", altitudeValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_BATTERY_LEVEL:

                        BatteryLevelValue batteryLevelValue = (BatteryLevelValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", batteryLevelValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", batteryLevelValue.getId().get()),
                                        set("name", batteryLevelValue.getName()),
                                        set("description", batteryLevelValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", batteryLevelValue.getIdentifier()),
                                        set("systemValue", batteryLevelValue.isSystemValue()),
                                        set("lastPushTime", batteryLevelValue.getLastPushTime()),
                                        //Sensorwert
                                        set("batteryLevel", batteryLevelValue.getBatteryLevel()),
                                        setOnInsert("type", batteryLevelValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_BI_STATE:

                        BiStateValue biStateValue = (BiStateValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", biStateValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", biStateValue.getId().get()),
                                        set("name", biStateValue.getName()),
                                        set("description", biStateValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", biStateValue.getIdentifier()),
                                        set("systemValue", biStateValue.isSystemValue()),
                                        set("lastPushTime", biStateValue.getLastPushTime()),
                                        //Sensorwert
                                        set("state", biStateValue.getState()),
                                        set("trueText", biStateValue.getTrueText()),
                                        set("falseText", biStateValue.getFalseText()),
                                        setOnInsert("type", biStateValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_COUNTER:

                        CounterValue counterValue = (CounterValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", counterValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", counterValue.getId().get()),
                                        set("name", counterValue.getName()),
                                        set("description", counterValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", counterValue.getIdentifier()),
                                        set("systemValue", counterValue.isSystemValue()),
                                        set("lastPushTime", counterValue.getLastPushTime()),
                                        //Sensorwert
                                        set("counterValue", counterValue.getCounterValue().longValue()),
                                        setOnInsert("type", counterValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_CURRENT:

                        CurrentValue currentValue = (CurrentValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", currentValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", currentValue.getId().get()),
                                        set("name", currentValue.getName()),
                                        set("description", currentValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", currentValue.getIdentifier()),
                                        set("systemValue", currentValue.isSystemValue()),
                                        set("lastPushTime", currentValue.getLastPushTime()),
                                        //Sensorwert
                                        set("current", currentValue.getCurrent()),
                                        setOnInsert("type", currentValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_DISTANCE:

                        DistanceValue distanceValue = (DistanceValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", distanceValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", distanceValue.getId().get()),
                                        set("name", distanceValue.getName()),
                                        set("description", distanceValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", distanceValue.getIdentifier()),
                                        set("systemValue", distanceValue.isSystemValue()),
                                        set("lastPushTime", distanceValue.getLastPushTime()),
                                        //Sensorwert
                                        set("distance", distanceValue.getDistance()),
                                        set("offset", distanceValue.getOffset()),
                                        setOnInsert("type", distanceValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_DURATION:

                        DurationValue durationValue = (DurationValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", durationValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", durationValue.getId().get()),
                                        set("name", durationValue.getName()),
                                        set("description", durationValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", durationValue.getIdentifier()),
                                        set("systemValue", durationValue.isSystemValue()),
                                        set("lastPushTime", durationValue.getLastPushTime()),
                                        //Sensorwert
                                        set("duration", durationValue.getDuration()),
                                        setOnInsert("type", durationValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_ENERGY:

                        EnergyValue energyValue = (EnergyValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", energyValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", energyValue.getId().get()),
                                        set("name", energyValue.getName()),
                                        set("description", energyValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", energyValue.getIdentifier()),
                                        set("systemValue", energyValue.isSystemValue()),
                                        set("lastPushTime", energyValue.getLastPushTime()),
                                        //Sensorwert
                                        set("energy", energyValue.getEnergy()),
                                        setOnInsert("type", energyValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_GAS_AMOUNT:

                        GasAmountValue gasAmountValue = (GasAmountValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", gasAmountValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", gasAmountValue.getId().get()),
                                        set("name", gasAmountValue.getName()),
                                        set("description", gasAmountValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", gasAmountValue.getIdentifier()),
                                        set("systemValue", gasAmountValue.isSystemValue()),
                                        set("lastPushTime", gasAmountValue.getLastPushTime()),
                                        //Sensorwert
                                        set("gasAmount", gasAmountValue.getGasAmount()),
                                        setOnInsert("type", gasAmountValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_HUMIDITY:

                        HumidityValue humidityValue = (HumidityValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", humidityValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", humidityValue.getId().get()),
                                        set("name", humidityValue.getName()),
                                        set("description", humidityValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", humidityValue.getIdentifier()),
                                        set("systemValue", humidityValue.isSystemValue()),
                                        set("lastPushTime", humidityValue.getLastPushTime()),
                                        //Sensorwert
                                        set("humidity", humidityValue.getHumidity()),
                                        setOnInsert("type", humidityValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_INPUT:

                        InputValue inputValue = (InputValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", inputValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", inputValue.getId().get()),
                                        set("name", inputValue.getName()),
                                        set("description", inputValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", inputValue.getIdentifier()),
                                        set("systemValue", inputValue.isSystemValue()),
                                        set("lastPushTime", inputValue.getLastPushTime()),
                                        //Sensorwert
                                        set("state", inputValue.getState()),
                                        setOnInsert("type", inputValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_LIGHT_INTENSITY:

                        LightIntensityValue lightIntensityValue = (LightIntensityValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", lightIntensityValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", lightIntensityValue.getId().get()),
                                        set("name", lightIntensityValue.getName()),
                                        set("description", lightIntensityValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", lightIntensityValue.getIdentifier()),
                                        set("systemValue", lightIntensityValue.isSystemValue()),
                                        set("lastPushTime", lightIntensityValue.getLastPushTime()),
                                        //Sensorwert
                                        set("lightIntensity", lightIntensityValue.getLightIntensity()),
                                        setOnInsert("type", lightIntensityValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_LIVE_BIT:

                        LiveBitValue liveBitValue = (LiveBitValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", liveBitValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", liveBitValue.getId().get()),
                                        set("name", liveBitValue.getName()),
                                        set("description", liveBitValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", liveBitValue.getIdentifier()),
                                        set("systemValue", liveBitValue.isSystemValue()),
                                        set("lastPushTime", liveBitValue.getLastPushTime()),
                                        //Sensorwert
                                        set("state", liveBitValue.getState()),
                                        set("timeout", liveBitValue.getTimeout()),
                                        set("trueText", liveBitValue.getTrueText()),
                                        set("falseText", liveBitValue.getFalseText()),
                                        setOnInsert("type", liveBitValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_MOISTURE:

                        MoistureValue moistureValue = (MoistureValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", moistureValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", moistureValue.getId().get()),
                                        set("name", moistureValue.getName()),
                                        set("description", moistureValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", moistureValue.getIdentifier()),
                                        set("systemValue", moistureValue.isSystemValue()),
                                        set("lastPushTime", moistureValue.getLastPushTime()),
                                        //Sensorwert
                                        set("moisture", moistureValue.getMoisture()),
                                        setOnInsert("type", moistureValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_STRING:

                        StringValue stringValue = (StringValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", stringValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", stringValue.getId().get()),
                                        set("name", stringValue.getName()),
                                        set("description", stringValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", stringValue.getIdentifier()),
                                        set("systemValue", stringValue.isSystemValue()),
                                        set("lastPushTime", stringValue.getLastPushTime()),
                                        //Sensorwert
                                        set("string", stringValue.getString()),
                                        setOnInsert("type", stringValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_TEMPERATURE:

                        TemperatureValue temperatureValue = (TemperatureValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", temperatureValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", temperatureValue.getId().get()),
                                        set("name", temperatureValue.getName()),
                                        set("description", temperatureValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", temperatureValue.getIdentifier()),
                                        set("systemValue", temperatureValue.isSystemValue()),
                                        set("lastPushTime", temperatureValue.getLastPushTime()),
                                        //Sensorwert
                                        set("temperature", temperatureValue.getTemperature()),
                                        set("offset", temperatureValue.getOffset()),
                                        setOnInsert("type", temperatureValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_USER_AT_HOME:

                        UserAtHomeValue userAtHomeValue = (UserAtHomeValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", userAtHomeValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", userAtHomeValue.getId().get()),
                                        set("name", userAtHomeValue.getName()),
                                        set("description", userAtHomeValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        set("identifier", userAtHomeValue.getIdentifier()),
                                        set("systemValue", userAtHomeValue.isSystemValue()),
                                        set("lastPushTime", userAtHomeValue.getLastPushTime()),
                                        //Sensorwert
                                        set("ipAddress", userAtHomeValue.getIpAddress()),
                                        set("atHome", userAtHomeValue.isAtHome()),
                                        set("timeout", userAtHomeValue.getTimeout()),
                                        set("useExternalDataSource", userAtHomeValue.isUseExternalDataSource()),
                                        setOnInsert("type", userAtHomeValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_VOLTAGE:

                        VoltageValue voltageValue = (VoltageValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", voltageValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", voltageValue.getId().get()),
                                        set("name", voltageValue.getName()),
                                        set("description", voltageValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", voltageValue.getIdentifier()),
                                        set("systemValue", voltageValue.isSystemValue()),
                                        set("lastPushTime", voltageValue.getLastPushTime()),
                                        //Sensorwert
                                        set("voltage", voltageValue.getVoltage()),
                                        setOnInsert("type", voltageValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SENSORVALUE_WATER_AMOUNT:

                        WaterAmountValue waterAmountValue = (WaterAmountValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", waterAmountValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", waterAmountValue.getId().get()),
                                        set("name", waterAmountValue.getName()),
                                        set("description", waterAmountValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", waterAmountValue.getIdentifier()),
                                        set("systemValue", waterAmountValue.isSystemValue()),
                                        set("lastPushTime", waterAmountValue.getLastPushTime()),
                                        //Sensorwert
                                        set("waterAmount", waterAmountValue.getWaterAmount()),
                                        setOnInsert("type", waterAmountValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_ACTUAL_POWER:

                        VirtualActualPowerValue virtualActualPowerValue = (VirtualActualPowerValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualActualPowerValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualActualPowerValue.getId().get()),
                                        set("name", virtualActualPowerValue.getName()),
                                        set("description", virtualActualPowerValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualActualPowerValue.getIdentifier()),
                                        set("systemValue", virtualActualPowerValue.isSystemValue()),
                                        set("lastPushTime", virtualActualPowerValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualActualPowerValue.getSensorValues()),
                                        setOnInsert("type", virtualActualPowerValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_ENERGY:

                        VirtualEnergyValue virtualEnergyValue = (VirtualEnergyValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualEnergyValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualEnergyValue.getId().get()),
                                        set("name", virtualEnergyValue.getName()),
                                        set("description", virtualEnergyValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualEnergyValue.getIdentifier()),
                                        set("systemValue", virtualEnergyValue.isSystemValue()),
                                        set("lastPushTime", virtualEnergyValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualEnergyValue.getSensorValues()),
                                        setOnInsert("type", virtualEnergyValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_GAS_AMOUNT:

                        VirtualGasAmountValue virtualGasAmountValue = (VirtualGasAmountValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualGasAmountValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualGasAmountValue.getId().get()),
                                        set("name", virtualGasAmountValue.getName()),
                                        set("description", virtualGasAmountValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualGasAmountValue.getIdentifier()),
                                        set("systemValue", virtualGasAmountValue.isSystemValue()),
                                        set("lastPushTime", virtualGasAmountValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualGasAmountValue.getSensorValues()),
                                        setOnInsert("type", virtualGasAmountValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_LIGHT_INTENSITY:

                        VirtualLightIntensityValue virtualLightIntensityValue = (VirtualLightIntensityValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualLightIntensityValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualLightIntensityValue.getId().get()),
                                        set("name", virtualLightIntensityValue.getName()),
                                        set("description", virtualLightIntensityValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualLightIntensityValue.getIdentifier()),
                                        set("systemValue", virtualLightIntensityValue.isSystemValue()),
                                        set("lastPushTime", virtualLightIntensityValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualLightIntensityValue.getSensorValues()),
                                        setOnInsert("type", virtualLightIntensityValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_TEMPERATURE:

                        VirtualTemperatureValue virtualTemperatureValue = (VirtualTemperatureValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualTemperatureValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualTemperatureValue.getId().get()),
                                        set("name", virtualTemperatureValue.getName()),
                                        set("description", virtualTemperatureValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualTemperatureValue.getIdentifier()),
                                        set("systemValue", virtualTemperatureValue.isSystemValue()),
                                        set("lastPushTime", virtualTemperatureValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualTemperatureValue.getSensorValues()),
                                        setOnInsert("type", virtualTemperatureValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case VIRTUALSENSORVALUE_WATER_AMOUNT:

                        VirtualWaterAmountValue virtualWaterAmountValue = (VirtualWaterAmountValue) sensorValue;
                        sensorCollection.updateOne(
                                eq("_id", virtualWaterAmountValue.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", virtualWaterAmountValue.getId().get()),
                                        set("name", virtualWaterAmountValue.getName()),
                                        set("description", virtualWaterAmountValue.getDescription().orElseGet(() -> "")),
                                        //Sensor Value
                                        setOnInsert("identifier", virtualWaterAmountValue.getIdentifier()),
                                        set("systemValue", virtualWaterAmountValue.isSystemValue()),
                                        set("lastPushTime", virtualWaterAmountValue.getLastPushTime()),
                                        //Sensorwert
                                        set("sensorValues", virtualWaterAmountValue.getSensorValues()),
                                        setOnInsert("type", virtualWaterAmountValue.getType().toString())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                }
                sensorValue.resetChangedData();
            }
        }

        lock.unlock();
    }

    /**
     * erstellt eine kopie vom Sensorwert
     *
     * @param sensorValue Sensorwert
     * @return Kopie des Sensorwertes
     */
    public SensorValue copyOf(SensorValue sensorValue) {

        if(sensorValue instanceof LiveBitValue) {

            LiveBitValue oldSensorValue = (LiveBitValue) sensorValue;
            LiveBitValue newSensorValue = new LiveBitValue(ID.of(oldSensorValue.getId().get()), oldSensorValue.getIdentifier(), oldSensorValue.getName());
            newSensorValue.setDisabled(oldSensorValue.isDisabled());
            newSensorValue.setLastPushTime(oldSensorValue.getLastPushTime());
            newSensorValue.setTimeout(oldSensorValue.getTimeout());
            newSensorValue.setState(oldSensorValue.getState());

            return newSensorValue;
        } else if (sensorValue instanceof UserAtHomeValue) {

            UserAtHomeValue oldSensorValue = (UserAtHomeValue) sensorValue;
            UserAtHomeValue newSensorValue = new UserAtHomeValue(ID.of(oldSensorValue.getId().get()), oldSensorValue.getIdentifier(), oldSensorValue.getName());
            newSensorValue.setDisabled(oldSensorValue.isDisabled());
            newSensorValue.setLastPushTime(oldSensorValue.getLastPushTime());
            newSensorValue.setTimeout(oldSensorValue.getTimeout());
            newSensorValue.setIpAddress(oldSensorValue.getIpAddress());
            newSensorValue.setUseExternalDataSource(oldSensorValue.isUseExternalDataSource());
            newSensorValue.setAtHome(oldSensorValue.isAtHome());

            return newSensorValue;
        }
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
            case SENSORVALUE_BI_STATE:

                return BiStateValue.class;
            case SENSORVALUE_COUNTER:

                return CounterValue.class;
            default:
                throw new IllegalStateException("Ungültiger Typ");
        }
    }
}
