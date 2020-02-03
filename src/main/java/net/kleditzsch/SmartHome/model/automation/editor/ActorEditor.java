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
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.shutter.MqttShutter;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.*;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import org.bson.Document;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Verwaltung schaltbare Elemente
 */
public class ActorEditor extends AbstractDatabaseEditor<Actor> {

    public static final String COLLECTION = "automation.actor";

    /**
     * schaltbare Elemente aus der Datenbank laden
     */
    @Override
    public void load() {

        MongoCollection switchableCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = switchableCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Actor> data = getData();
        data.clear();
        for(Document document : iterator) {

                    switch (AutomationElement.Type.valueOf(document.getString("type"))) {

                        case SWITCHABLE_AVM_SOCKET:

                            AvmSocket avmSocket = new AvmSocket();
                            //Element
                            avmSocket.setId(ID.of(document.getString("_id")));
                            avmSocket.setName(document.getString("name"));
                            avmSocket.setDescription(document.getString("description"));
                            //Automation Element
                            avmSocket.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                avmSocket.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }
                            avmSocket.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Double Switchable
                            avmSocket.setInverse(document.getBoolean("inverse"));
                            //AVM Steckdose
                            avmSocket.setIdentifier(document.getString("identifier"));
                            avmSocket.setTempSensorId(ID.of(document.getString("tempSensorId")));
                            avmSocket.setPowerSensorId(ID.of(document.getString("powerSensorId")));
                            avmSocket.setEnergySensorId(ID.of(document.getString("energySensorId")));

                            data.add(avmSocket);

                            break;
                        case SWITCHABLE_MQTT_SINGLE:

                            MqttSingle mqttSingle = new MqttSingle();

                            //Element
                            mqttSingle.setId(ID.of(document.getString("_id")));
                            mqttSingle.setName(document.getString("name"));
                            mqttSingle.setDescription(document.getString("description"));
                            //Automation Element
                            mqttSingle.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                mqttSingle.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }
                            mqttSingle.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //MQTT Single
                            mqttSingle.setMqttName(document.getString("mqttName"));

                            data.add(mqttSingle);

                            break;
                        case SWITCHABLE_MQTT_DOUBLE:

                            MqttDouble mqttDouble = new MqttDouble();

                            //Element
                            mqttDouble.setId(ID.of(document.getString("_id")));
                            mqttDouble.setName(document.getString("name"));
                            mqttDouble.setDescription(document.getString("description"));
                            //Automation Element
                            mqttDouble.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                mqttDouble.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }
                            mqttDouble.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Double Switchable
                            mqttDouble.setInverse(document.getBoolean("inverse"));
                            //MQTT Double
                            mqttDouble.setMqttName(document.getString("mqttName"));

                            data.add(mqttDouble);

                            break;
                        case SWITCHABLE_TPLINK_SOCKET:

