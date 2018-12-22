package net.kleditzsch.SmartHome.app.shoppinglist;

import com.mongodb.client.model.CreateCollectionOptions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingItemSuggestionEditor;
import net.kleditzsch.SmartHome.view.shoppinglist.user.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Einkaufsliste
 */
public class ShoppingListApplication implements SubApplication {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        //Vorschläge Collection als Chapped Collection anlegen
        if(!Application.getInstance().getDatabaseManager().getCollectionNames().contains(ShoppingItemSuggestionEditor.COLLECTION)) {

            Application.getInstance().getDatabaseManager().getDatabase().createCollection(ShoppingItemSuggestionEditor.COLLECTION, new CreateCollectionOptions().capped(true).maxDocuments(500).sizeInBytes(5 * 1024 * 1024));
        }
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(ShoppingListIndexServlet.class, "/shoppinglist/");
        contextHandler.addServlet(ShoppingListIndexServlet.class, "/shoppinglist/index");
        contextHandler.addServlet(ShoppingListAddListServlet.class, "/shoppinglist/addlist");
        contextHandler.addServlet(ShoppingListOrderListServlet.class, "/shoppinglist/listorder");
        contextHandler.addServlet(ShoppingListDeleteListServlet.class, "/shoppinglist/deletelist");
        contextHandler.addServlet(ShoppingListClearListServlet.class, "/shoppinglist/clearlist");
        contextHandler.addServlet(ShoppingListClearListItemsServlet.class, "/shoppinglist/clearlistitems");
        contextHandler.addServlet(ShoppingListPrintServlet.class, "/shoppinglist/printlist");
        contextHandler.addServlet(ShoppingListAddItemServlet.class, "/shoppinglist/additem");
        contextHandler.addServlet(ShoppingListDeleteItemServlet.class, "/shoppinglist/deleteitem");
    }

    /**
     * startet die Anwendung
     */
    public void start() {

    }

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump() {


    }

    /**
     * Beendet die Anwendung
     */
    public void stop() {


    }
}
