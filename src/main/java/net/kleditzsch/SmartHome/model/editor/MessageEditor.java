package net.kleditzsch.SmartHome.model.editor;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.utility.datetime.DatabaseDateTimeUtil;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Meldungen Verwaltung
 */
public abstract class MessageEditor {

    public static final String COLLECTION = "global.message";

    /**
     * gibt eine Liste aller Meldungen zurück
     *
     * @return Liste aller Meldungen
     */
    public static List<Message> listMessages() {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = messageCollection.find()
                .sort(Sorts.ascending("timestamp"));

        List<Message> messages = new ArrayList<>(1000);
        for(Document document : iterator) {

            messages.add(documentToMessage(document));
        }
        return messages;
    }

    /**
     * gibt eine Liste aller Module (die eine Meldung generiert haben) zurück
     *
     * @return Liste der Module
     */
    public static List<String> listModules() {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        DistinctIterable<String> iterator = messageCollection.distinct("module", String.class);

        List<String> modules = new ArrayList<>(250);
        for(String module : iterator) {

            modules.add(module);
        }
        return modules;
    }

    /**
     * gibt eine Liste aller Meldungen eines Modules zurück
     *
     * @param module Modul
     * @return Meldungen des Modules
     */
    public static List<Message> listModuleMessages(String module) {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = messageCollection.find(eq("module", module))
                .sort(Sorts.descending("timestamp"));

        List<Message> messages = new ArrayList<>(250);
        for(Document document : iterator) {

            messages.add(documentToMessage(document));
        }
        return messages;
    }

    /**
     * gibt eine Liste aller Meldungen eines Modules zurück
     *
     * @param module Modul
     * @return Meldungen des Modules
     */
    public static List<Message> listModuleMessages(String module, Message.Type filter) {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = messageCollection.find(
                    and(
                            eq("module", module),
                            eq("type", filter.toString())
                    )
                )
                .sort(Sorts.descending("timestamp"));

        List<Message> messages = new ArrayList<>(250);
        for(Document document : iterator) {

            messages.add(documentToMessage(document));
        }
        return messages;
    }

    /**
     * gibt die Anzahl der Meldungen zurück
     *
     * @return Anzahl der Meldungen
     */
    public static long countMessages() {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return messageCollection.countDocuments();
    }

    /**
     * gibt die Anzahl der Meldungen eines Modules zurück
     *
     * @param module Modul
     * @return Anzahl der Meldungen eines Modules
     */
    public static long countModulMessages(String module) {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return messageCollection.countDocuments(eq("module", module));
    }

    /**
     * speichert eine Meldung in der Datenbank
     *
     * @param message Meldung
     * @return ID die Meldung
     */
    public static ID addMessage(Message message) {

        message.setId(ID.create());

        List<Document> extraInformations = new ArrayList<>();
        message.getExtraInformation().forEach((k, v) -> {

            extraInformations.add(
                    new Document()
                        .append("key", k)
                        .append("value", v)
            );
        });

        Document document = new Document()
                .append("_id", message.getId().get())
                .append("timestamp", message.getTimestamp())
                .append("module", message.getModule())
                .append("type", message.getType().toString())
                .append("acknowledgeRequired", message.isAcknowledgeRequired())
                .append("acknowledgeType", message.getAcknowledgeType().toString())
                .append("acknowledgeApproved", message.isAcknowledgeApproved())
                .append("acknowledgeApprovedTime", message.getAcknowledgeApprovedTime())
                .append("timestamp", message.getTimestamp())
                .append("message", message.getMessage())
                .append("description", message.getDescription())
                .append("extraInformation", extraInformations);

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        messageCollection.insertOne(document);

        return message.getId();
    }

    /**
     * erstellt aus deinem BSON Dokument ein Meldungs Objekt
     *
     * @param document BSON Dokument
     * @return Meldungsobjekt
     */
    private static Message documentToMessage(Document document) {

        Message element = new Message();
        element.setId(ID.of(document.getString("_id")));
        element.setTimestamp(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("timestamp")));
        element.setModule(document.getString("module"));
        element.setType(Message.Type.valueOf(document.getString("type")));
        element.setAcknowledgeRequired(document.getBoolean("acknowledgeRequired"));
        element.setAcknowledgeType(Message.AckType.valueOf(document.getString("acknowledgeType")));
        element.setAcknowledgeApproved(document.getBoolean("acknowledgeApproved"));
        element.setAcknowledgeApprovedTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("acknowledgeApprovedTime")));
        element.setMessage(document.getString("message"));
        element.setDescription(document.getString("description"));

        List<Document> extraInfo = (List<Document>) document.get("extraInformation");
        extraInfo.forEach(doc -> {

            element.getExtraInformation().put(doc.getString("key"), doc.getString("value"));
        });
        return element;
    }
}
