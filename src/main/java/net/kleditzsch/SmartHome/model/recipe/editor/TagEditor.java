package net.kleditzsch.SmartHome.model.recipe.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.recipe.recipe.Tag;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Tag Verwaltung
 */
public abstract class TagEditor {

    public static final String COLLECTION = "recipe.tag";

    /**
     * gibt eine Liste aller Tags zurück
     *
     * @return Liste aller Tags
     */
    public static List<Tag> listTags() {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"));

        List<Tag> tags = new ArrayList<>(50);
        for(Document document : iterator) {

            tags.add(documentToTag(document));
        }
        return tags;
    }

    /**
     * gibt eine Liste mit den Tags des Bereiches zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Tags
     */
    public static List<Tag> listTags(long start, long length) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<Tag> tags = new ArrayList<>(50);
        for(Document document : iterator) {

            tags.add(documentToTag(document));
        }
        return tags;
    }

    /**
     * gibt eine Liste mit den Tags der IDs zurück
     *
     * @param idList Liste mit den IDs
     * @return Liste der Tags
     */
    public static List<Tag> listTagsByIDList(List<ID> idList) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(
                in("_id", idList.stream().map(ID::toString).collect(Collectors.toList()))
        );

        List<Tag> tags = new ArrayList<>(50);
        for(Document document : iterator) {

            tags.add(documentToTag(document));
        }
        return tags;
    }

    /**
     * sucht ein Tag in der Datenbank und gibt dessen Objekt zurück
     *
     * @param id Tag ID
     * @return Tag Objekt
     */
    public static Optional<Tag> getTagById(ID id) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = collection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToTag((Document) iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Aznahl der Tags zurück
     *
     * @return Anzahl der Tags
     */
    public static long countTags() {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        return collection.countDocuments();
    }

    /**
     * speichert ein neues Tag in der Datenbank
     *
     * @param tag Tag
     * @return ID
     */
    public static ID addTag(Tag tag) {

        tag.setId(ID.create());
        Document document = new Document()
                .append("_id", tag.getId().get())
                .append("description", tag.getDescription().orElse(""))
                .append("name", tag.getName())
                .append("color", tag.getColor().toString());

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        collection.insertOne(document);

        return tag.getId();
    }

    /**
     * aktualisiert ein Tag in der Datenbank
     *
     * @param tag Tag
     * @return Erfolgsmeldung
     */
    public static boolean updateTag(Tag tag) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = collection.updateOne(
                eq("_id", tag.getId().get()),
                combine(
                        set("description", tag.getDescription().orElse("")),
                        set("name", tag.getName()),
                        set("color", tag.getColor().toString())
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * Löscht ein Tag
     *
     * @param id Tag ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteTag(ID id) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(collection.deleteOne(eq("_id", id.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * liest die Daten eines Dokumentes in ein Tag Objekt ein
     *
     * @param document Dokument
     * @return Tag
     */
    private static Tag documentToTag(Document document) {

        Tag element = new Tag();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));
        element.setColor(Tag.Color.valueOf(document.getString("color")));
        return element;
    }
}
