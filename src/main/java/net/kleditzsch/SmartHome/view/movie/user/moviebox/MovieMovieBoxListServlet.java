package net.kleditzsch.SmartHome.view.movie.user.moviebox;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.Pagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieMovieBoxListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/moviebox/movieboxlist.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
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

        Pagination pagination = new Pagination(MovieBoxEditor.countMovieBoxes(), elementsAtPage, index);
        pagination.setBaseLink("/movie/moviebox?index=");
        List<MovieBox> movieBoxessAtPage = MovieBoxEditor.listMovieBoxes(pagination.getCurrentPageIndex(), pagination.getElementsAtPage());
        model.with("pagination", pagination);
        model.with("movieBoxessAtPage", movieBoxessAtPage);
        List<String> newestMovies = MovieEditor.listNewestMovieIds(newestMoviesCount).stream().map(ID::toString).collect(Collectors.toList());
        List<String> boxesWithNewMovies = new ArrayList<>(newestMoviesCount);
        movieBoxessAtPage.forEach(box -> {
            box.getBoxMovies().forEach(boxMovie -> {
                if(newestMovies.contains(boxMovie.getMovieId().toString())) {
                    boxesWithNewMovies.add(box.getId().get());
                }
            });
        });
        model.with("boxesWithNewMovies", boxesWithNewMovies);

        List<String> viewSoonMovies = MovieEditor.listViewSoonMovieIds().stream().map(ID::toString).collect(Collectors.toList());
        List<String> boxesWithViewSoonMovies = new ArrayList<>(newestMoviesCount);
        movieBoxessAtPage.forEach(box -> {
            box.getBoxMovies().forEach(boxMovie -> {
                if(viewSoonMovies.contains(boxMovie.getMovieId().toString())) {
                    boxesWithViewSoonMovies.add(box.getId().get());
                }
            });
        });
        model.with("boxesWithViewSoonMovies", boxesWithViewSoonMovies);

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
