package net.kleditzsch.SmartHome.app.contract;

import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.contract.user.ContractIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Verträgeverwaltung
 */
public class ContractApplication implements SubApplication {

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

        contextHandler.addServlet(ContractIndexServlet.class, "/contract/");
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
