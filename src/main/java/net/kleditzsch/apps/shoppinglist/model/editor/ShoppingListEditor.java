package net.kleditzsch.apps.shoppinglist.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.shoppinglist.model.list.Item;
import net.kleditzsch.apps.shoppinglist.model.list.ShoppingList;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Verwaltung der Einkaufslisten
 */
public abstract class ShoppingListEditor {

    public static final String COLLECTION = "shoppinglist.shoppinglist";

    /**
     * gibt eine Liste mit allen Einkaufslisten zurück
     *
     * @return Liste mit allen Einkaufslisten
     */
    public static List<ShoppingList> listShoppingLists() {

        MongoCollection shoppingListCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = shoppingListCollection.find()
                .sort(Sorts.ascending("name"));

        List<ShoppingList> shoppingLists = new ArrayList<>(10);
        for(Document document : iterator) {

            shoppingLists.add(documentToShoppingList(document));
        }
        return shoppingLists;
    }

    /**
     * gibt eine Einkaufsliste zurück
     *
     * @param listId Listen ID
     * @return Einkaufsliste
     */
    public static Optional<ShoppingList> getShoppingListById(ID listId) {

        MongoCollection shoppingListCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = shoppingListCollection.find(new Document().append("_id", listId.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToShoppingList(iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * Speichert eine neue Einkaufsliste in der Datenbank
     *
     * @param shoppingList Einkaufsliste
     * @return ID der neuen Liste
     */
    public static ID addShoppingList(ShoppingList shoppingList) {

        List<Document> items = new ArrayList<>(shoppingList.getItems().size());
        shoppingList.getItems().forEach(item -> {

            Document document = new Document()
                    .append("_id", item.getId().get())
                    .append("name", item.getName())
                    .append("description", item.getDescription().orElse(""))
                    .append("amount", item.getAmount().orElse(""));
            items.add(document);
        });

        //Neue ID vergeben
        shoppingList.setId(ID.create());
        Document document = new Document()
                .append("_id", shoppingList.getId().get())
                .append("name", shoppingList.getName())
                .append("description", shoppingList.getDescription().orElse(""))
                .append("orderId", shoppingList.getOrderId())
                .append("items", items);

        MongoCollection shoppingListCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        shoppingListCollection.insertOne(document);

        return shoppingList.getId();
    }

    /**
     * aktualisiert die Daten einer Einkaufsliste in der Datenbank
     *
     * @param shoppingList Einkaufsliste
     * @return Erfolgsmeldung
     */
    public static boolean updateShoppingList(ShoppingList shoppingList) {

        List<Document> items = new ArrayList<>(shoppingList.getItems().size());
        shoppingList.getItems().forEach(item -> {

            Document document = new Document()
                    .append("_id", item.getId().get())
                    .append("name", item.getName())
                    .append("description", item.getDescription().orElse(""))
                    .append("amount", item.getAmount().orElse(""));
            items.add(document);
        });

        MongoCollection shoppingListCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = shoppingListCollection.updateOne(
                eq("_id", shoppingList.getId().get()),
                combine(
                        set("name", shoppingList.getName()),
                        set("description", shoppingList.getDescription().orElse("")),
                        set("orderId", shoppingList.getOrderId()),
                        set("items", items)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Einkaufsliste aus der Datenbank
     *
     * @param listId Listen ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteShoppingList(ID listId) {

        MongoCollection shoppingListCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(shoppingListCollection.deleteOne(eq("_id", listId.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * importiert die Daten aus dem Dokument in die Objektstruktur
     *
     * @param document Dokument
     * @return Einkaufsliste
     */
    private static ShoppingList documentToShoppingList(Document document) {

        ShoppingList element = new ShoppingList();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));
        element.setOrderId(document.getInteger("orderId"));

        List<Document> items = (List<Document>) document.get("items");
        items.forEach(itemDocument -> {

            Item item = new Item();
            item.setId(ID.of(itemDocument.getString("_id")));
            item.setName(itemDocument.getString("name"));
            item.setDescription(itemDocument.getString("description"));
            item.setAmount(itemDocument.getString("amount"));
            element.getItems().add(item);
        });
        return element;
    }
}
