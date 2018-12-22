package net.kleditzsch.SmartHome.model.shoppinglist.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import net.kleditzsch.SmartHome.app.Application;
import org.bson.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Verwaltet die Vorschläge für Artikel
 */
public class ShoppingItemSuggestionEditor {

    public static final String COLLECTION = "shoppinglist.suggestion";

    /**
     * Liste mit den Verschlägen (eindeutig)
     */
    private Set<String> suggestions;

    /**
     * @param suggestions Liste der Verschläge
     */
    private ShoppingItemSuggestionEditor(Set<String> suggestions) {

        this.suggestions = suggestions;
    };

    /**
     * Initalisiert den Editor und lädt die erforderlichen Daten aus der Datenbank
     *
     * @return Vorschlagseditor
     */
    public static ShoppingItemSuggestionEditor create() {

        MongoCollection suggestionCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = suggestionCollection.find()
                .sort(Sorts.ascending("name"));

        Set<String> suggestions = new HashSet<>();
        for(Document document : iterator) {

            suggestions.add(document.getString("name"));
        }
        return new ShoppingItemSuggestionEditor(suggestions);
    }

    /**
     * gibt eine Liste mit den Verschlägen zurück
     *
     * @return Liste mit den Verschlägen
     */
    public Set<String> listSuggestions() {

        return Collections.unmodifiableSet(suggestions);
    }

    /**
     * speichert einen neuen Vorschlag (wenn nicht schon vorhanden)
     *
     * @param name Vorschlag
     * @return Erfolgsmeldung
     */
    public boolean addSuggestion(String name) {

        for (String suggestion : suggestions) {

            if(suggestion.equalsIgnoreCase(name)) {

                return false;
            }
        }

        if(suggestions.add(name)) {

            Document doc = new Document();
            doc.append("name", name);
            MongoCollection suggestionCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
            suggestionCollection.insertOne(doc);
            return true;
        }
        return false;
    }
}
