package net.kleditzsch.apps.movie.view.user.movieseries;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.apps.movie.model.movie.meta.SeriesMovie;
import net.kleditzsch.SmartHome.utility.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieSeriesMovieOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("seriesid") != null && req.getParameter("seriesmovieid") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID movieSeriesId = ID.of(req.getParameter("seriesid"));
                ID seriesMovieId = ID.of(req.getParameter("seriesmovieid"));

                //Sortierungen anpassen
                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
                if(movieSeriesOptional.isPresent()) {

                    MovieSeries movieSeries = movieSeriesOptional.get();
                    List<SeriesMovie> seriesMovies = movieSeries.getSeriesMovies().stream().sorted(Comparator.comparingInt(SeriesMovie::getOrderId)).collect(Collectors.toList());
                    Optional<SeriesMovie> seriesMovieOptional = seriesMovies.stream().filter(seriesMovie -> seriesMovie.getId().equals(seriesMovieId)).findFirst();
                    if(seriesMovieOptional.isPresent()) {

                        int currentIndex = seriesMovies.indexOf(seriesMovieOptional.get());
                        int newIndex = 0;
                        if(moveUp) {

                            //nach oben verschieben
                            newIndex = currentIndex -1;
                        } else {

                            //nach unten verschieben
                            newIndex = currentIndex + 1;
                        }
                        if(newIndex >= 0 && newIndex <= 10_000) {

                            //neu sortieren und Speichern;
                            CollectionUtil.moveItem(currentIndex, newIndex, seriesMovies);
                            int orderId = 0;
                            for (SeriesMovie seriesMovie : seriesMovies) {

                                seriesMovie.setOrderId(orderId);
                                orderId++;
                            }
                            movieSeries.getSeriesMovies().clear();
                            movieSeries.getSeriesMovies().addAll(seriesMovies);

                            if(MovieSeriesEditor.updateMovieSeries(movieSeries)) {

                                req.getSession().setAttribute("success", true);
                                req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                                resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
                            } else {

                                req.getSession().setAttribute("success", false);
                                req.getSession().setAttribute("message", "Fehler beim Speichern der sortierung");
                                resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
                            }
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Falscher Index");
                            resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
                        }
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Film nicht gefunden");
                        resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
                    }
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Unbekannter Film Index");
                    resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeriesId.get());
                }
            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/movie/movieseries");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/movie/movieseries");
        }
    }
}
