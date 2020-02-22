package net.kleditzsch.applications.contract;

import net.kleditzsch.smarthome.application.Application;
import net.kleditzsch.applications.contract.view.user.ContractIndexServlet;
import net.kleditzsch.smarthome.application.MetaData;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Verträgeverwaltung
 */
public class ContractApplication implements Application {

    /**
     * Meta Informationen
     */
    private static MetaData meta = new MetaData(
            "Verträge",
            "/contract/",
            "/contract/index",
            "contract.png"
    );

    /**
     * gibt den Eindeutigen Namen der Anwendung zurück
     *
     * @return Eindeutiger Name der Anwendung
     */
    @Override
    public String getApplicationName() {
        return "contract";
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

        contextHandler.addServlet(ContractIndexServlet.class, "/contract/index");
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
