package net.kleditzsch.SmartHome.app.recipe;

import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.recipe.user.RecipeIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Rezepteverwaltung
 */
public class RecipeApplication implements SubApplication {

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

        contextHandler.addServlet(RecipeIndexServlet.class, "/recipe/");
        contextHandler.addServlet(RecipeIndexServlet.class, "/recipe/index");
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
