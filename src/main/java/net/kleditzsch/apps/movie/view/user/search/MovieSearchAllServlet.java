package net.kleditzsch.apps.movie.view.user.search;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.ListPagination;
import net.kleditzsch.apps.movie.model.editor.MovieBoxEditor;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;
import net.kleditzsch.apps.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.apps.movie.model.editor.PersonEditor;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieBox;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.apps.movie.model.movie.meta.Person;
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
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
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
                case "person":

                    //Nach Regiseuren suchen
                    List<Person> resultPersons = PersonEditor.createAndLoad().getData().stream()
                            .filter(director -> director.getName().toLowerCase().contains(query.toLowerCase()))
                            .sorted(Comparator.comparing(Person::getName))
                            .collect(Collectors.toList());
                    ListPagination<Person> directorsPagination = new ListPagination<>(resultPersons, elementsAtPage, index);
                    directorsPagination.setBaseLink("/movie/searchall?type=person&query=" + HtmlEscapers.htmlEscaper().escape(query) + "&index=");
                    model.with("personsCount", resultPersons.size());
                    model.with("personsPagination", directorsPagination);
                    break;
                default:


                    break;
            }

        } else {

            //kein Suchbegriff eingegeben
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
