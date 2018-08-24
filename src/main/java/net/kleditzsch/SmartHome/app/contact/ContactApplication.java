package net.kleditzsch.SmartHome.app.contact;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.contact.user.ContactIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Kontakteverwaltung
 */
public class ContactApplication implements SubApplication {

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

        contextHandler.addServlet(ContactIndexServlet.class, "/contact/");
        contextHandler.addServlet(ContactIndexServlet.class, "/contact/index");
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
