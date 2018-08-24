package net.kleditzsch.SmartHome.app.calendar;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.calendar.user.CalendarIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse des Kalenders
 */
public class CalendarApplication implements SubApplication {

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

        contextHandler.addServlet(CalendarIndexServlet.class, "/calendar/");
        contextHandler.addServlet(CalendarIndexServlet.class, "/calendar/index");
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
