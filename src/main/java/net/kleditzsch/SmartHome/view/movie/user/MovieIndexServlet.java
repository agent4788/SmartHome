package net.kleditzsch.SmartHome.view.movie.user;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class MovieIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> newestMoviesCountOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_NEWEST_MOVIES_COUNT);
        if (newestMoviesCountOptional.isPresent()) {

            newestMoviesCount = newestMoviesCountOptional.get().getValue();
        }
        settingsLock.unlock();

        Random random = new Random();

        //neuste Filme (vier Zufällige Auswählen)
        List<Movie> newestMoviesAll = MovieEditor.listNewestMovies(newestMoviesCount);
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

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
