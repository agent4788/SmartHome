package net.kleditzsch.smarthome.application;

import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Standasr Schnittstelle für eine interne Anwendung
 */
public interface Application {

    /**
     * gibt den Eindeutigen Namen der Anwendung zurück
     *
     * @return Eindeutiger Name der Anwendung
     */
    String getApplicationName();

    /**
     * gibt die Meta Informationen der Anwendung zurück
     *
     * @return Meta Informationen
     */
    MetaData getMetaData();

    /**
     * Initlisiert die Anwendungsdaten
     */
    void init();

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    void initWebContext(ServletContextHandler contextHandler);

    /**
     * startet die Anwendung
     */
    void start();

    /**
     * Speichert alle Anwendungsdaten
     */
    void dump();

    /**
     * Beendet die Anwendung
     */
    void stop();
}
