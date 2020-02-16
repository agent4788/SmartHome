package net.kleditzsch.apps.knowledge;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.apps.knowledge.view.KnowledgeIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Kontakteverwaltung
 */
public class KnowledgeApplication implements Application {

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

        contextHandler.addServlet(KnowledgeIndexServlet.class, "/knowledge/");
        contextHandler.addServlet(KnowledgeIndexServlet.class, "/knowledge/index");
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
