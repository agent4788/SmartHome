package net.kleditzsch.SmartHome.view.movie.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.SmartHome.util.api.tmdb.data.MovieInfo;
import net.kleditzsch.SmartHome.util.api.tmdb.data.SearchResult;
import net.kleditzsch.SmartHome.util.api.tmdb.exception.TmdbException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieTmdbMovieInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Antwort initalisieren
        resp.setContentType("application/json");

        //Einstellungen laden
        String tmdbApiKey = "";
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> tmdbApiKeyOptional = settingsEditor.getStringSetting(SettingsEditor.MOVIE_TMDB_API_KEY);
        if (tmdbApiKeyOptional.isPresent()) {

            tmdbApiKey = tmdbApiKeyOptional.get().getValue();
        }
        settingsLock.unlock();

        if(tmdbApiKey.length() > 0) {

            if (req.getParameter("id") != null) {

                try {

                    int id = Integer.parseInt(req.getParameter("id"));

                    SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                    Optional<MovieInfo> movieInfoOptional = tmdb.getMovieInfo(id);

                    if(movieInfoOptional.isPresent()) {

                        MovieInfo movieInfo = movieInfoOptional.get();

                        JsonObject mi = new JsonObject();
                        mi.add("id", new JsonPrimitive(movieInfo.getId()));
                        mi.add("title", new JsonPrimitive(movieInfo.getTitle()));
                        movieInfo.getDescription().ifPresent(value -> mi.add("description", new JsonPrimitive(value)));
                        movieInfo.getPosterPath().ifPresent(value -> mi.add("posterPath", new JsonPrimitive(tmdb.getImageURI(value, 500))));
                        movieInfo.getPosterPath().ifPresent(value -> mi.add("posterPathPlain", new JsonPrimitive(value)));
                        movieInfo.getBackgroundPath().ifPresent(value -> mi.add("backgroundPath", new JsonPrimitive(tmdb.getImageURI(value, 500))));
                        movieInfo.getBackgroundPath().ifPresent(value -> mi.add("backgroundPathPlain", new JsonPrimitive(value)));
                        movieInfo.getReleaseDate().ifPresent(value -> mi.add("releaseDate", new JsonPrimitive(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(value))));
                        movieInfo.getReleaseDate().ifPresent(value -> mi.add("year", new JsonPrimitive(value.getYear())));
                        movieInfo.getDuration().ifPresent(value -> mi.add("duration", new JsonPrimitive(value)));

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(mi.toString());
                    } else {

                        //Film nicht gefunden
                        JsonObject error = new JsonObject();
                        error.add("error", new JsonPrimitive(true));
                        error.add("message", new JsonPrimitive("Der Film wurde nicht gefunden"));

                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(error.toString());
                    }
                } catch (TmdbException e) {

                    //TMDB Fehler
                    JsonObject error = new JsonObject();
                    error.add("error", new JsonPrimitive(true));
                    error.add("message", new JsonPrimitive("Keine Suchbegriffe angegeben"));

                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(error.toString());
                } catch (InterruptedException e) {

                    //Anwendungsfehler
                    JsonObject error = new JsonObject();
                    error.add("error", new JsonPrimitive(true));
                    error.add("message", new JsonPrimitive("Allgemeiner Anwendungsfehler"));

                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(error.toString());
                } catch (NumberFormatException e) {

                    //Ungültige Film ID
                    JsonObject error = new JsonObject();
                    error.add("error", new JsonPrimitive(true));
                    error.add("message", new JsonPrimitive("Ungültige Film ID"));

                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(error.toString());
                }
            } else  {

                //keine Suchbegriffe eingestellt
                JsonObject error = new JsonObject();
                error.add("error", new JsonPrimitive(true));
                error.add("message", new JsonPrimitive("Keine Film ID angegeben"));

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(error.toString());
            }
        } else {

            //kein API Key eingestellt
            JsonObject error = new JsonObject();
            error.add("error", new JsonPrimitive(true));
            error.add("message", new JsonPrimitive("Kein Api Schlüssel eingestellt"));

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(error.toString());
        }
    }
}
