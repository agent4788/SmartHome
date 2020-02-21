package net.kleditzsch.apps.movie.view.user.movie;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.Pagination;
import net.kleditzsch.apps.movie.model.editor.DiscEditor;
import net.kleditzsch.apps.movie.model.editor.GenreEditor;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieFilter;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieMovieListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movie/movielist.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Filter
        MovieFilter filter = new MovieFilter();
        if(req.getParameter("duration") != null) {

            switch(req.getParameter("duration")) {

                case "short":

                    filter.setMaxLength(90);
                    model.with("filtered", true);
                    model.with("durationFilter", "short");
                    break;
                case "normal":

                    filter.setMinLength(90);
                    filter.setMaxLength(150);
                    model.with("filtered", true);
                    model.with("durationFilter", "normal");
                    break;
                case "long":

                    filter.setMinLength(150);
                    model.with("filtered", true);
                    model.with("durationFilter", "long");
                    break;
            }
        } else  if(req.getParameter("genre") != null) {

            try {

                ID genreId = ID.of(req.getParameter("genre"));
                filter.setGenre(genreId);
                model.with("filtered", true);
                model.with("genreFilter", genreId.get());
            } catch (Exception e) {}

        } else if(req.getParameter("disc") != null) {

            try {

                ID discId = ID.of(req.getParameter("disc"));
                filter.setDisc(discId);
                model.with("filtered", true);
                model.with("discFilter", discId.get());
            } catch (Exception e) {}
        } else  if(req.getParameter("quality") != null) {

            switch (req.getParameter("quality").toLowerCase()) {

                case "sd":

                    filter.setQuality(Disc.Quality.SD);
                    model.with("filtered", true);
                    model.with("qualityFilter", "sd");
                    break;
                case "hd":

                    filter.setQuality(Disc.Quality.HD);
                    model.with("filtered", true);
                    model.with("qualityFilter", "hd");
                    break;
                case "uhd":

                    filter.setQuality(Disc.Quality.UHD);
                    model.with("filtered", true);
                    model.with("qualityFilter", "uhd");
                    break;
            }
        } else if(req.getParameter("rating") != null) {

            try {

                int rating = Integer.parseInt(req.getParameter("rating"));
                if(rating >= 0 && rating <= 5) {

                    filter.setRating(rating);
                    model.with("filtered", true);
                    model.with("ratingFilter", rating);
                }
            } catch (Exception e) {}
        }

        int elementsAtPage = 25;
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
        newestMoviesCount = settingsEditor.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue();
        settingsLock.unlock();

        Pagination pagination = new Pagination(MovieEditor.countMovies(filter,true), elementsAtPage, index);
        if(filter.getMinLength() > 0 || filter.getMaxLength() < Integer.MAX_VALUE) {

            pagination.setBaseLink("/movie/movie?duration=" + HtmlEscapers.htmlEscaper().escape(req.getParameter("duration")) + "&index=");
        } else if(filter.getGenre().isPresent()) {

            pagination.setBaseLink("/movie/movie?genre=" + filter.getGenre().get().get() + "&index=");
        } else if(filter.getDisc().isPresent()) {

            pagination.setBaseLink("/movie/movie?disc=" + filter.getDisc().get().get() + "&index=");
        }  else if(filter.getQuality().isPresent()) {

            pagination.setBaseLink("/movie/movie?quality=" + filter.getQuality().get() + "&index=");
        } else {

            pagination.setBaseLink("/movie/movie?index=");
        }
        List<Movie> moviesAtPage = MovieEditor.listMovies(filter, pagination.getCurrentPageIndex(), pagination.getElementsAtPage());
        model.with("movieFilter", filter);
        model.with("pagination", pagination);
        model.with("moviesAtPage", moviesAtPage);
        model.with("newestMovies", MovieEditor.listNewestMovieIds(newestMoviesCount).stream().map(ID::toString).collect(Collectors.toList()));
        model.with("viewSoonMovies", MovieEditor.listViewSoonMovieIds().stream().map(ID::toString).collect(Collectors.toList()));

        model.with("genreList", GenreEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Genre::getName)).collect(Collectors.toList()));
        model.with("dicsList", DiscEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Disc::getOrderId)).collect(Collectors.toList()));

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
