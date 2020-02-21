package net.kleditzsch.apps.movie.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
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

    public static final String COLLECTION = "movie.meta.disc";

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

        MongoCollection discCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = discCollection.find();

        List<Disc> data = getData();
        data.clear();
        for(Document document :iterator) {

            Disc element = new Disc();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.setQuality(Disc.Quality.valueOf(document.getString("quality")));
            element.setOrderId(document.getInteger("orderId"));
            element.resetChangedData();

            data.add(element);
        };
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
                .append("quality", disc.getQuality().toString())
                .append("orderId", disc.getOrderId());

        MongoCollection discCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
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

        MongoCollection discCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = discCollection.updateOne(
                eq("_id", disc.getId().get()),
                combine(
                        set("name", disc.getName()),
                        set("description", disc.getDescription().orElseGet(() -> "")),
                        set("quality", disc.getQuality().toString()),
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

        MongoCollection discCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
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