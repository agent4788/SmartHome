package net.kleditzsch.SmartHome.app.movie;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.view.movie.admin.MovieAdminIndexServlet;
import net.kleditzsch.SmartHome.view.movie.admin.actor.MovieActorDeleteServlet;
import net.kleditzsch.SmartHome.view.movie.admin.actor.MovieActorFormServlet;
import net.kleditzsch.SmartHome.view.movie.admin.actor.MovieActorListServlet;
import net.kleditzsch.SmartHome.view.movie.admin.director.MovieDirectorDeleteServlet;
import net.kleditzsch.SmartHome.view.movie.admin.director.MovieDirectorFormServlet;
import net.kleditzsch.SmartHome.view.movie.admin.director.MovieDirectorListServlet;
import net.kleditzsch.SmartHome.view.movie.admin.disc.MovieDiscDeleteServlet;
import net.kleditzsch.SmartHome.view.movie.admin.disc.MovieDiscFormServlet;
import net.kleditzsch.SmartHome.view.movie.admin.disc.MovieDiscListServlet;
import net.kleditzsch.SmartHome.view.movie.admin.fsk.MovieFskDeleteServlet;
import net.kleditzsch.SmartHome.view.movie.admin.fsk.MovieFskFormServlet;
import net.kleditzsch.SmartHome.view.movie.admin.fsk.MovieFskListServlet;
import net.kleditzsch.SmartHome.view.movie.admin.fsk.MovieFskOrderServlet;
import net.kleditzsch.SmartHome.view.movie.admin.genre.MovieGenreDeleteServlet;
import net.kleditzsch.SmartHome.view.movie.admin.genre.MovieGenreFormServlet;
import net.kleditzsch.SmartHome.view.movie.admin.genre.MovieGenreListServlet;
import net.kleditzsch.SmartHome.view.movie.user.MovieFskImageServlet;
import net.kleditzsch.SmartHome.view.movie.user.MovieIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Filmdatenbank
 */
public class MovieApplication implements SubApplication {

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

        contextHandler.addServlet(MovieIndexServlet.class, "/movie/");
        contextHandler.addServlet(MovieIndexServlet.class, "/movie/index");
        contextHandler.addServlet(MovieAdminIndexServlet.class, "/movie/admin/");
        contextHandler.addServlet(MovieAdminIndexServlet.class, "/movie/admin/index");
        contextHandler.addServlet(MovieActorListServlet.class, "/movie/admin/actor");
        contextHandler.addServlet(MovieActorFormServlet.class, "/movie/admin/actorform");
        contextHandler.addServlet(MovieActorDeleteServlet.class, "/movie/admin/actordelete");
        contextHandler.addServlet(MovieDirectorListServlet.class, "/movie/admin/director");
        contextHandler.addServlet(MovieDirectorFormServlet.class, "/movie/admin/directorform");
        contextHandler.addServlet(MovieDirectorDeleteServlet.class, "/movie/admin/directordelete");
        contextHandler.addServlet(MovieDiscListServlet.class, "/movie/admin/disc");
        contextHandler.addServlet(MovieDiscFormServlet.class, "/movie/admin/discform");
        contextHandler.addServlet(MovieDiscDeleteServlet.class, "/movie/admin/discdelete");
        contextHandler.addServlet(MovieGenreListServlet.class, "/movie/admin/genre");
        contextHandler.addServlet(MovieGenreFormServlet.class, "/movie/admin/genreform");
        contextHandler.addServlet(MovieGenreDeleteServlet.class, "/movie/admin/genredelete");
        contextHandler.addServlet(MovieFskListServlet.class, "/movie/admin/fsk");
        contextHandler.addServlet(MovieFskFormServlet.class, "/movie/admin/fskform");
        contextHandler.addServlet(MovieFskDeleteServlet.class, "/movie/admin/fskdelete");
        contextHandler.addServlet(MovieFskOrderServlet.class, "/movie/admin/fskorder");

        contextHandler.addServlet(MovieFskImageServlet.class, "/movie/fsklogo");
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
