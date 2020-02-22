package net.kleditzsch.applications.network;

import net.kleditzsch.smarthome.application.Application;
import net.kleditzsch.applications.network.view.user.NetworkIndexServlet;
import net.kleditzsch.applications.network.view.user.nasstate.NetworkNasStateServlet;
import net.kleditzsch.applications.network.view.user.overview.NetworkDeviceDeleteServlet;
import net.kleditzsch.applications.network.view.user.overview.NetworkDeviceFormServlet;
import net.kleditzsch.applications.network.view.user.overview.NetworkGroupDeleteServlet;
import net.kleditzsch.applications.network.view.user.overview.NetworkGroupFormServlet;
import net.kleditzsch.applications.network.view.user.printerstate.NetworkPrinterStateServlet;
import net.kleditzsch.smarthome.application.MetaData;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Netzwerkverwaltung
 */
public class NetworkApplication implements Application {

    /**
     * Meta Informationen
     */
    private static MetaData meta = new MetaData(
            "Netzwerk",
            "/network/",
            "/network/index",
            "network.png",
            7
    );

    /**
     * gibt den Eindeutigen Namen der Anwendung zurück
     *
     * @return Eindeutiger Name der Anwendung
     */
    @Override
    public String getApplicationName() {
        return "network";
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


    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(NetworkIndexServlet.class, "/network/index");
        contextHandler.addServlet(NetworkGroupFormServlet.class, "/network/groupform");
        contextHandler.addServlet(NetworkGroupDeleteServlet.class, "/network/groupdelete");
        contextHandler.addServlet(NetworkDeviceFormServlet.class, "/network/deviceform");
        contextHandler.addServlet(NetworkDeviceDeleteServlet.class, "/network/devicedelete");
        contextHandler.addServlet(NetworkPrinterStateServlet.class, "/network/printer");
        contextHandler.addServlet(NetworkNasStateServlet.class, "/network/nas");
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
