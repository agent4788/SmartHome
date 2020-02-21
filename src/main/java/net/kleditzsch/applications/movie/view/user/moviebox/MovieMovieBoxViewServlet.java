package net.kleditzsch.applications.movie.view.user.moviebox;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.string.StringUtil;
import net.kleditzsch.applications.movie.model.editor.*;
import net.kleditzsch.applications.movie.model.movie.Movie;
import net.kleditzsch.applications.movie.model.movie.MovieBox;
import net.kleditzsch.applications.movie.model.movie.MovieSeries;
import net.kleditzsch.applications.movie.model.movie.meta.BoxMovie;
import net.kleditzsch.applications.movie.model.movie.meta.FSK;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieMovieBoxViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/moviebox/movieboxview.html");
        JtwigModel model = JtwigModel.newModel();

        //Neuste Filme laden
        int newestMoviesCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        newestMoviesCount = settingsEditor.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue();
        settingsLock.unlock();
        model.with("newestMovies", MovieEditor.listNewestMovieIds(newestMoviesCount).stream().map(ID::toString).collect(Collectors.toList()));

        try {

            ID id = ID.of(req.getParameter("id").trim());
            boolean editMode = req.getParameter("edit") != null && req.getParameter("edit").trim().equals("1");

            Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(id);
            if(movieBoxOptional.isPresent()) {

                MovieBox movieBox = movieBoxOptional.get();
                List<Movie> boxMovies = MovieEditor.listMoviesByIDList(movieBox.getBoxMovies().stream().map(BoxMovie::getMovieId).collect(Collectors.toList()));
                boxMovies = boxMovies.stream()
                        .sorted((movie1, movie2) -> {

                            int orderMovie1 = movieBox.getBoxMovies().stream().filter(boxMovie -> boxMovie.getMovieId().equals(movie1.getId())).mapToInt(BoxMovie::getOrderId).findFirst().getAsInt();
                            int orderMovie2 = movieBox.getBoxMovies().stream().filter(boxMovie -> boxMovie.getMovieId().equals(movie2.getId())).mapToInt(BoxMovie::getOrderId).findFirst().getAsInt();
                            return Integer.compare(orderMovie1, orderMovie2);
                        })
                        .collect(Collectors.toList());

                //gesamte Laufzeit
                long movieBoxDuration = 0;
                if(boxMovies.size() > 0) {

                    movieBoxDuration = boxMovies.stream().mapToInt(Movie::getDuration).summaryStatistics().getSum();
                }

                //höchste Altersfreigabe
                FSK movieBoxFsk = null;
                FskEditor fskEditor = FskEditor.createAndLoad();
                if(boxMovies.size() > 0) {

                    int maxLevel = boxMovies.stream().mapToInt((movie) -> {

                                Optional<FSK> fskOptional = fskEditor.getById(movie.getFskId());
                                return fskOptional.map(FSK::getLevel).orElse(-1);
                            })
                            .summaryStatistics()
                            .getMax();
                    Optional<FSK> maxFsk = fskEditor.getData().stream().filter(fsk -> fsk.getLevel() == maxLevel).findFirst();
                    if(maxFsk.isPresent()) {

                        movieBoxFsk = maxFsk.get();
                    }
                } else {

                    //FSK 0
                    Optional<FSK> maxFsk = fskEditor.getData().stream().filter(fsk -> fsk.getLevel() == 0).findFirst();
                    if(maxFsk.isPresent()) {

                        movieBoxFsk = maxFsk.get();
                    }
                }

                //Filmreihen laden
                List<ID> movieSeriesIDs = new ArrayList<>();
                boxMovies.forEach(boxMovie -> {
                    if(boxMovie.getSeriesId().isPresent()) {

                        movieSeriesIDs.add(boxMovie.getSeriesId().get());
                    }
                });
                List<MovieSeries> movieSeriesList = MovieSeriesEditor.listMovieSeriesByIDList(movieSeriesIDs);
                Map<String, MovieSeries> movieSeriesMap = new HashMap<>(movieSeriesList.size());
                movieSeriesList.forEach(movieSeries -> {

                    movieSeries.setDescription(StringUtil.limitTextLength(movieSeries.getDescription().orElse(""), 250));
                    movieSeriesMap.put(movieSeries.getId().get(), movieSeries);
                });

                Map<String, Integer> boxMoviesOrder = new HashMap<>(movieBox.getBoxMovies().size());
                movieBox.getBoxMovies().forEach(boxMovie -> boxMoviesOrder.put(boxMovie.getMovieId().get(), boxMovie.getOrderId()));

                model.with("movieBox", movieBox);
                model.with("boxMovies", boxMovies);
                model.with("movieBoxDuration", movieBoxDuration);
                model.with("movieBoxFsk", movieBoxFsk);
                model.with("editMode", editMode);
                model.with("boxMoviesOrder", boxMoviesOrder);
                model.with("maxOrderId", movieBox.getBoxMovies().stream().mapToInt(BoxMovie::getOrderId).summaryStatistics().getMax());
                model.with("movieSeriesList", movieSeriesList);
                model.with("movieSeriesMap", movieSeriesMap);

                model.with("discEditor", DiscEditor.createAndLoad());
                model.with("genreEditor", GenreEditor.createAndLoad());
                model.with("fskEditor", fskEditor);
                model.with("personEditor", PersonEditor.createAndLoad());
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
