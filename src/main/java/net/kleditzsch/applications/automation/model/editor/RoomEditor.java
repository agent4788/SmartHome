package net.kleditzsch.applications.automation.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.database.AbstractDatabaseEditor;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.options.SwitchCommands;
import net.kleditzsch.applications.automation.model.global.SwitchCommand;
import net.kleditzsch.applications.automation.model.room.Interface.RoomElement;
import net.kleditzsch.applications.automation.model.room.Room;
import net.kleditzsch.applications.automation.model.room.element.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class RoomEditor extends AbstractDatabaseEditor<Room> {

    public static final String COLLECTION = "automation.room";

    /**
     * läd die Räume aus der Datenbank
     */
    @Override
    public void load() {

        MongoCollection roomCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = roomCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Room> data = getData();
        data.clear();
        for(Document document : iterator) {

            Room element = new Room();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.setDisplayText(document.getString("displayText"));
            element.setOrderId(document.getInteger("orderId"));
            element.setDisabled(document.getBoolean("disabled"));
            element.setIconFile(document.getString("iconFile"));
            element.setDashboard(document.getBoolean("dashboard", false));
            element.setDefaultDashboard(document.getBoolean("defaultDashboard", false));

            List<Document> elements = (List<Document>) document.get("elements");
            for (Document roomElement : elements) {

                RoomElement.Type type = RoomElement.Type.valueOf(roomElement.getString("type"));
                switch (type) {

                    case BUTTON_ELEMENT:

                        ButtonElement be = new ButtonElement();

                        //Allgemeine Daten
                        be.setId(ID.of(roomElement.getString("_id")));
                        be.setName(roomElement.getString("name"));
                        be.setDescription(roomElement.getString("description"));
                        be.setDisplayText(roomElement.getString("displayText"));
                        be.setOrderId(roomElement.getInteger("orderId"));
                        be.setDisabled(roomElement.getBoolean("disabled"));
                        be.setIconFile(roomElement.getString("iconFile"));

                        //Sicherheitsabfrage
                        be.setSafetyRequestEnabled(roomElement.getBoolean("safetyRequestEnabled"));
                        be.setSafetyRequestIcon(roomElement.getString("safetyRequestIcon"));
                        be.setSafetyRequestHeaderText(roomElement.getString("safetyRequestHeaderText"));
                        be.setSafetyRequestText(roomElement.getString("safetyRequestText"));
                        be.setSafetyRequestExecuteButtonText(roomElement.getString("safetyRequestExecuteButtonText"));
                        be.setSafetyRequestCancelButtonText(roomElement.getString("safetyRequestCancelButtonText"));

                        //sonstiges
                        be.setOnButtonText(roomElement.getString("onButtonText"));
                        be.setOffButtonText(roomElement.getString("offButtonText"));
                        be.setDoubleButton(roomElement.getBoolean("isDoubleButton"));

                        //Schaltbefehle
                        List<Document> commands = (List<Document>) roomElement.get("commands");
                        for(Document command: commands) {

                            be.getCommands().add(new SwitchCommand(ID.of(command.getString("switchableId")), SwitchCommands.valueOf(command.getString("command"))));
                        }

                        element.getRoomElements().add(be);
                        break;
                    case SENSOR_ELEMENT:

                        SensorElement se = new SensorElement();

                        //Allgemeine Daten
                        se.setId(ID.of(roomElement.getString("_id")));
                        se.setName(roomElement.getString("name"));
                        se.setDescription(roomElement.getString("description"));
                        se.setDisplayText(roomElement.getString("displayText"));
                        se.setOrderId(roomElement.getInteger("orderId"));
                        se.setDisabled(roomElement.getBoolean("disabled"));
                        se.setIconFile(roomElement.getString("iconFile"));

                        //Sensor IDs
                        se.setFirstSensorValueId(ID.of(roomElement.getString("firstSensorValueId")));
                        if(roomElement.getString("secondSensorValueId") != null) {

                            se.setSecondSensorValueId(ID.of(roomElement.getString("secondSensorValueId")));
                        }
                        if(roomElement.getString("thirdSensorValueId") != null) {

                            se.setThirdSensorValueId(ID.of(roomElement.getString("thirdSensorValueId")));
                        }

                        element.getRoomElements().add(se);
                        break;
                    case VIRTUAL_SENSOR_ELEMENT:

                        VirtualSensorElement ve = new VirtualSensorElement();

                        //Allgemeine Daten
                        ve.setId(ID.of(roomElement.getString("_id")));
                        ve.setName(roomElement.getString("name"));
                        ve.setDescription(roomElement.getString("description"));
                        ve.setDisplayText(roomElement.getString("displayText"));
                        ve.setOrderId(roomElement.getInteger("orderId"));
                        ve.setDisabled(roomElement.getBoolean("disabled"));
                        ve.setIconFile(roomElement.getString("iconFile"));

                        //ID des Virtuellen Sensors
                        ve.setVirtualSensorId(ID.of(roomElement.getString("virtualSensorId")));

                        element.getRoomElements().add(ve);
                        break;
                    case DIVIDER_ELEMENT:

                        DividerElement de = new DividerElement();

                        //Allgemeine Daten
                        de.setId(ID.of(roomElement.getString("_id")));
                        de.setName(roomElement.getString("name"));
                        de.setDescription(roomElement.getString("description"));
                        de.setDisplayText(roomElement.getString("displayText"));
                        de.setOrderId(roomElement.getInteger("orderId"));
                        de.setDisabled(roomElement.getBoolean("disabled"));
                        de.setIconFile(roomElement.getString("iconFile"));

                        //Darstellung
                        de.setIcon(roomElement.getString("icon"));

                        element.getRoomElements().add(de);
                        break;
                    case SHUTTER_ELEMENT:

                        ShutterElement sue = new ShutterElement();

                        //Allgemeine Daten
                        sue.setId(ID.of(roomElement.getString("_id")));
                        sue.setName(roomElement.getString("name"));
                        sue.setDescription(roomElement.getString("description"));
                        sue.setDisplayText(roomElement.getString("displayText"));
                        sue.setOrderId(roomElement.getInteger("orderId"));
                        sue.setDisabled(roomElement.getBoolean("disabled"));
                        sue.setIconFile(roomElement.getString("iconFile"));

                        //Spezifische Daten
                        List<String> shutterIds = (List<String>) roomElement.get("shutterIds");
                        for(String shutterId: shutterIds) {

                            sue.getShutterIds().add(ID.of(shutterId));
                        }

                        element.getRoomElements().add(sue);
                        break;
                }
            }
            element.resetChangedData();

            data.add(element);
        }

        lock.unlock();
    }

    /**
     * gibt eine Liste mit Räumen zurück
     *
     * @return Liste mit Räumen
     */
    public List<Room> listRooms() {

        return super.getData().stream().filter(e -> !e.isDashboard()).collect(Collectors.toList());
    }

    /**
     * gibt eine Liste aller Räume zurück, sortiert nach Sortierungs ID
     *
     * @return Liste aller Räume
     */
    public List<Room> getRoomsSorted() {
        return listRooms().stream()
                .sorted(Comparator.comparingInt(Room::getOrderId))
                .collect(Collectors.toList());
    }

    /**
     * gibt eine Liste mit Dashboards zurück
     *
     * @return Liste mit Dashboards
     */
    public List<Room> listDashboards() {

        return super.getData().stream().filter(Room::isDashboard).collect(Collectors.toList());
    }

    /**
     * gibt das Standard Dashboard zurück
     *
     * @return Standard Dashboard
     */
    public Optional<Room> getDefaultDashboard() {

        Optional<Room> defaultDashboard = super.getData().stream().filter(Room::isDefaultDashboard).findFirst();
        if(!defaultDashboard.isPresent() || defaultDashboard.get().isDisabled()) {

            List<Room> dashboards = listDashboards().stream().filter(e -> !e.isDisabled()).collect(Collectors.toList());
            if(dashboards.size() > 0) {

                return Optional.of(dashboards.get(0));
            }
        }
        return defaultDashboard;
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param room ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Room room) {

        MongoCollection roomCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(room) && getData().remove(room)) {

            if(roomCollection.deleteOne(eq("_id", room.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    /**
     * speichert die Räume in die Datenbank
     */
    @Override
    public void dump() {

        MongoCollection roomCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<Room> data = getData();
        for(Room room : data) {

            if(room.isChangedData()) {

                //Raumelemente vorbereiten
                List<Document> roomElements = new ArrayList<>(room.getRoomElements().size());
                for (RoomElement roomElement : room.getRoomElements()) {

                    Document roomElementDoc = new Document();
                    roomElementDoc.append("_id", roomElement.getId().get());
                    roomElementDoc.append("name", roomElement.getName());
                    roomElementDoc.append("description", roomElement.getDescription().isPresent() ? roomElement.getDescription().get() : "");
                    roomElementDoc.append("displayText", roomElement.getDisplayText());
                    roomElementDoc.append("orderId", roomElement.getOrderId());
                    roomElementDoc.append("disabled", roomElement.isDisabled());
                    roomElementDoc.append("iconFile", roomElement.getIconFile());
                    roomElementDoc.append("type", roomElement.getType().toString());

                    if (roomElement instanceof ButtonElement) {

                        //Button Element
                        ButtonElement be = (ButtonElement) roomElement;

                        roomElementDoc.append("safetyRequestEnabled", be.isSafetyRequestEnabled());
                        roomElementDoc.append("safetyRequestIcon", be.getSafetyRequestIcon());
                        roomElementDoc.append("safetyRequestHeaderText", be.getSafetyRequestHeaderText());
                        roomElementDoc.append("safetyRequestText", be.getSafetyRequestText());
                        roomElementDoc.append("safetyRequestExecuteButtonText", be.getSafetyRequestExecuteButtonText());
                        roomElementDoc.append("safetyRequestCancelButtonText", be.getSafetyRequestCancelButtonText());

                        roomElementDoc.append("onButtonText", be.getOnButtonText());
                        roomElementDoc.append("offButtonText", be.getOffButtonText());
                        roomElementDoc.append("isDoubleButton", be.isDoubleButton());

                        List<Document> switchCommands = new ArrayList<>(be.getCommands().size());
                        for (SwitchCommand sc : be.getCommands()) {

                            switchCommands.add(new Document().append("switchableId", sc.getSwitchableId().get()).append("command", sc.getCommand().toString()));
                        }
                        roomElementDoc.append("commands", switchCommands);
                    } else if (roomElement instanceof SensorElement) {

                        //Sensor Element
                        SensorElement se = (SensorElement) roomElement;
                        roomElementDoc.append("firstSensorValueId", se.getFirstSensorValueId().isPresent() ? se.getFirstSensorValueId().get().toString() : null);
                        roomElementDoc.append("secondSensorValueId", se.getSecondSensorValueId().isPresent() ? se.getSecondSensorValueId().get().toString() : null);
                        roomElementDoc.append("thirdSensorValueId", se.getThirdSensorValueId().isPresent() ? se.getThirdSensorValueId().get().toString() : null);
                    } else if (roomElement instanceof VirtualSensorElement) {

                        //Virtual Sensor Element
                        VirtualSensorElement ve = (VirtualSensorElement) roomElement;
                        roomElementDoc.append("virtualSensorId", ve.getVirtualSensorId().isPresent() ? ve.getVirtualSensorId().get().toString() : "");
                    } else if (roomElement instanceof DividerElement) {

                        //Divider Element
                        DividerElement de = (DividerElement) roomElement;
                        roomElementDoc.append("icon", de.getIcon().isPresent() ? de.getIcon().get() : "");
                    } else if (roomElement instanceof ShutterElement) {

                        //Shutter Element
                        ShutterElement sue = (ShutterElement) roomElement;
                        List<String> shutterIds = new ArrayList<>(sue.getShutterIds().size());
                        for (ID shutterId : sue.getShutterIds()) {

                            shutterIds.add(shutterId.get());
                        }
                        roomElementDoc.append("shutterIds", shutterIds);
                    }
                    roomElements.add(roomElementDoc);
                }

                roomCollection.updateOne(
                        eq("_id", room.getId().get()),
                        combine(
                                setOnInsert("_id", room.getId().get()),
                                set("name", room.getName()),
                                set("description", room.getDescription().orElseGet(() -> "")),
                                set("displayText", room.getDisplayText()),
                                set("description", room.getDescription().isPresent() ? room.getDescription().get() : ""),
                                set("orderId", room.getOrderId()),
                                set("disabled", room.isDisabled()),
                                set("iconFile", room.getIconFile()),
                                set("dashboard", room.isDashboard()),
                                set("defaultDashboard", room.isDefaultDashboard()),
                                set("elements", roomElements)
                        ),
                        new UpdateOptions().upsert(true)
                );

                room.resetChangedData();
            }
        }

        lock.unlock();
    }
}
