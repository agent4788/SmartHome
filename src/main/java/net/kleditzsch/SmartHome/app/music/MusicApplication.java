package net.kleditzsch.SmartHome.app.music;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.music.user.MusicIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Musikverwaltung
 */
public class MusicApplication implements SubApplication {

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

        contextHandler.addServlet(MusicIndexServlet.class, "/music/");
        contextHandler.addServlet(MusicIndexServlet.class, "/music/index");
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
