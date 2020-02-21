package net.kleditzsch.applications.movie.view.user.movieseries;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.applications.movie.model.movie.Movie;
import net.kleditzsch.applications.movie.model.movie.MovieSeries;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieMovieSeriesDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(id);
                if(movieSeriesOptional.isPresent()) {

                    MovieSeries movieSeries = movieSeriesOptional.get();

                    //Filmbox ggf. incl Cover löschen
                    success = MovieSeriesEditor.deleteMovieSeries(movieSeries.getId());

                    //Filme aktualisieren
                    movieSeries.getSeriesMovies().forEach(seriesMovie -> {

                        Optional<Movie> movieOptional = MovieEditor.getMovie(seriesMovie.getMovieId());
                        if(movieOptional.isPresent()) {

                            Movie movie = movieOptional.get();
                            movie.setInMovieSeries(null);
                            MovieEditor.updateMovie(movie);
                        }
                    });

                    //Cover Datei löschen
                    if(movieSeries.getPosterFile() != null && movieSeries.getPosterFile().length() > 0) {

                        Path logoFile = Paths.get("upload/cover").resolve(movieSeries.getPosterFile());
                        try {

                            Files.delete(logoFile);
                        } catch (Exception e) {}
                    }
                } else {

                    success = false;
                }
            } catch (Exception e) {

                success = false;
                e.printStackTrace();
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Film Reihe wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/movieseries");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Film Reihe konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/movieseries");
        }
    }
}
