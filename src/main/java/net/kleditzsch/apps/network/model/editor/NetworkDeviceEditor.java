package net.kleditzsch.apps.network.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.network.model.devices.NetworkDevice;
import net.kleditzsch.apps.network.model.devices.NetworkDeviceGroup;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Verwaltung der Netzwerkgeräte
 */
public abstract class NetworkDeviceEditor {

    public static final String COLLECTION = "network.devices";

    /**
     * gibt eine Liste mit allen Gerätegruppen zurück
     *
     * @return Liste mit allen Gerätegruppen
     */
    public static List<NetworkDeviceGroup> listNetworkDeviceGroups() {

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = deviceCollection.find()
                .sort(Sorts.ascending("name"));

        List<NetworkDeviceGroup> deviceGroups = new ArrayList<>(10);
        for(Document document : iterator) {

            deviceGroups.add(documentToNetworkDeviceGroup(document));
        }
        return deviceGroups;
    }

    /**
     * gibt eine Gerätegruppe zurück
     *
     * @param groupId Gruppen ID
     * @return Gerätegruppe
     */
    public static Optional<NetworkDeviceGroup> getNetworkDeviceGroup(ID groupId) {

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = deviceCollection.find(new Document().append("_id", groupId.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToNetworkDeviceGroup(iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * speichert eine neue Gerätegruppe in der Datenbank
     *
     * @param networkDeviceGroup Gerätegruppe
     * @return ID der neuen Gruppe
     */
    public static ID addNetworkDeviceGroup(NetworkDeviceGroup networkDeviceGroup) {

        List<Document> devices = new ArrayList<>(networkDeviceGroup.getDevices().size());
        networkDeviceGroup.getDevices().forEach(device -> {

            Document document = new Document()
                .append("_id", device.getId().get())
                .append("name", device.getName())
                .append("description", device.getDescription().orElse(""))
                .append("ipAddress", device.getIpAddress())
                .append("ain", device.getAin())
                .append("macAddress", device.getMacAddress())
                .append("linkType", device.getLinkType().toString())
                .append("linkSpeed", device.getLinkSpeed())
                .append("hostName", device.getHostName());
            devices.add(document);
        });

        //Neue ID vergeben
        networkDeviceGroup.setId(ID.create());
        Document document = new Document()
                .append("_id", networkDeviceGroup.getId().get())
                .append("name", networkDeviceGroup.getName())
                .append("description", networkDeviceGroup.getDescription().orElse(""))
                .append("devices", devices);

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        deviceCollection.insertOne(document);

        return networkDeviceGroup.getId();
    }

    /**
     * aktualisiert die Daten einer Gerätegruppe
     *
     * @param networkDeviceGroup Gerätegruppe
     * @return Erfolgsmeldung
     */
    public static boolean updateNetworDeviceGroup(NetworkDeviceGroup networkDeviceGroup) {

        List<Document> devices = new ArrayList<>(networkDeviceGroup.getDevices().size());
        networkDeviceGroup.getDevices().forEach(device -> {

            Document document = new Document()
                    .append("_id", device.getId().get())
                    .append("name", device.getName())
                    .append("description", device.getDescription().orElse(""))
                    .append("ipAddress", device.getIpAddress())
                    .append("ain", device.getAin())
                    .append("macAddress", device.getMacAddress())
                    .append("linkType", device.getLinkType().toString())
                    .append("linkSpeed", device.getLinkSpeed())
                    .append("hostName", device.getHostName());
            devices.add(document);
        });

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = deviceCollection.updateOne(
                eq("_id", networkDeviceGroup.getId().get()),
                combine(
                        set("name", networkDeviceGroup.getName()),
                        set("description", networkDeviceGroup.getDescription().orElse("")),
                        set("devices", devices)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Gerätegruppe
     *
     * @param groupId Gruppen ID
     * @return erfolgsmeldung
     */
    public static boolean deleteNetworkDeviceGroup(ID groupId) {

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(deviceCollection.deleteOne(eq("_id", groupId.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * importiert die Daten aus dem Dokument in die Objektstruktur
     *
     * @param document Dokument
     * @return Gerätegruppe
     */
    private static NetworkDeviceGroup documentToNetworkDeviceGroup(Document document) {

        NetworkDeviceGroup element = new NetworkDeviceGroup();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));

        List<Document> devices = (List<Document>) document.get("devices");
        devices.forEach(deviceDocument -> {

            NetworkDevice device = new NetworkDevice();
            device.setId(ID.of(deviceDocument.getString("_id")));
            device.setName(deviceDocument.getString("name"));
            device.setDescription(deviceDocument.getString("description"));
            device.setIpAddress(deviceDocument.getString("ipAddress"));
            device.setAin(deviceDocument.getString("ain"));
            device.setMacAddress(deviceDocument.getString("macAddress"));
            device.setLinkType(NetworkDevice.LinkType.valueOf(deviceDocument.getString("linkType")));
            device.setLinkSpeed(deviceDocument.getString("linkSpeed"));
            device.setHostName(deviceDocument.getString("hostName"));
            element.getDevices().add(device);
        });
        return element;
    }
}
