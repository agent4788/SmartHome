package net.kleditzsch.apps.movie.view.user;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;
import net.kleditzsch.apps.movie.model.movie.Movie;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        newestMoviesCount = settingsEditor.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue();
        settingsLock.unlock();

        Random random = new Random();

        //neuste Filme (vier Zufällige Auswählen)
        List<Movie> newestMoviesAll = MovieEditor.listNewestMovies(newestMoviesCount);
        model.with("newestMoviesCount", newestMoviesAll.size());
        if(newestMoviesAll.size() > 4) {

            List<Movie> newestMovies = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(newestMoviesAll.size());
                newestMovies.add(newestMoviesAll.get(randomIndex));
                newestMoviesAll.remove(randomIndex);
            }
            model.with("newestMovies", newestMovies);
        } else {

            model.with("newestMovies", newestMoviesAll);
        }

        //demnächst anschauen Filme (vier Zufällige Auswählen)
        List<Movie> viewSoonMoviesAll = MovieEditor.listViewSoonMovies();
        model.with("viewSoonMoviesCount", viewSoonMoviesAll.size());
        if(viewSoonMoviesAll.size() > 4) {

            List<Movie> viewSoonMovies = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(viewSoonMoviesAll.size());
                viewSoonMovies.add(viewSoonMoviesAll.get(randomIndex));
                viewSoonMoviesAll.remove(randomIndex);
            }
            model.with("viewSoonMovies", viewSoonMovies);
        } else {

            model.with("viewSoonMovies", viewSoonMoviesAll);
        }

        //beste Bewertung (vier Zufällige Auswählen)
        List<Movie> bestRatedMoviesAll = MovieEditor.listBestRatedMovies(newestMoviesCount);
        if(bestRatedMoviesAll.size() > 4) {

            List<Movie> bestRatedMovies = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(bestRatedMoviesAll.size());
                bestRatedMovies.add(bestRatedMoviesAll.get(randomIndex));
                bestRatedMoviesAll.remove(randomIndex);
            }
            model.with("bestRatedMovies", bestRatedMovies);
        } else {

            model.with("bestRatedMovies", bestRatedMoviesAll);
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
