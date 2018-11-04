package net.kleditzsch.SmartHome.view.movie.user.search;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieSearchAllServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/search/searchall.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Einstellungen laden
        int elementsAtPage = 25;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("query") && form.fieldNotEmpty("type")) {

            String query = form.getString("query", "Suche");
            String type = form.getString("type", "Suchtyp");
            model.with("query", query);

            //spezifische Suche
            switch (type) {

                case "movie":

                    //Nach Filmen suchen
                    List<Movie> resultMovies = MovieEditor.search(query);
                    ListPagination<Movie> moviePagination = new ListPagination<>(resultMovies, elementsAtPage, index);
                    moviePagination.setBaseLink("/movie/searchall?type=movie&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("moviesCount", resultMovies.size());
                    model.with("moviePagination", moviePagination);
                    break;
                case "moviebox":

                    //Nach Film Boxen suchen
                    List<MovieBox> resultMovieBoxes = MovieBoxEditor.search(query);
                    ListPagination<MovieBox> boxPagination = new ListPagination<>(resultMovieBoxes, elementsAtPage, index);
                    boxPagination.setBaseLink("/movie/searchall?type=moviebox&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("movieBoxesCount", resultMovieBoxes.size());
                    model.with("movieBoxPagination", boxPagination);
                    break;
                case "movieseries":

                    //Nach Film Reihen suchen
                    List<MovieSeries> resultMovieSeries = MovieSeriesEditor.search(query);
                    ListPagination<MovieSeries> movieSeriesPagination = new ListPagination<>(resultMovieSeries, elementsAtPage, index);
                    movieSeriesPagination.setBaseLink("/movie/searchall?type=movieseries&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("movieSeriesCount", resultMovieSeries.size());
                    model.with("movieSeriesPagination", movieSeriesPagination);
                    break;
                case "director":

                    //Nach Regiseuren suchen
                    List<Director> resultDirectors = DirectorEditor.createAndLoad().getData().stream()
                            .filter(director -> director.getName().toLowerCase().contains(query.toLowerCase()))
                            .sorted(Comparator.comparing(Director::getName))
                            .collect(Collectors.toList());
                    ListPagination<Director> directorsPagination = new ListPagination<>(resultDirectors, elementsAtPage, index);
                    directorsPagination.setBaseLink("/movie/searchall?type=director&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("directorsCount", resultDirectors.size());
                    model.with("directorsPagination", directorsPagination);
                    break;
                case "actor":

                    //Nach Schauspielern suchen
                    List<Actor> resultActors = ActorEditor.createAndLoad().getData().stream()
                            .filter(actor -> actor.getName().toLowerCase().contains(query.toLowerCase()))
                            .sorted(Comparator.comparing(Actor::getName))
                            .collect(Collectors.toList());
                    ListPagination<Actor> actorsPagination = new ListPagination<>(resultActors, elementsAtPage, index);
                    actorsPagination.setBaseLink("/movie/searchall?type=director&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("actorsCount", resultActors.size());
                    model.with("actorsPagination", actorsPagination);
                    break;
                default:


                    break;
            }

        } else {

            //kein Suchbegriff eingegeben
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
