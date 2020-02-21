package net.kleditzsch.applications.movie.view.user.movieseries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.smarthome.utility.datetime.TimeUtil;
import net.kleditzsch.smarthome.utility.string.StringUtil;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.movie.Movie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MovieSeriesMovieSearchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Antwort initalisieren
        resp.setContentType("application/json");

        if(req.getParameter("query") != null) {

            String query = req.getParameter("query").trim();
            List<Movie> searchResultList =  MovieEditor.search(query);

            JsonObject result = new JsonObject();
            result.addProperty("resultCount", searchResultList.size());
            JsonArray results = new JsonArray();
            searchResultList.forEach(movie -> {

                JsonObject movieJson = new JsonObject();
                movieJson.addProperty("id", movie.getId().get());
                movieJson.addProperty("title", movie.getTitle() + (movie.getSubTitle().length() > 0 ? " - " + movie.getSubTitle() : ""));
                movieJson.addProperty("cover", movie.getCoverFile());
                movieJson.addProperty("description", StringUtil.limitTextLength(movie.getDescription().orElse(""), 500));
                movieJson.addProperty("duration", TimeUtil.formatSeconds(movie.getDuration() * 60 + 1, false));
                movieJson.addProperty("year", movie.getYear());
                results.add(movieJson);
            });
            result.add("results", results);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(result.toString());
        } else {

            //kein API Key eingestellt
            JsonObject error = new JsonObject();
            error.add("error", new JsonPrimitive(true));
            error.add("message", new JsonPrimitive("Kein Suchbegriff angegeben"));

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(error.toString());
        }
    }
}
