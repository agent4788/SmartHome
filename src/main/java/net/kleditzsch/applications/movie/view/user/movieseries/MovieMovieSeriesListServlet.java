package net.kleditzsch.applications.movie.view.user.movieseries;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.Pagination;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.applications.movie.model.movie.MovieSeries;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieMovieSeriesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movieseries/movieserieslist.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
        newestMoviesCount = settingsEditor.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue();
        settingsLock.unlock();

        Pagination pagination = new Pagination(MovieSeriesEditor.countMovieSeries(), elementsAtPage, index);
        pagination.setBaseLink("/movie/movieseries?index=");
        List<MovieSeries> movieSeriesAtPage = MovieSeriesEditor.listMovieSeries(pagination.getCurrentPageIndex(), pagination.getElementsAtPage());
        model.with("pagination", pagination);
        model.with("movieSeriesAtPage", movieSeriesAtPage);
        List<String> newestMovies = MovieEditor.listNewestMovieIds(newestMoviesCount).stream().map(ID::toString).collect(Collectors.toList());
        List<String> seriesWithNewMovies = new ArrayList<>(newestMoviesCount);
        movieSeriesAtPage.forEach(box -> {
            box.getSeriesMovies().forEach(seriesMovie -> {
                if(newestMovies.contains(seriesMovie.getMovieId().toString())) {
                    seriesWithNewMovies.add(box.getId().get());
                }
            });
        });
        model.with("seriesWithNewMovies", seriesWithNewMovies);

        List<String> viewSoonMovies = MovieEditor.listViewSoonMovieIds().stream().map(ID::toString).collect(Collectors.toList());
        List<String> seriesWithViewSoonMovies = new ArrayList<>(newestMoviesCount);
        movieSeriesAtPage.forEach(series -> {
            series.getSeriesMovies().forEach(boxMovie -> {
                if(viewSoonMovies.contains(boxMovie.getMovieId().toString())) {
                    seriesWithViewSoonMovies.add(series.getId().get());
                }
            });
        });
        model.with("seriesWithViewSoonMovies", seriesWithViewSoonMovies);

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
