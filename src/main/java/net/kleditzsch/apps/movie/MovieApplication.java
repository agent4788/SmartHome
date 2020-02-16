package net.kleditzsch.apps.movie;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.apps.movie.model.editor.*;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
import net.kleditzsch.apps.movie.model.movie.meta.FSK;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;
import net.kleditzsch.apps.movie.view.admin.MovieAdminIndexServlet;
import net.kleditzsch.apps.movie.view.admin.dbimport.MovieImportServlet;
import net.kleditzsch.apps.movie.view.admin.dbimport.zip.*;
import net.kleditzsch.apps.movie.view.admin.person.MoviePersonDeleteServlet;
import net.kleditzsch.apps.movie.view.admin.person.MoviePersonFormServlet;
import net.kleditzsch.apps.movie.view.admin.person.MoviePersonListServlet;
import net.kleditzsch.apps.movie.view.admin.disc.MovieDiscDeleteServlet;
import net.kleditzsch.apps.movie.view.admin.disc.MovieDiscFormServlet;
import net.kleditzsch.apps.movie.view.admin.disc.MovieDiscListServlet;
import net.kleditzsch.apps.movie.view.admin.disc.MovieDiscOrderServlet;
import net.kleditzsch.apps.movie.view.admin.fsk.MovieFskDeleteServlet;
import net.kleditzsch.apps.movie.view.admin.fsk.MovieFskFormServlet;
import net.kleditzsch.apps.movie.view.admin.fsk.MovieFskListServlet;
import net.kleditzsch.apps.movie.view.admin.fsk.MovieFskOrderServlet;
import net.kleditzsch.apps.movie.view.admin.genre.MovieGenreDeleteServlet;
import net.kleditzsch.apps.movie.view.admin.genre.MovieGenreFormServlet;
import net.kleditzsch.apps.movie.view.admin.genre.MovieGenreListServlet;
import net.kleditzsch.apps.movie.view.admin.settings.MovieSettingsServlet;
import net.kleditzsch.apps.movie.view.admin.sortin.MovieSortInBluRayServlet;
import net.kleditzsch.apps.movie.view.admin.sortin.MovieSortInDvdServlet;
import net.kleditzsch.apps.movie.view.admin.sortin.MovieSortInIndexServlet;
import net.kleditzsch.apps.movie.view.admin.statistic.MovieStatisticServlet;
import net.kleditzsch.apps.movie.view.user.*;
import net.kleditzsch.apps.movie.view.user.movie.*;
import net.kleditzsch.apps.movie.view.user.moviebox.*;
import net.kleditzsch.apps.movie.view.user.movieseries.*;
import net.kleditzsch.apps.movie.view.user.search.MovieSearchAllServlet;
import net.kleditzsch.apps.movie.view.user.search.MovieSearchPersonMoviesServlet;
import net.kleditzsch.apps.movie.view.user.search.MovieSearchServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Filmdatenbank
 */
public class MovieApplication implements Application {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        //Medien initalisieren
        DiscEditor discEditor = DiscEditor.createAndLoad();
        if (discEditor.getData().size() == 0) {

            Disc disc = new Disc();
            disc.setName("DVD");
            disc.setOrderId(0);
            discEditor.add(disc);

            disc.setName("Blu-ray");
            disc.setOrderId(1);
            discEditor.add(disc);

            disc.setName("3D Blu-ray");
            disc.setOrderId(2);
            discEditor.add(disc);

            disc.setName("4K UHD Blu-ray");
            disc.setOrderId(3);
            discEditor.add(disc);

            disc.setName("SD Datei");
            disc.setOrderId(4);
            discEditor.add(disc);

            disc.setName("HD Datei");
            disc.setOrderId(5);
            discEditor.add(disc);

            disc.setName("4K UHD Datei");
            disc.setOrderId(6);
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
            fsk.setLevel(4);
            fsk.setImageFile("7c0aa02c-3ebf-4f5b-8ad6-9f12cd36a04d.jpg");
            fskEditor.add(fsk);
        }

