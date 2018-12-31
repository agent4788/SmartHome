package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Person;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Schauspieler Verwaltung
 */
public class PersonEditor extends AbstractDatabaseEditor<Person> {

    public static final String COLLECTION = "movie.meta.person";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static PersonEditor create() {

        return new PersonEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static PersonEditor createAndLoad() {

        PersonEditor editor = new PersonEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = actorCollection.find();

        List<Person> data = getData();
        data.clear();
        for (Document document : iterator) {

            Person element = new Person();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.resetChangedData();

            data.add(element);
        };
    }

    /**
     * erstellt einen neuen Schauspieler in der Datenbank
     *
     * @param person Schauspieler
     * @return ID
     */
    public ID add(Person person) {

        //Neue ID vergeben
        person.setId(ID.create());
        Document document = new Document()
                .append("_id", person.getId().get())
                .append("name", person.getName())
                .append("description", person.getDescription().orElse(""));

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        actorCollection.insertOne(document);

        return person.getId();
    }

    /**
     * aktualisiert die Daten eines Schauspielers
     *
     * @param person Schauspieler
     * @return Erfolgsmeldung
     */
    public boolean update(Person person) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = actorCollection.updateOne(
                eq("_id", person.getId().get()),
                combine(
                        set("name", person.getName()),
                        set("description", person.getDescription().orElse(""))
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param person ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Person person) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(actorCollection.deleteOne(eq("_id", person.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    @Override
    public void dump() {}
}
