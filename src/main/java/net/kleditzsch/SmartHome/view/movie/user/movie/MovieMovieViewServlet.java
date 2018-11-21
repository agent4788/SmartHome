package net.kleditzsch.SmartHome.view.movie.user.movie;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.string.StringUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieMovieViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movie/movieview.html");
        JtwigModel model = JtwigModel.newModel();

        try {

            ID id = ID.of(req.getParameter("id").trim());

            Optional<Movie> movieOptional = MovieEditor.getMovie(id);
            if(movieOptional.isPresent()) {

                Movie movie = movieOptional.get();
                model.with("movie", movie);
                model.with("discEditor", DiscEditor.createAndLoad());
                model.with("genreEditor", GenreEditor.createAndLoad());
                model.with("fskEditor", FskEditor.createAndLoad());
                model.with("directorEditor", DirectorEditor.createAndLoad());
                model.with("actorEditor", ActorEditor.createAndLoad());

                //Filmreihe laden
                if(movie.getSeriesId().isPresent()) {

                    Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movie.getSeriesId().get());
                    if(movieSeriesOptional.isPresent()) {

                        MovieSeries movieSeries = movieSeriesOptional.get();
                        movieSeries.setDescription(StringUtil.limitTextLength(movieSeries.getDescription().orElse(""), 250));
                        model.with("movieSeries", movieSeries);
                    }
                }
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
