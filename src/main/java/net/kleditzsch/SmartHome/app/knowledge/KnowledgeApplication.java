package net.kleditzsch.SmartHome.app.knowledge;

import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.contact.user.ContactIndexServlet;
import net.kleditzsch.SmartHome.view.contact.user.overview.*;
import net.kleditzsch.SmartHome.view.knowledge.user.KnowledgeIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Kontakteverwaltung
 */
public class KnowledgeApplication implements SubApplication {

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
