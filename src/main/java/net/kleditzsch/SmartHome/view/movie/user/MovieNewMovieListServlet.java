package net.kleditzsch.SmartHome.view.movie.user;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.editor.GenreEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieNewMovieListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/newmovielist.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 20;
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        Optional<IntegerSetting> newestMoviesCountOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_NEWEST_MOVIES_COUNT);
        if (newestMoviesCountOptional.isPresent()) {

            newestMoviesCount = newestMoviesCountOptional.get().getValue();
        }
        settingsLock.unlock();

        List<Movie> newestMovies = MovieEditor.listNewestMovies(newestMoviesCount);

        ListPagination pagination = new ListPagination(newestMovies, elementsAtPage, index);
        pagination.setBaseLink("/movie/newmovies?index=");
        List<Movie> moviesAtPage = MovieEditor.listMovies(pagination.getCurrentPageIndex(), pagination.getElementsAtPage());
        model.with("pagination", pagination);
        model.with("discEditor", DiscEditor.createAndLoad());
        model.with("genreEditor", GenreEditor.createAndLoad());
        model.with("viewSoonMovies", MovieEditor.listViewSoonMovieIds().stream().map(ID::toString).collect(Collectors.toList()));

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
