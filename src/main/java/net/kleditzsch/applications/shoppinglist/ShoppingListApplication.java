package net.kleditzsch.applications.shoppinglist;

import com.mongodb.client.model.CreateCollectionOptions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.application.Application;
import net.kleditzsch.applications.shoppinglist.model.editor.ShoppingItemSuggestionEditor;
import net.kleditzsch.applications.shoppinglist.view.user.*;
import net.kleditzsch.smarthome.application.MetaData;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Einkaufsliste
 */
public class ShoppingListApplication implements Application {

    /**
     * Meta Informationen
     */
    private static MetaData meta = new MetaData(
            "Einkaufsliste",
            "/shoppinglist/",
            "/shoppinglist/index",
            "shoppinglist.png",
            4
    );

    /**
     * gibt den Eindeutigen Namen der Anwendung zurück
     *
     * @return Eindeutiger Name der Anwendung
     */
    @Override
    public String getApplicationName() {
        return "shoppinglist";
    }

    /**
     * gibt die Meta Informationen der Anwendung zurück
     *
     * @return Meta Informationen
     */
    @Override
    public MetaData getMetaData() {
        return meta;
    }

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        //Vorschläge Collection als Chapped Collection anlegen
        if(!SmartHome.getInstance().getDatabaseManager().getCollectionNames().contains(ShoppingItemSuggestionEditor.COLLECTION)) {

            SmartHome.getInstance().getDatabaseManager().getDatabase().createCollection(ShoppingItemSuggestionEditor.COLLECTION, new CreateCollectionOptions().capped(true).maxDocuments(500).sizeInBytes(5 * 1024 * 1024));
        }
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

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
