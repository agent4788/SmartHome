package net.kleditzsch.SmartHome.app.movie;

import com.google.gson.GsonBuilder;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.editor.FskEditor;
import net.kleditzsch.SmartHome.model.movie.editor.GenreEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
import net.kleditzsch.SmartHome.model.movie.movie.meta.FSK;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Genre;
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
import net.kleditzsch.SmartHome.view.movie.user.MovieCoverImageServlet;
import net.kleditzsch.SmartHome.view.movie.user.MovieFskImageServlet;
import net.kleditzsch.SmartHome.view.movie.user.MovieIndexServlet;
import net.kleditzsch.SmartHome.view.movie.user.movie.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.time.LocalDate;

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
        contextHandler.addServlet(MovieCoverImageServlet.class, "/movie/cover");
        contextHandler.addServlet(MovieMovieListServlet.class, "/movie/movie");
        contextHandler.addServlet(MovieMovieFormServlet.class, "/movie/movieform");
        contextHandler.addServlet(MovieMovieViewServlet.class, "/movie/movieview");
        contextHandler.addServlet(MovieMovieDeleteServlet.class, "/movie/moviedelete");
        contextHandler.addServlet(MovieUpdateRatingServlet.class, "/movie/updaterating");
        contextHandler.addServlet(MovieUpdateViewSoonServlet.class, "/movie/updateviewsoon");
    }

    /**
     * startet die Anwendung
     */
    public void start() {

        //Medien initalisieren
        DiscEditor discEditor = DiscEditor.createAndLoad();
        if (discEditor.getData().size() == 0) {

            Disc disc = new Disc();
            disc.setName("DVD");
            discEditor.add(disc);

            disc.setName("Blu ray");
            discEditor.add(disc);

            disc.setName("3D Blu ray");
            discEditor.add(disc);

            disc.setName("4K UHD Blu ray");
            discEditor.add(disc);

            disc.setName("SD Datei");
            discEditor.add(disc);

            disc.setName("HD Datei");
            discEditor.add(disc);

            disc.setName("4K UHD Datei");
            discEditor.add(disc);
        }

        //Genre initalisieren
        GenreEditor genreEditor = GenreEditor.createAndLoad();
        if(genreEditor.getData().size() == 0) {

            Genre genre = new Genre();
            genre.setName("Action");
            genreEditor.add(genre);

            genre.setName("Comedy");
            genreEditor.add(genre);

            genre.setName("Dokumentation");
            genreEditor.add(genre);

            genre.setName("Drama");
            genreEditor.add(genre);

            genre.setName("Fantasy");
            genreEditor.add(genre);

            genre.setName("Horror");
            genreEditor.add(genre);

            genre.setName("Kriegsfilm");
            genreEditor.add(genre);

            genre.setName("Mafia");
            genreEditor.add(genre);

            genre.setName("Musik");
            genreEditor.add(genre);

            genre.setName("Märchen");
            genreEditor.add(genre);

            genre.setName("Serien");
            genreEditor.add(genre);

            genre.setName("Si-Fi");
            genreEditor.add(genre);

            genre.setName("Thriller");
            genreEditor.add(genre);

            genre.setName("Western");
            genreEditor.add(genre);

            genre.setName("Zeichentrick");
            genreEditor.add(genre);
        }

        //FSK vorbelegen
        FskEditor fskEditor = FskEditor.createAndLoad();
        if(fskEditor.getData().size() == 0) {

            FSK fsk = new FSK();
            fsk.setName("Ohne Altersbeschränkung");
            fsk.setLevel(0);
            fsk.setImageFile("7030a83c-d037-4905-86a7-55b80c694aad.jpg");
            fskEditor.add(fsk);

            fsk.setName("ab 6 Jahre");
            fsk.setLevel(1);
            fsk.setImageFile("c0046070-1961-4b61-9853-86cae4000361.jpg");
            fskEditor.add(fsk);

            fsk.setName("ab 12 Jahre");
            fsk.setLevel(2);
            fsk.setImageFile("a11f587f-a444-4ffc-bf4e-da2abf9b6890.jpg");
            fskEditor.add(fsk);

            fsk.setName("ab 16 Jahre");
            fsk.setLevel(3);
            fsk.setImageFile("cd1cd52a-384c-4fee-9f02-450ae5571529.jpg");
            fskEditor.add(fsk);

            fsk.setName("ab 18 Jahre");
            fsk.setLevel(3);
            fsk.setImageFile("7c0aa02c-3ebf-4f5b-8ad6-9f12cd36a04d.jpg");
            fskEditor.add(fsk);
        }

        //Beispieldaten einfügen
        if (MovieEditor.countMovies(true) == 0) {

            fskEditor.load();
            genreEditor.load();
            discEditor.load();
            for(int i = 1; i <= 120; i++) {

                Movie movie = new Movie();
                movie.setTitle("Beispiel " + i);
                movie.setSubTitle("Beispiel Zusatztitel " + i);
                movie.setDescription("Beispielbeschreibung");
                movie.setRating(i % 5);
                movie.setPurchaseDate(LocalDate.now());
                movie.setDuration(10 + i);
                movie.setYear(1999);
                movie.setPrice(9.99);
                movie.setFskId(fskEditor.getData().get(0).getId());
                movie.setGenreId(genreEditor.getData().get(0).getId());
                movie.setDiscId(discEditor.getData().get(0).getId());
                MovieEditor.addMovie(movie);
            }
        }
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
