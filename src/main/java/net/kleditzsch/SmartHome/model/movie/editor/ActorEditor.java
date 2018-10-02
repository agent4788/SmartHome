package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Schauspieler Verwaltung
 */
public class ActorEditor extends AbstractDatabaseEditor<Actor> {

    private static final String COLLECTION = "movie.meta.actor";

    /**
     * erstell neinen neuen Editor
     *
     * @return Editor
     */
    public static ActorEditor create() {

        return new ActorEditor();
    }

    /**
     * erstell neinen neuen Editor und lädt die Daten aus der Datenbank
     *
     * @return Editor
     */
    public static ActorEditor createAndLoad() {

        ActorEditor editor = new ActorEditor();
        editor.load();
        return editor;
    }

    @Override
    public void load() {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = actorCollection.find();

        List<Actor> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            Actor element = new Actor();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.resetChangedData();

            data.add(element);
        });
    }

    /**
     * erstellt einen neuen Schauspieler in der Datenbank
     *
     * @param actor Schauspieler
     * @return ID
     */
    public ID add(Actor actor) {

        //Neue ID vergeben
        actor.setId(ID.create());
        Document document = new Document()
                .append("_id", actor.getId().get())
                .append("name", actor.getName())
                .append("description", actor.getDescription().orElse(""));

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        actorCollection.insertOne(document);

        return actor.getId();
    }

    /**
     * aktualisiert die Daten eines Schauspielers
     *
     * @param actor Schauspieler
     * @return Erfolgsmeldung
     */
    public boolean update(Actor actor) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = actorCollection.updateOne(
                eq("_id", actor.getId().get()),
                combine(
                        set("name", actor.getName()),
                        set("description", actor.getDescription().orElse(""))
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param actor ELement
     * @return erfolgsmeldung
     */
    public boolean delete(Actor actor) {

        MongoCollection actorCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(actorCollection.deleteOne(eq("_id", actor.getId().get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    @Override
    public void dump() {}
}
