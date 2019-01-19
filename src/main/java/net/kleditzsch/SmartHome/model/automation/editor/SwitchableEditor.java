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
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
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
public class SwitchableEditor extends AbstractDatabaseEditor<Switchable> {

    public static final String COLLECTION = "automation.switchable";

    /**
     * schaltbare Elemente aus der Datenbank laden
     */
    @Override
    public void load() {

        MongoCollection switchableCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = switchableCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Switchable> data = getData();
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
                        case SWITCHABLE_OUTPUT:

                            Output output = new Output();
                            //Element
                            output.setId(ID.of(document.getString("_id")));
                            output.setName(document.getString("name"));
                            output.setDescription(document.getString("description"));
                            //Automation Element
                            output.setDisabled(document.getBoolean("disabled"));
                            //Switchable
                            if(document.getDate("lastToggleTime") != null) {

                                output.setLastToggleTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("lastToggleTime")));
                            }output.setState(AutomationElement.State.valueOf(document.getString("state")));
                            //Double Switchable
                            output.setInverse(document.getBoolean("inverse"));
                            //Ausgang
                            output.setSwitchServerId(ID.of(document.getString("switchServerId")));
                            output.setPin(document.getInteger("pin"));

                            data.add(output);
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
    public List<Switchable> getSublistByType(Class type) {

        return getData().stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param switchable ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Switchable switchable) {

        MongoCollection switchableCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(switchable) && getData().remove(switchable)) {

            if(switchableCollection.deleteOne(eq("_id", switchable.getId().get())).getDeletedCount() == 1) {

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

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<Switchable> data = getData();
        for(Switchable switchable : data) {

            if(switchable.isChangedData()) {

                switch (switchable.getType()) {

                    case SWITCHABLE_AVM_SOCKET:

                        AvmSocket avmSocket = (AvmSocket) switchable;
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
                    case SWITCHABLE_TPLINK_SOCKET:

                        TPlinkSocket tPlinkSocket = (TPlinkSocket) switchable;
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
                    case SWITCHABLE_OUTPUT:

                        Output output = (Output) switchable;
                        switchableCollection.updateOne(
                                eq("_id", output.getId().get()),
                                combine(
                                        //Element
                                        setOnInsert("_id", output.getId().get()),
                                        set("name", output.getName()),
                                        set("description", output.getDescription().orElseGet(() -> "")),
                                        //Automation Element
                                        set("disabled", output.isDisabled()),
                                        //Switchable
                                        set("lastToggleTime", output.getLastToggleTime()),
                                        set("state", output.getState().toString()),
                                        //Double Switchable
                                        set("inverse", output.isInverse()),
                                        //Ausgang
                                        setOnInsert("type", output.getType().toString()),
                                        set("switchServerId", output.getSwitchServerId().get()),
                                        set("pin", output.getPin())
                                ),
                                new UpdateOptions().upsert(true)
                        );
                        break;
                    case SWITCHABLE_SCRIPT_SINGLE:

                        ScriptSingle scriptSingle = (ScriptSingle) switchable;
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

                        ScriptDouble scriptDouble = (ScriptDouble) switchable;
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

                        WakeOnLan wakeOnLan = (WakeOnLan) switchable;
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
                }
                switchable.resetChangedData();
            }
        }

        lock.unlock();
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

        } else if(switchable instanceof AvmSocket) {

            AvmSocket oldSocket = (AvmSocket) switchable;
            AvmSocket newSocket = new AvmSocket();

            //Allgemeine Daten
            newSocket.setId(ID.of(oldSocket.getId().get()));
            newSocket.setDisabled(oldSocket.isDisabled());

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
            newOutput.setDisabled(oldOutput.isDisabled());

            //Spezifische Daten
            newOutput.setSwitchServerId(ID.of(oldOutput.getSwitchServerId().get()));
            newOutput.setPin(oldOutput.getPin());

            return newOutput;

        } else if(switchable instanceof ScriptDouble) {

            ScriptDouble oldScript = (ScriptDouble) switchable;
            ScriptDouble newScript = new ScriptDouble();

            //Allgemeine Daten
            newScript.setId(ID.of(oldScript.getId().get()));
            newScript.setDisabled(oldScript.isDisabled());

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
            newScript.setDisabled(oldScript.isDisabled());

            //Spezifische Daten
            newScript.setWorkingDir(oldScript.getWorkingDir());
            newScript.setCommand(oldScript.getCommand());

            return newScript;

        } else if(switchable instanceof WakeOnLan) {

            WakeOnLan oldWol = (WakeOnLan) switchable;
            WakeOnLan newWol = new WakeOnLan();

            //Allgemeine Daten
            newWol.setId(ID.of(oldWol.getId().get()));
            newWol.setDisabled(oldWol.isDisabled());

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
