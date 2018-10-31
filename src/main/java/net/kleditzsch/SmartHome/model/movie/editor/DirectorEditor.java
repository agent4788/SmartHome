package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;
import org.bson.Document;

import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Regiseure Verwaltung
 */
public class DirectorEditor extends AbstractDatabaseEditor<Director> {

    public static final String COLLECTION = "movie.meta.director";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static DirectorEditor create() {

        return new DirectorEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static DirectorEditor createAndLoad() {

        DirectorEditor editor = new DirectorEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection directorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = directorCollection.find();

        List<Director> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            Director element = new Director();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.resetChangedData();

            data.add(element);
        });
    }

    /**
     * erstellt einen neuen Regiseur in der Datenbank
     *
     * @param director Regiseur
     * @return ID
     */
    public ID add(Director director) {

        //Neue ID vergeben
        director.setId(ID.create());
        Document document = new Document()
                .append("_id", director.getId().get())
                .append("name", director.getName())
                .append("description", director.getDescription().orElseGet(() -> ""));

        MongoCollection directorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        directorCollection.insertOne(document);

        return director.getId();
    }

    /**
     * aktualisiert die Daten eines Regiseur
     *
     * @param director Regiseur
     * @return Erfolgsmeldung
     */
    public boolean update(Director director) {

        MongoCollection directorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = directorCollection.updateOne(
                eq("_id", director.getId().get()),
                combine(
                        set("name", director.getName()),
                        set("description", director.getDescription().orElseGet(() -> ""))
                )
        );

        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param director ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Director director) {

        MongoCollection directorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(directorCollection.deleteOne(eq("_id", director.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    @Override
    public void dump() {}
}
