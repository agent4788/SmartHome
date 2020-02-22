package net.kleditzsch.smarthome.application;

import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.*;

/**
 * Verwaltung der Applications
 */
public class ApplicationRegistry {

    /**
     * Liste mit den Anwendungen
     */
    private volatile Map<String, Application> applications = new HashMap<>();

    /**
     * registriert eine Anwendung
     *
     * @param application Anwendung
     * @return true wenn Erfolgreich
     */
    public boolean registerApplication(Application application) {

        if(!applications.containsKey(application.getApplicationName())) {

            applications.put(application.getApplicationName(), application);
            return true;
        }
        return false;
    }

    /**
     * entfernt eine Anwendung
     *
     * @param application Anwendung
     * @return true wenn Erfolgreich
     */
    public boolean unregisterApplication(Application application) {

        if(applications.containsKey(application.getApplicationName())) {

            applications.remove(application.getApplicationName());
            return true;
        }
        return false;
    }

    /**
     * gibt eine Liste aller registrierten Anwendungen zurück
     *
     * @return Liste aller registrierten Anwendungen
     */
    public List<Application> listApplications() {

        return new ArrayList<>(applications.values());
    }

    /**
     * gibt ein Anwendungsobjekt zurück
     *
     * @param clazz Typ
     * @param <T>
     * @return Anwendungsobjekt
     */
    public <T>T getApplication(Class<T> clazz) {

        Optional<Application> appOptional = applications.values().stream().filter(clazz::isInstance).findFirst();
        if(appOptional.isPresent()) {

            return (T) appOptional.get();
        }
        throw new IllegalArgumentException("Ungültige Anwendung angefordert");
    }

    /**
     * initalisiert alle registrierten Anwendungen
     */
    public void init() {

        applications.forEach((name, app) -> {

            app.init();
        });
    }

    /**
     * meldet die Webseiten aller registrierten Anwendungen an
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        applications.forEach((name, app) -> {

            app.initWebContext(contextHandler);
        });
    }

    /**
     * startet alle registrierten Anwendungen
     */
    public void start() {

        applications.forEach((name, app) -> {

            app.start();
        });
    }

    /**
     * sichert alle registrierten Anwendungen
     */
    public void dump() {

        applications.forEach((name, app) -> {

            app.dump();
        });
    }

    /**
     * stoppt alle registrierten Anwendungen
     */
    public void stop() {

        applications.forEach((name, app) -> {

            app.stop();
        });
    }
}
