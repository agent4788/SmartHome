package net.kleditzsch.apps.movie.view.admin.sortin;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.movie.model.editor.DiscEditor;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
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

public class MovieSortInDvdServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/sortin/sortindvd.html");
        JtwigModel model = JtwigModel.newModel();

        //Anzahl der zu sortierenden Filme ermitteln
        int newestMoviesCount = 50;
        if(req.getSession().getAttribute("dvdcount") != null) {

            //Anzahl der Filme bekannt
            newestMoviesCount = (int) req.getSession().getAttribute("dvdcount");
        } else {

            //Anzahl der Filme unbekannt
            if(req.getParameter("count") != null) {

                //Anzahl übergeben
                newestMoviesCount = Integer.parseInt(req.getParameter("count"));
                req.getSession().setAttribute("dvdcount", newestMoviesCount);
            } else {

                //Anzahl nicht übergeben -> Einstellungen laden
                SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
                ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                settingsLock.lock();
                newestMoviesCount = settingsEditor.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue();
                settingsLock.unlock();

                //Auswahlseite anzeigen
                JtwigTemplate preTemplate = JtwigFactory.fromClasspath("/webserver/template/movie/admin/sortin/selectdvdcount.html");
                JtwigModel preModel = JtwigModel.newModel();
                preModel.with("count", newestMoviesCount);

                //Template rendern
                resp.setContentType("text/html");
                resp.setStatus(HttpServletResponse.SC_OK);
                preTemplate.render(preModel, new WriterOutputStream(resp.getWriter()));
                return;
            }
        }

        //Blu-ray Discs ermitteln
        DiscEditor discEditor = DiscEditor.createAndLoad();
        List<Disc> discs = discEditor.getData().stream().filter(disc -> disc.getName().toLowerCase().contains("dvd")).collect(Collectors.toList());

        //Liste mit allen Blu-ray Filmen laden
        List<Movie> movies = MovieEditor.listMovies().stream()
                .filter(movie -> !movie.isInMovieBox() && discs.stream().anyMatch(d -> d.getId().equals(movie.getDiscId())))
                .collect(Collectors.toList());

        //Liste der neusten Filme laden
        List<ID> newestMovieIds = new ArrayList<>();
        if(req.getSession().getAttribute("newestMovieList") != null && req.getParameter("reset") == null) {

            //noch offene IDs
            List<String> strIds = (List<String>) req.getSession().getAttribute("newestMovieList");
            newestMovieIds = strIds.stream().map(strId -> ID.of(strId)).collect(Collectors.toList());
        } else {

            //Liste aus der Datenbank laden
            newestMovieIds = MovieEditor.listNewestMovieIds(newestMoviesCount);
            req.getSession().removeAttribute("count");
            resp.sendRedirect("/movie/admin/sortin/dvd");
        }

        //Nächsten Film
        if(req.getParameter("oldid") != null) {

            ID oldId = ID.of(req.getParameter("oldid").trim());
            newestMovieIds = newestMovieIds.stream()
                    .filter(id -> !id.equals(oldId))
                    .collect(Collectors.toList());
        }

        final List<ID> finalNewestMovieIds = newestMovieIds;
        List<Movie> newestMovies = movies.stream()
                .filter(movie -> finalNewestMovieIds.stream().anyMatch(nm -> movie.getId().equals(nm)))
                .filter(movie -> !movie.isInMovieBox() && discs.stream().anyMatch(d -> d.getId().equals(movie.getDiscId())))
                .collect(Collectors.toList());

        //nächsten neuen Film suchen und die 5 vorherigen und nachfolgenden Filme ermitteln
        if(newestMovies.size() > 0) {

            Movie movie = newestMovies.get(0);
            model.with("mainMovie", movie);
            int movieIndex = movies.indexOf(movie);

            //vorherige Filme
            List<Movie> previousMovies = new ArrayList<>(5);
            //if(movieIndex - 5 >= 0) previousMovies.add(movies.get(movieIndex - 5));
            //if(movieIndex - 4 >= 0) previousMovies.add(movies.get(movieIndex - 4));
            if(movieIndex - 3 >= 0) previousMovies.add(movies.get(movieIndex - 3));
            if(movieIndex - 2 >= 0) previousMovies.add(movies.get(movieIndex - 2));
            if(movieIndex - 1 >= 0) previousMovies.add(movies.get(movieIndex - 1));
            model.with("previousMovies", previousMovies);

            //nachfolgende Filme
            List<Movie> nextMovies = new ArrayList<>(5);
            if(movieIndex + 1 < movies.size()) nextMovies.add(movies.get(movieIndex + 1));
            if(movieIndex + 2 < movies.size()) nextMovies.add(movies.get(movieIndex + 2));
            if(movieIndex + 3 < movies.size()) nextMovies.add(movies.get(movieIndex + 3));
            //if(movieIndex + 4 < movies.size()) nextMovies.add(movies.get(movieIndex + 4));
            //if(movieIndex + 5 < movies.size()) nextMovies.add(movies.get(movieIndex + 5));
            model.with("nextMovies", nextMovies);

            //liste mit den restlichen neuen Film IDs speichern
            req.getSession().setAttribute("newestMovieList", newestMovieIds.stream().map(id -> id.get()).collect(Collectors.toList()));
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
