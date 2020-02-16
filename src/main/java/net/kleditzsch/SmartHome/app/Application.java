package net.kleditzsch.SmartHome.app;

import com.google.gson.GsonBuilder;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Standasr Schnittstelle für eine interne Anwendung
 */
public interface Application {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init();

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler);

    /**
     * startet die Anwendung
     */
    public void start();

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump();

    /**
     * Beendet die Anwendung
     */
    public void stop();
}