        //Index anlegen
        if (MovieEditor.countMovies(true) == 0) {

            MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(MovieEditor.COLLECTION);
            collection.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));
        }

        if (MovieBoxEditor.countMovieBoxes() == 0) {

            MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(MovieBoxEditor.COLLECTION);
            collection.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));
        }
        if (MovieSeriesEditor.countMovieSeries() == 0) {

            MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(MovieSeriesEditor.COLLECTION);
            collection.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));
        }
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(MovieIndexServlet.class, "/movie/");
        contextHandler.addServlet(MovieIndexServlet.class, "/movie/index");
        contextHandler.addServlet(MovieNewMovieListServlet.class, "/movie/newmovies");
        contextHandler.addServlet(MovieViewSoonMovieListServlet.class, "/movie/viewsoonmovies");
        contextHandler.addServlet(MovieFskImageServlet.class, "/movie/fsklogo");
        contextHandler.addServlet(MovieCoverImageServlet.class, "/movie/cover");
        contextHandler.addServlet(MovieMovieListServlet.class, "/movie/movie");
        contextHandler.addServlet(MovieMovieFormServlet.class, "/movie/movieform");
        contextHandler.addServlet(MovieMovieViewServlet.class, "/movie/movieview");
        contextHandler.addServlet(MovieMovieDeleteServlet.class, "/movie/moviedelete");
        contextHandler.addServlet(MovieUpdateRatingServlet.class, "/movie/updaterating");
        contextHandler.addServlet(MovieUpdateViewSoonServlet.class, "/movie/updateviewsoon");
        contextHandler.addServlet(MovieMovieBoxListServlet.class, "/movie/moviebox");
        contextHandler.addServlet(MovieMovieBoxFormServlet.class, "/movie/movieboxform");
        contextHandler.addServlet(MovieBoxMovieFormServlet.class, "/movie/boxmovieform");
        contextHandler.addServlet(MovieMovieBoxViewServlet.class, "/movie/movieboxview");
        contextHandler.addServlet(MovieBoxMovieOrderServlet.class, "/movie/boxmovieorder");
        contextHandler.addServlet(MovieBoxMovieDeleteServlet.class, "/movie/boxmoviedelete");
        contextHandler.addServlet(MovieMovieBoxDeleteServlet.class, "/movie/movieboxdelete");
        contextHandler.addServlet(MovieTmdbSearchServlet.class, "/movie/tmdbsearch");
        contextHandler.addServlet(MovieTmdbMovieInfoServlet.class, "/movie/tmdbmovieinfo");
        contextHandler.addServlet(MovieMovieSeriesListServlet.class, "/movie/movieseries");
        contextHandler.addServlet(MovieMovieSeriesFormServlet.class, "/movie/movieseriesform");
        contextHandler.addServlet(MovieMovieSeriesViewServlet.class, "/movie/movieseriesview");
        contextHandler.addServlet(MovieSeriesMovieFormServlet.class, "/movie/seriesmovieform");
        contextHandler.addServlet(MovieSeriesMovieOrderServlet.class, "/movie/seriesmovieorder");
        contextHandler.addServlet(MovieSeriesMovieDeleteServlet.class, "/movie/seriesmoviedelete");
        contextHandler.addServlet(MovieMovieSeriesDeleteServlet.class, "/movie/movieseriesdelete");
        contextHandler.addServlet(MovieSeriesMovieSearchServlet.class, "/movie/seriesmoviesearch");
        contextHandler.addServlet(MovieSearchServlet.class, "/movie/search");
        contextHandler.addServlet(MovieSearchAllServlet.class, "/movie/searchall");
        contextHandler.addServlet(MovieSearchPersonMoviesServlet.class, "/movie/serachpersonmovies");

        contextHandler.addServlet(MovieAdminIndexServlet.class, "/movie/admin/");
        contextHandler.addServlet(MovieAdminIndexServlet.class, "/movie/admin/index");
        contextHandler.addServlet(MoviePersonListServlet.class, "/movie/admin/person");
        contextHandler.addServlet(MoviePersonFormServlet.class, "/movie/admin/personform");
        contextHandler.addServlet(MoviePersonDeleteServlet.class, "/movie/admin/persondelete");
        contextHandler.addServlet(MovieDiscListServlet.class, "/movie/admin/disc");
        contextHandler.addServlet(MovieDiscFormServlet.class, "/movie/admin/discform");
        contextHandler.addServlet(MovieDiscDeleteServlet.class, "/movie/admin/discdelete");
        contextHandler.addServlet(MovieDiscOrderServlet.class, "/movie/admin/discorder");
        contextHandler.addServlet(MovieGenreListServlet.class, "/movie/admin/genre");
        contextHandler.addServlet(MovieGenreFormServlet.class, "/movie/admin/genreform");
        contextHandler.addServlet(MovieGenreDeleteServlet.class, "/movie/admin/genredelete");
        contextHandler.addServlet(MovieFskListServlet.class, "/movie/admin/fsk");
        contextHandler.addServlet(MovieFskFormServlet.class, "/movie/admin/fskform");
        contextHandler.addServlet(MovieFskDeleteServlet.class, "/movie/admin/fskdelete");
        contextHandler.addServlet(MovieFskOrderServlet.class, "/movie/admin/fskorder");
        contextHandler.addServlet(MovieSortInIndexServlet.class, "/movie/admin/sortin");
        contextHandler.addServlet(MovieSortInDvdServlet.class, "/movie/admin/sortin/dvd");
        contextHandler.addServlet(MovieSortInBluRayServlet.class, "/movie/admin/sortin/bluray");
        contextHandler.addServlet(MovieStatisticServlet.class, "/movie/admin/statistic");
        contextHandler.addServlet(MovieSettingsServlet.class, "/movie/admin/settings");

        contextHandler.addServlet(MovieImportServlet.class, "/movie/admin/import");
        contextHandler.addServlet(MovieZipImportServlet.class, "/movie/admin/zipimport/start");
        contextHandler.addServlet(MovieZipImportUnzipServlet.class, "/movie/admin/zipimport/unzip");
        contextHandler.addServlet(MovieZipImportMovieServlet.class, "/movie/admin/zipimport/movie");
        contextHandler.addServlet(MovieZipImportMovieBoxServlet.class, "/movie/admin/zipimport/moviebox");
        contextHandler.addServlet(MovieZipImportCoverServlet.class, "/movie/admin/zipimport/cover");
        contextHandler.addServlet(MovieZipImportFinalServlet.class, "/movie/admin/zipimport/final");
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
