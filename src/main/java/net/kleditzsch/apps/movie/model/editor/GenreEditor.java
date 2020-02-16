package net.kleditzsch.apps.movie.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.database.AbstractDatabaseEditor;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Genre Verwaltung
 */
public class GenreEditor extends AbstractDatabaseEditor<Genre> {

    public static final String COLLECTION = "movie.meta.genre";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static GenreEditor create() {

        return new GenreEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static GenreEditor createAndLoad() {

        GenreEditor editor = new GenreEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection genreCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = genreCollection.find();

        List<Genre> data = getData();
        data.clear();
        for (Document document : iterator) {

            Genre element = new Genre();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.resetChangedData();

            data.add(element);
        };
    }

    /**
     * erstellt ein neues Genre in der Datenbank
     *
     * @param genre Genre
     * @return ID
     */
    public ID add(Genre genre) {

        //Neue ID vergeben
        genre.setId(ID.create());
        Document document = new Document()
                .append("_id", genre.getId().get())
                .append("name", genre.getName())
                .append("description", genre.getDescription().orElseGet(() -> ""));

        MongoCollection actorCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        actorCollection.insertOne(document);

        return genre.getId();
    }

    /**
     * aktualisiert die Daten eines Genres
     *
     * @param genre Genre
     * @return Erfolgsmeldung
     */
    public boolean update(Genre genre) {

        MongoCollection actorCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = actorCollection.updateOne(
                eq("_id", genre.getId().get()),
                combine(
                        set("name", genre.getName()),
                        set("description", genre.getDescription().orElseGet(() -> ""))
                )
        );

        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param genre ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Genre genre) {

        MongoCollection genreCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(genreCollection.deleteOne(eq("_id", genre.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    @Override
    public void dump() {}
}
