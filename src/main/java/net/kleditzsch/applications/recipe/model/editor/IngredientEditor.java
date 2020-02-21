package net.kleditzsch.applications.recipe.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.recipe.model.recipe.Ingredient;
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
 * Zutantenverwaltung
 */
public abstract class IngredientEditor {

    public static final String COLLECTION = "recipe.ingredient";

    /**
     * gibt eine Liste aller Zutaten zurück
     *
     * @return Liste aller Zutaten
     */
    public static List<Ingredient> listIngredients() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"));

        List<Ingredient> ingredients = new ArrayList<>(50);
        for(Document document : iterator) {

            ingredients.add(documentToIngredient(document));
        }
        return ingredients;
    }

    /**
     * gibt eine Liste mit den Zutaten des Bereiches zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Zutaten
     */
    public static List<Ingredient> listIngredients(long start, long length) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<Ingredient> ingredients = new ArrayList<>(50);
        for(Document document : iterator) {

            ingredients.add(documentToIngredient(document));
        }
        return ingredients;
    }

    public static List<Ingredient> listIngredientsByIdList(List<ID> idList) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(
                in("_id", idList.stream().map(ID::toString).collect(Collectors.toList()))
        );

        List<Ingredient> ingredients = new ArrayList<>(50);
        for(Document document : iterator) {

            ingredients.add(documentToIngredient(document));
        }
        return ingredients;
    }

    /**
     * sucht eine Zutat in der Datenbank und gibt dessen Objekt zurück
     *
     * @param id Zutaten ID
     * @return Zutaten Objekt
     */
    public static Optional<Ingredient> getIngredientById(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = collection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToIngredient((Document) iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Aznahl der Zutaten zurück
     *
     * @return Anzahl der Zutaten
     */
    public static long countIngredients() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return collection.countDocuments();
    }

    /**
     * speichert eine neue Zutat in der Datenbank
     *
     * @param ingredient Zutat
     * @return ID der Zutat
     */
    public static ID addIngredient(Ingredient ingredient) {

        ingredient.setId(ID.create());
        Document document = new Document()
                .append("_id", ingredient.getId().get())
                .append("description", ingredient.getDescription().orElse(""))
                .append("name", ingredient.getName())
                .append("unit", ingredient.getUnit());

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        collection.insertOne(document);

        return ingredient.getId();
    }

    /**
     * aktualisiert eine Zutat in der Datenbank
     *
     * @param ingredient Zutat
     * @return Erfolgsmeldung
     */
    public static boolean updateIngredient(Ingredient ingredient) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = collection.updateOne(
                eq("_id", ingredient.getId().get()),
                combine(
                        set("description", ingredient.getDescription().orElse("")),
                        set("name", ingredient.getName()),
                        set("unit", ingredient.getUnit())
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Zuzat aus der Datenbank
     *
     * @param id Zutat ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteIngredient(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(collection.deleteOne(eq("_id", id.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * liest die Daten eines Dokumentes in ein Zutaten Objekt ein
     *
     * @param document Dokument
     * @return Zutat
     */
    private static Ingredient documentToIngredient(Document document) {

        Ingredient element = new Ingredient();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));
        element.setUnit(document.getString("unit"));
        return element;
    }
}
