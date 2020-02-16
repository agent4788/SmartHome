package net.kleditzsch.apps.movie.view.user.movie;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.apps.movie.model.editor.*;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.string.StringUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieMovieViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movie/movieview.html");
        JtwigModel model = JtwigModel.newModel();

        //Neuste Filme laden
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> newestMoviesCountOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_NEWEST_MOVIES_COUNT);
        if (newestMoviesCountOptional.isPresent()) {

            newestMoviesCount = newestMoviesCountOptional.get().getValue();
        }
        settingsLock.unlock();
        model.with("newestMovies", MovieEditor.listNewestMovieIds(newestMoviesCount).stream().map(ID::toString).collect(Collectors.toList()));

        try {

            ID id = ID.of(req.getParameter("id").trim());

            Optional<Movie> movieOptional = MovieEditor.getMovie(id);
            if(movieOptional.isPresent()) {

                Movie movie = movieOptional.get();
                model.with("movie", movie);
                model.with("discEditor", DiscEditor.createAndLoad());
                model.with("genreEditor", GenreEditor.createAndLoad());
                model.with("fskEditor", FskEditor.createAndLoad());
                model.with("personEditor", PersonEditor.createAndLoad());

                //Filmreihe laden
                if(movie.getSeriesId().isPresent()) {

                    Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movie.getSeriesId().get());
                    if(movieSeriesOptional.isPresent()) {

                        MovieSeries movieSeries = movieSeriesOptional.get();
                        movieSeries.setDescription(StringUtil.limitTextLength(movieSeries.getDescription().orElse(""), 250));
                        model.with("movieSeries", movieSeries);
                    }
                }
            } else {

                //Element nicht gefunden
                throw new Exception();
            }

        } catch (Exception e) {}

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
