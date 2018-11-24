package net.kleditzsch.SmartHome.app.network;

import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.network.user.NetworkIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Netzwerkverwaltung
 */
public class NetworkApplication implements SubApplication {

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

        contextHandler.addServlet(NetworkIndexServlet.class, "/network/");
        contextHandler.addServlet(NetworkIndexServlet.class, "/network/index");
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
