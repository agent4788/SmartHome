package net.kleditzsch.SmartHome.app.network;

import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.network.user.*;
import net.kleditzsch.SmartHome.view.network.user.overview.NetworkDeviceDeleteServlet;
import net.kleditzsch.SmartHome.view.network.user.overview.NetworkDeviceFormServlet;
import net.kleditzsch.SmartHome.view.network.user.overview.NetworkGroupDeleteServlet;
import net.kleditzsch.SmartHome.view.network.user.overview.NetworkGroupFormServlet;
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
        contextHandler.addServlet(NetworkGroupFormServlet.class, "/network/groupform");
        contextHandler.addServlet(NetworkGroupDeleteServlet.class, "/network/groupdelete");
        contextHandler.addServlet(NetworkDeviceFormServlet.class, "/network/deviceform");
        contextHandler.addServlet(NetworkDeviceDeleteServlet.class, "/network/devicedelete");
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
