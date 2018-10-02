package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.FSK;
import org.bson.Document;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class FskEditor extends AbstractDatabaseEditor<FSK> {

    private static final String COLLECTION = "movie.meta.fsk";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static FskEditor create() {

        return new FskEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static FskEditor createAndLoad() {

        FskEditor editor = new FskEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection fskCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = fskCollection.find();

        List<FSK> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            FSK element = new FSK();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.setLevel(document.getInteger("level"));
            element.setImageFile(document.getString("imageFile"));
            element.resetChangedData();

            data.add(element);
        });
    }

    /**
     * gibt eine Liste aller FSK zurück, sortiert nach Level
     *
     * @return Liste aller Räume
     */
    public List<FSK> getFskSorted() {
        return super.getData().stream()
                .sorted(Comparator.comparingInt(FSK::getLevel))
                .collect(Collectors.toList());
    }

    /**
     * erstellt eine neue Altersfreigabe in der Datenbank
     *
     * @param fsk Altersfreigabe
     * @return ID
     */
    public ID add(FSK fsk) {

        //Neue ID vergeben
        fsk.setId(ID.create());
        Document document = new Document()
                .append("_id", fsk.getId().get())
                .append("name", fsk.getName())
                .append("description", fsk.getDescription().orElseGet(() -> ""))
                .append("level", fsk.getLevel())
                .append("imageFile", fsk.getImageFile());

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        actorCollection.insertOne(document);

        return fsk.getId();
    }

    /**
     * aktualisiert die Daten einer Altersfreigabe
     *
     * @param fsk Altersfreigabe
     * @return Erfolgsmeldung
     */
    public boolean update(FSK fsk) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = actorCollection.updateOne(
                eq("_id", fsk.getId().get()),
                combine(
                        set("name", fsk.getName()),
                        set("description", fsk.getDescription().orElseGet(() -> "")),
                        set("level", fsk.getLevel()),
                        set("imageFile", fsk.getImageFile())
                        )
        );

        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param fsk ELement
     * @return erfolgsmeldung
     */
    public boolean delete(FSK fsk) {

        MongoCollection fskCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(fskCollection.deleteOne(eq("_id", fsk.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * aktualisiert das FSK Level
     *
     * @param id ID des Datensatzes
     * @param newLevel neues Level
     * @return erfolgsmeldung
     */
    public boolean updateLevel(ID id, int newLevel) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = actorCollection.updateOne(
                eq("_id", id.get()),
                combine(
                        set("level", newLevel)
                )
        );

        return result.wasAcknowledged();
    }

    @Override
    public void dump() {}
}
