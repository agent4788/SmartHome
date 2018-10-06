package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
import org.bson.Document;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Medienverwaltung
 */
public class DiscEditor extends AbstractDatabaseEditor<Disc> {

    private static final String COLLECTION = "movie.meta.disc";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static DiscEditor create() {

        return new DiscEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static DiscEditor createAndLoad() {

        DiscEditor editor = new DiscEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection discCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = discCollection.find();

        List<Disc> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            Disc element = new Disc();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.setOrderId(document.getInteger("orderId"));
            element.resetChangedData();

            data.add(element);
        });
    }

    /**
     * erstellt ein neues Medium in der Datenbank
     *
     * @param disc Medium
     * @return ID
     */
    public ID add(Disc disc) {

        //Neue ID vergeben
        disc.setId(ID.create());
        Document document = new Document()
                .append("_id", disc.getId().get())
                .append("name", disc.getName())
                .append("description", disc.getDescription().orElseGet(() -> ""))
                .append("orderId", disc.getOrderId());

        MongoCollection discCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        discCollection.insertOne(document);

        return disc.getId();
    }

    /**
     * aktualisiert die Daten eines Mediums
     *
     * @param disc Medium
     * @return Erfolgsmeldung
     */
    public boolean update(Disc disc) {

        MongoCollection discCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = discCollection.updateOne(
                eq("_id", disc.getId().get()),
                combine(
                        set("name", disc.getName()),
                        set("description", disc.getDescription().orElseGet(() -> "")),
                        set("orderId", disc.getOrderId())
                        )
        );

        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param disc ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Disc disc) {

        MongoCollection discCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if (discCollection.deleteOne(eq("_id", disc.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * gibt eine Liste mit allen Medien sortiert nach Sortierungs ID zurück
     *
     * @return Liste mit allen Medien
     */
    public List<Disc> getDiscsSorted() {

        return super.getData().stream()
                .sorted(Comparator.comparingInt(Disc::getOrderId))
                .collect(Collectors.toList());
    }

    @Override
    public void dump() {
    }
}