package net.kleditzsch.SmartHome.app.shoppinglist;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.shoppinglist.user.ShoppingListIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Einkaufsliste
 */
public class ShoppingListApplication implements SubApplication {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {


    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(ShoppingListIndexServlet.class, "/shoppinglist/");
        contextHandler.addServlet(ShoppingListIndexServlet.class, "/shoppinglist/index");
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
