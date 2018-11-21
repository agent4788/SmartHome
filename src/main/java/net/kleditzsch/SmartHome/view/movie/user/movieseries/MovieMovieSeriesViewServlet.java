package net.kleditzsch.SmartHome.view.movie.user.movieseries;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.model.movie.movie.meta.BoxMovie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.FSK;
import net.kleditzsch.SmartHome.model.movie.movie.meta.SeriesMovie;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.string.StringUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MovieMovieSeriesViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movieseries/movieseriesview.html");
        JtwigModel model = JtwigModel.newModel();

        try {

            ID id = ID.of(req.getParameter("id").trim());
            boolean editMode = req.getParameter("edit") != null && req.getParameter("edit").trim().equals("1");

            Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(id);
            if(movieSeriesOptional.isPresent()) {

                MovieSeries movieSeries = movieSeriesOptional.get();
                List<SeriesMovie> seriesMovies = movieSeries.getSeriesMovies();
                seriesMovies = seriesMovies.stream()
                        .sorted((movie1, movie2) -> {

                            int orderMovie1 = movieSeries.getSeriesMovies().stream().mapToInt(SeriesMovie::getOrderId).findFirst().getAsInt();
                            int orderMovie2 = movieSeries.getSeriesMovies().stream().mapToInt(SeriesMovie::getOrderId).findFirst().getAsInt();
                            return Integer.compare(orderMovie1, orderMovie2);
                        })
                        .collect(Collectors.toList());

                List<Movie> movieList = MovieEditor.listMoviesByIDList(seriesMovies.stream().map(SeriesMovie::getMovieId).collect(Collectors.toList()));
                movieList.forEach(movie -> {

                    String description = movie.getDescription().orElse("");
                    movie.setDescription(StringUtil.limitTextLength(description, 500));
                });
                Map<String, Movie> movies = new HashMap<>(seriesMovies.size());
                movieList.forEach(movie -> movies.put(movie.getId().get(), movie));

                Map<String, Integer> seriesMoviesOrder = new HashMap<>(movieSeries.getSeriesMovies().size());
                movieSeries.getSeriesMovies().forEach(seriesMovie -> seriesMoviesOrder.put(seriesMovie.getMovieId().get(), seriesMovie.getOrderId()));

                IntSummaryStatistics stats = movieList.stream().mapToInt(Movie::getYear).summaryStatistics();
                int minYear = stats.getMin() <= LocalDate.now().getYear() ? stats.getMin() : 1900;
                int maxYear = stats.getMax() >= 1900 ? stats.getMax() : LocalDate.now().getYear();

                model.with("movieSeries", movieSeries);
                model.with("seriesMovies", seriesMovies);
                model.with("movies", movies);
                model.with("minYear", minYear);
                model.with("maxYear", maxYear);
                model.with("editMode", editMode);
                model.with("seriesMoviesOrder", seriesMoviesOrder);
                model.with("maxOrderId", movieSeries.getSeriesMovies().stream().mapToInt(SeriesMovie::getOrderId).summaryStatistics().getMax());
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
