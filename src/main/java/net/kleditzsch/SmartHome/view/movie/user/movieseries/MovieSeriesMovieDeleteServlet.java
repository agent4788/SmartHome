package net.kleditzsch.SmartHome.view.movie.user.movieseries;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieSeriesEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.model.movie.movie.meta.BoxMovie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.SeriesMovie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieSeriesMovieDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        ID movieSeriesId = null;

        String movieSeriesIdStr = req.getParameter("movieseriesid");
        String seriesMovieIdStr = req.getParameter("seriesmovieid");
        if(movieSeriesIdStr != null && seriesMovieIdStr != null) {

            try {

                movieSeriesId = ID.of(movieSeriesIdStr);
                final ID seriesMovieId = ID.of(seriesMovieIdStr);

                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
                if(movieSeriesOptional.isPresent()) {

                    MovieSeries movieSeries = movieSeriesOptional.get();

                    //Film aus der Reihe löschen
                    Optional<SeriesMovie> seriesMovieOptional = movieSeries.getSeriesMovies().stream().filter(seriesMovie -> seriesMovie.getId().equals(seriesMovieId)).findFirst();
                    if(seriesMovieOptional.isPresent()) {

                        SeriesMovie seriesMovie = seriesMovieOptional.get();
                        movieSeries.getSeriesMovies().remove(seriesMovie);
                        success = MovieSeriesEditor.updateMovieSeries(movieSeries);

                        //Film aktualisieren
                        Optional<Movie> movieOptional = MovieEditor.getMovie(seriesMovie.getMovieId());
                        if(movieOptional.isPresent()) {

                            Movie movie = movieOptional.get();
                            movie.setInMovieSeries(null);
                            success = MovieEditor.updateMovie(movie);
                        } else {

                            success = false;
                        }
                    } else {

                        success = false;
                    }
                } else {

                    success = false;
                }

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Der Film wurde erfolgreich gelöscht");
            if(movieSeriesId != null) {

                resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
            }
            resp.sendRedirect("/movie/movieseries");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Film konnte nicht gelöscht werden");
            if(movieSeriesId != null) {

                resp.sendRedirect("/movie/movieseriesview?edit=1id=" + movieSeriesId.get());
            }
            resp.sendRedirect("/movie/movieseries");
        }
    }
}
