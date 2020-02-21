package net.kleditzsch.apps.movie.view.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.apps.automation.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.apps.automation.api.tmdb.data.MovieInfo;
import net.kleditzsch.apps.automation.api.tmdb.data.SearchResult;
import net.kleditzsch.apps.automation.api.tmdb.exception.TmdbException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieTmdbSearchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Antwort initalisieren
        resp.setContentType("application/json");

        //Einstellungen laden
        String tmdbApiKey = "";
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        tmdbApiKey = settingsEditor.getStringSetting(Settings.MOVIE_TMDB_API_KEY).getValue();
        settingsLock.unlock();

        if(tmdbApiKey.length() > 0) {

            if (req.getParameter("query") != null) {

                String query = req.getParameter("query");
                int page = 1;
                if(req.getParameter("page") != null) {

                    try {

                        int rawPage = Integer.parseInt(req.getParameter("page"));
                        if(rawPage >= 1 && rawPage <= 1000) {

                            page = rawPage;
                        }
                    } catch (NumberFormatException e) {}
                }

                try {

                    SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                    SearchResult searchResult = tmdb.searchMovie(query, page);

                    JsonObject result = new JsonObject();
                    result.add("page", new JsonPrimitive(searchResult.getPage()));
                    result.add("pages", new JsonPrimitive(searchResult.getPages()));
                    result.add("resultCount", new JsonPrimitive(searchResult.getResultCount()));

                    JsonArray results = new JsonArray();
                    for(MovieInfo movieInfo : searchResult.getResults()) {

                        JsonObject mi = new JsonObject();
                        mi.add("id", new JsonPrimitive(movieInfo.getId()));
                        mi.add("title", new JsonPrimitive(movieInfo.getTitle()));
                        movieInfo.getDescription().ifPresent(value -> mi.add("description", new JsonPrimitive(value)));
                        movieInfo.getPosterPath().ifPresent(value -> mi.add("posterPath", new JsonPrimitive(tmdb.getImageURI(value, 500))));
                        movieInfo.getBackgroundPath().ifPresent(value -> mi.add("backgroundPath", new JsonPrimitive(tmdb.getImageURI(value, 500))));
                        movieInfo.getReleaseDate().ifPresent(value -> mi.add("releaseDate", new JsonPrimitive(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(value))));

                        results.add(mi);
                    }
                    result.add("results", results);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(result.toString());

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
                }
            } else  {

                //keine Suchbegriffe eingestellt
                JsonObject error = new JsonObject();
                error.add("error", new JsonPrimitive(true));
                error.add("message", new JsonPrimitive("Keine Suchbegriffe angegeben"));

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