                            TPlinkSocket tPlinkSocket = new TPlinkSocket();
                            //Element
                            tPlinkSocket.setId(ID.of(document.getString("_id")));
                            tPlinkSocket.setName(document.getString("name"));
                            tPlinkSocket.setDescription(document.getString("description"));
                            //Automation Element
                            tPlinkSocket.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                tPlinkSocket.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }
                            tPlinkSocket.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Double Switchable
                            tPlinkSocket.setInverse(document.getBoolean("inverse"));
                            //TP-Link Steckdose
                            tPlinkSocket.setIpAddress(document.getString("ipAddress"));
                            tPlinkSocket.setPort(document.getInteger("port"));
                            tPlinkSocket.setSocketType(TPlinkSocket.SOCKET_TYPE.valueOf(document.getString("socketType")));
                            if(document.getString("voltageSensorId") != null) tPlinkSocket.setVoltageSensorId(ID.of(document.getString("voltageSensor")));
                            if(document.getString("currentSensorId") != null) tPlinkSocket.setCurrentSensorId(ID.of(document.getString("currentSensor")));
                            if(document.getString("powerSensorId") != null) tPlinkSocket.setPowerSensorId(ID.of(document.getString("powerSensorId")));
                            if(document.getString("energySensorId") != null) tPlinkSocket.setEnergySensorId(ID.of(document.getString("energySensorId")));

                            data.add(tPlinkSocket);
                            break;
                        case SWITCHABLE_SCRIPT_SINGLE:

                            ScriptSingle scriptSingle = new ScriptSingle();
                            //Element
                            scriptSingle.setId(ID.of(document.getString("_id")));
                            scriptSingle.setName(document.getString("name"));
                            scriptSingle.setDescription(document.getString("description"));
                            //Automation Element
                            scriptSingle.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                scriptSingle.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }scriptSingle.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Einfaches Script
                            scriptSingle.getCommand().addAll((Collection<? extends String>) document.get("command"));
                            scriptSingle.setWorkingDir(document.getString("workingDir"));

                            data.add(scriptSingle);
                            break;
                        case SWITCHABLE_SCRIPT_DOUBLE:

                            ScriptDouble scriptDouble = new ScriptDouble();
                            //Element
                            scriptDouble.setId(ID.of(document.getString("_id")));
                            scriptDouble.setName(document.getString("name"));
                            scriptDouble.setDescription(document.getString("description"));
                            //Automation Element
                            scriptDouble.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                scriptDouble.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }scriptDouble.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Double Switchable
                            scriptDouble.setInverse(document.getBoolean("inverse"));
                            //Doppeltes Script
                            scriptDouble.getOnCommand().addAll((Collection<? extends String>) document.get("onCommand"));
                            scriptDouble.getOffCommand().addAll((Collection<? extends String>) document.get("offCommand"));
                            scriptDouble.setWorkingDir(document.getString("workingDir"));

                            data.add(scriptDouble);
                            break;
                        case SWITCHABLE_WAKE_ON_LAN:

                            WakeOnLan wakeOnLan = new WakeOnLan();
                            //Element
                            wakeOnLan.setId(ID.of(document.getString("_id")));
                            wakeOnLan.setName(document.getString("name"));
                            wakeOnLan.setDescription(document.getString("description"));
                            //Automation Element
                            wakeOnLan.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                wakeOnLan.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }
                            wakeOnLan.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Wake on Lan
                            wakeOnLan.setMac(document.getString("mac"));
                            wakeOnLan.setIpAddress(document.getString("ipAddress"));

                            data.add(wakeOnLan);
                            break;
                        case SHUTTER_MQTT:

                            MqttShutter mqttShutter = new MqttShutter();

                            //Element
                            mqttShutter.setId(ID.of(document.getString("_id")));
                            mqttShutter.setName(document.getString("name"));
                            mqttShutter.setDescription(document.getString("description"));
                            //Automation Element
                            mqttShutter.setDisabled(document.getBoolean("disabled"));
                            //Shutter
                            mqttShutter.setLevel(document.getInteger("level"));
                            if(document.getDate("lastUpdateTime") != null) {

                                mqttShutter.setLastUpdateTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastUpdateTime")));
                            }

                            //MQTT Shutter
                            mqttShutter.setMqttName(document.getString("mqttName"));

                            data.add(mqttShutter);

                            break;
                    }
        };

        lock.unlock();
    }

    /**
     * gibt eine Liste mit den Elementen der IDs zurück (gefiltert nach einem Typ)
     *
     * @param type Typ filtern
     * @return Liste mit Elementen
     */
    public List<Actor> getSublistByType(Class type) {

        return getData().stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * gibt eine Liste mit allen schaltbaren Elementen zurück
     *
     * @return Liste mit allen schaltbaren Elementen
     */
    public List<Switchable> listSwitchables() {

        return getData().stream()
                .filter(e -> e instanceof Switchable)
                .map(e -> (Switchable) e)
                .collect(Collectors.toList());
    }

    /**
     * gibt eine Liste mit allen Rollladen Elementen zurück
     *
     * @return Liste mit allen Rollladen Elementen
     */
    public List<Shutter> listShutters() {

        return getData().stream()
                .filter(e -> e instanceof Shutter)
                .map(e -> (Shutter) e)
                .collect(Collectors.toList());
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param actor ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Actor actor) {

        MongoCollection switchableCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(actor) && getData().remove(actor)) {

            if(switchableCollection.deleteOne(eq("_id", actor.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    /**
     * schaltbare Elemente in die Datenbank speichern
     */
    @Override
    public void dump() {

        MongoCollection switchableCollection = Application.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Actor> data = getData();
        for(Actor actor : data) {

            if(actor.isChangedData()) {

                switch (actor.getType()) {

                    case SWITCHABLE_AVM_SOCKET:

                        AvmSocket avmSocket = (AvmSocket) actor;
                        switchableCollection.updateOne(
                                eq("_id", avmSocket.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", avmSocket.getId().get()),
                                        set("name", avmSocket.getName()),
                                        set("description", avmSocket.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", avmSocket.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", avmSocket.getLastToggleTime()),
                                        set("state", avmSocket.getState().toString()),
                                        //Double Switchable
                                        set("inverse", avmSocket.isInverse()),
                                        //AVM Steckdose
                                        setOnInsert("type", avmSocket.getType().toString()),
                                        set("identifier", avmSocket.getIdentifier()),
                                        set("tempSensorId", avmSocket.getTempSensorId().get()),
                                        set("powerSensorId", avmSocket.getPowerSensorId().get()),
                                        set("energySensorId", avmSocket.getEnergySensorId().get())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_MQTT_SINGLE:

                        MqttSingle mqttSingle = (MqttSingle) actor;
                        switchableCollection.updateOne(
                                eq("_id", mqttSingle.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", mqttSingle.getId().get()),
                                        set("name", mqttSingle.getName()),
                                        set("description", mqttSingle.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", mqttSingle.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", mqttSingle.getLastToggleTime()),
                                        set("state", mqttSingle.getState().toString()),
                                        //MQTT Single
                                        setOnInsert("type", mqttSingle.getType().toString()),
                                        set("mqttName", mqttSingle.getMqttName())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_MQTT_DOUBLE:

                        MqttDouble mqttDouble = (MqttDouble) actor;
                        switchableCollection.updateOne(
                                eq("_id", mqttDouble.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", mqttDouble.getId().get()),
                                        set("name", mqttDouble.getName()),
                                        set("description", mqttDouble.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", mqttDouble.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", mqttDouble.getLastToggleTime()),
                                        set("state", mqttDouble.getState().toString()),
                                        //Double Switchable
                                        set("inverse", mqttDouble.isInverse()),
                                        //MQTT Single
                                        setOnInsert("type", mqttDouble.getType().toString()),
                                        set("mqttName", mqttDouble.getMqttName())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_TPLINK_SOCKET:

                        TPlinkSocket tPlinkSocket = (TPlinkSocket) actor;
                        String voltageSensorId = null;
                        if (tPlinkSocket.getVoltageSensorId().isPresent()) {
                            voltageSensorId = tPlinkSocket.getVoltageSensorId().get().get();
                        }
                        String currentSensorId = null;
                        if (tPlinkSocket.getCurrentSensorId().isPresent()) {
                            currentSensorId = tPlinkSocket.getCurrentSensorId().get().get();
                        }
                        String powerSensorId = null;
                        if (tPlinkSocket.getPowerSensorId().isPresent()) {
                            powerSensorId = tPlinkSocket.getPowerSensorId().get().get();
                        }
                        String energySensorId = null;
                        if (tPlinkSocket.getEnergySensorId().isPresent()) {
                            energySensorId = tPlinkSocket.getEnergySensorId().get().get();
                        }
                        switchableCollection.updateOne(
                                eq("_id", tPlinkSocket.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", tPlinkSocket.getId().get()),
                                        set("name", tPlinkSocket.getName()),
                                        set("description", tPlinkSocket.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", tPlinkSocket.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", tPlinkSocket.getLastToggleTime()),
                                        set("state", tPlinkSocket.getState().toString()),
                                        //Double Switchable
                                        set("inverse", tPlinkSocket.isInverse()),
                                        //TP-Link Steckdose
                                        setOnInsert("type", tPlinkSocket.getType().toString()),
                                        set("ipAddress", tPlinkSocket.getIpAddress()),
                                        set("port", tPlinkSocket.getPort()),
                                        set("socketType", tPlinkSocket.getSocketType().toString()),
                                        set("voltageSensorId", voltageSensorId),
                                        set("currentSensorId", currentSensorId),
                                        set("powerSensorId", powerSensorId),
                                        set("energySensorId", energySensorId)
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_SCRIPT_SINGLE:

                        ScriptSingle scriptSingle = (ScriptSingle) actor;
                        switchableCollection.updateOne(
                                eq("_id", scriptSingle.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", scriptSingle.getId().get()),
                                        set("name", scriptSingle.getName()),
                                        set("description", scriptSingle.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", scriptSingle.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", scriptSingle.getLastToggleTime()),
                                        set("state", scriptSingle.getState().toString()),
                                        //Einfaches Script
                                        setOnInsert("type", scriptSingle.getType().toString()),
                                        set("command", scriptSingle.getCommand()),
                                        set("workingDir", scriptSingle.getWorkingDir())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_SCRIPT_DOUBLE:

                        ScriptDouble scriptDouble = (ScriptDouble) actor;
                        switchableCollection.updateOne(
                                eq("_id", scriptDouble.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", scriptDouble.getId().get()),
                                        set("name", scriptDouble.getName()),
                                        set("description", scriptDouble.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", scriptDouble.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", scriptDouble.getLastToggleTime()),
                                        set("state", scriptDouble.getState().toString()),
                                        //Double Switchable
                                        set("inverse", scriptDouble.isInverse()),
                                        //Ausgang
                                        setOnInsert("type", scriptDouble.getType().toString()),
                                        set("onCommand", scriptDouble.getOnCommand()),
                                        set("offCommand", scriptDouble.getOffCommand()),
                                        set("workingDir", scriptDouble.getWorkingDir())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_WAKE_ON_LAN:

                        WakeOnLan wakeOnLan = (WakeOnLan) actor;
                        switchableCollection.updateOne(
                                eq("_id", wakeOnLan.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", wakeOnLan.getId().get()),
                                        set("name", wakeOnLan.getName()),
                                        set("description", wakeOnLan.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", wakeOnLan.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", wakeOnLan.getLastToggleTime()),
                                        set("state", wakeOnLan.getState().toString()),
                                        //Eingfaches Script
                                        setOnInsert("type", wakeOnLan.getType().toString()),
                                        set("mac", wakeOnLan.getMac()),
                                        set("ipAddress", wakeOnLan.getIpAddress())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SHUTTER_MQTT:

                        MqttShutter mqttShutter = (MqttShutter) actor;
                        switchableCollection.updateOne(
                                eq("_id", mqttShutter.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", mqttShutter.getId().get()),
                                        set("name", mqttShutter.getName()),
                                        set("description", mqttShutter.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", mqttShutter.isDisabled()),
                                        //Level
                                        set("level", mqttShutter.getLevel()),
                                        set("lastUpdateTime", mqttShutter.getLastUpdateTime()),
                                        //MQTT Single
                                        setOnInsert("type", mqttShutter.getType().toString()),
                                        set("mqttName", mqttShutter.getMqttName())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                }
                actor.resetChangedData();
            }
        }

        lock.unlock();
    }

    /**
     * erstellt eine kopie vom Schaltbaren Element
     *
     * @param actor Schaltbares Element
     * @return Kopie des Schaltbaren Elements
     */
    public Actor copyOf(Actor actor) {

        if(actor instanceof TPlinkSocket) {

            TPlinkSocket oldSocket = (TPlinkSocket) actor;
            TPlinkSocket newSocket = new TPlinkSocket();

            //Allgemeine Daten
            newSocket.setId(ID.of(oldSocket.getId().get()));
            newSocket.setDisabled(oldSocket.isDisabled());

            //Spezifische Daten
            newSocket.setIpAddress(oldSocket.getIpAddress());
            newSocket.setPort(oldSocket.getPort());
            newSocket.setSocketType(oldSocket.getSocketType());
            if(oldSocket.getCurrentSensorId().isPresent()) newSocket.setCurrentSensorId(ID.of(oldSocket.getCurrentSensorId().get().get()));
            if(oldSocket.getVoltageSensorId().isPresent()) newSocket.setVoltageSensorId(ID.of(oldSocket.getVoltageSensorId().get().get()));
            if(oldSocket.getEnergySensorId().isPresent()) newSocket.setEnergySensorId(ID.of(oldSocket.getEnergySensorId().get().get()));
            if(oldSocket.getPowerSensorId().isPresent()) newSocket.setPowerSensorId(ID.of(oldSocket.getPowerSensorId().get().get()));

            return newSocket;

        } else if(actor instanceof AvmSocket) {

            AvmSocket oldSocket = (AvmSocket) actor;
            AvmSocket newSocket = new AvmSocket();

            //Allgemeine Daten
            newSocket.setId(ID.of(oldSocket.getId().get()));
            newSocket.setDisabled(oldSocket.isDisabled());

            //Double Switchable
            newSocket.setInverse(oldSocket.isInverse());

            //Spezifische Daten
            newSocket.setIdentifier(oldSocket.getIdentifier());
            newSocket.setTempSensorId(ID.of(oldSocket.getTempSensorId().get()));
            newSocket.setPowerSensorId(ID.of(oldSocket.getPowerSensorId().get()));
            newSocket.setEnergySensorId(ID.of(oldSocket.getEnergySensorId().get()));

            return newSocket;

        } else if(actor instanceof MqttSingle) {

            MqttSingle mqttSingle = (MqttSingle) actor;
            MqttSingle newMqtt = new MqttSingle();

            //Allgemeine Daten
            newMqtt.setId(ID.of(mqttSingle.getId().get()));
            newMqtt.setDisabled(mqttSingle.isDisabled());

            //Spezifische Daten
            newMqtt.setMqttName(mqttSingle.getMqttName());

            return newMqtt;

        } else if(actor instanceof MqttDouble) {

            MqttDouble mqttDouble = (MqttDouble) actor;
            MqttDouble newMqtt = new MqttDouble();

            //Allgemeine Daten
            newMqtt.setId(ID.of(mqttDouble.getId().get()));
            newMqtt.setDisabled(mqttDouble.isDisabled());

            //Double Switchable
            newMqtt.setInverse(mqttDouble.isInverse());

            //Spezifische Daten
            newMqtt.setMqttName(mqttDouble.getMqttName());

            return newMqtt;

        } else if(actor instanceof ScriptDouble) {

            ScriptDouble oldScript = (ScriptDouble) actor;
            ScriptDouble newScript = new ScriptDouble();

            //Allgemeine Daten
            newScript.setId(ID.of(oldScript.getId().get()));
            newScript.setDisabled(oldScript.isDisabled());

            //Double Switchable
            newScript.setInverse(oldScript.isInverse());

            //Spezifische Daten
            newScript.setWorkingDir(oldScript.getWorkingDir());
            newScript.setOnCommand(oldScript.getOnCommand());
            newScript.setOffCommand(oldScript.getOffCommand());

            return newScript;

        } else if(actor instanceof ScriptSingle) {

            ScriptSingle oldScript = (ScriptSingle) actor;
            ScriptSingle newScript = new ScriptSingle();

            //Allgemeine Daten
            newScript.setId(ID.of(oldScript.getId().get()));
            newScript.setDisabled(oldScript.isDisabled());

            //Spezifische Daten
            newScript.setWorkingDir(oldScript.getWorkingDir());
            newScript.setCommand(oldScript.getCommand());

            return newScript;

        } else if(actor instanceof WakeOnLan) {

            WakeOnLan oldWol = (WakeOnLan) actor;
            WakeOnLan newWol = new WakeOnLan();

            //Allgemeine Daten
            newWol.setId(ID.of(oldWol.getId().get()));
            newWol.setDisabled(oldWol.isDisabled());

            //Spezifische Daten
            newWol.setIpAddress(oldWol.getIpAddress());
            newWol.setMac(oldWol.getMac());

            return newWol;
        }else if(actor instanceof MqttShutter) {

            MqttShutter mqttShutter = (MqttShutter) actor;
            MqttShutter newMqtt = new MqttShutter();

            //Allgemeine Daten
            newMqtt.setId(ID.of(mqttShutter.getId().get()));
            newMqtt.setDisabled(mqttShutter.isDisabled());

            //Spezifische Daten
            newMqtt.setMqttName(mqttShutter.getMqttName());
            newMqtt.setLevel(mqttShutter.getLevel());

            return newMqtt;
        }

        LoggerUtil.getLogger(this).severe("Der übergebene Aktor " + actor.getName() + " ist ein nicht bekannter Typ!");
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
            case SWITCHABLE_MQTT_SINGLE:

                return MqttSingle.class;
            case SWITCHABLE_MQTT_DOUBLE:

                return MqttDouble.class;
            case SHUTTER_MQTT:

                return MqttShutter.class;
            case SWITCHABLE_AVM_SOCKET:

                return AvmSocket.class;
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
