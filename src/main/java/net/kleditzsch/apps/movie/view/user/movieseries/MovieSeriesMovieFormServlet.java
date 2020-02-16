package net.kleditzsch.apps.movie.view.user.movieseries;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.*;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.apps.movie.model.movie.meta.*;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.Request;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class MovieSeriesMovieFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movieseries/seriesmovieform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        MovieSeries movieSeries = null;
        SeriesMovie seriesMovie = null;
        Movie movie = null;

        if(req.getParameter("seriesid") != null && req.getParameter("seriesmovieid") != null) {

            addElement = false;

            try {

                ID movieSeriesId = ID.of(req.getParameter("seriesid").trim());
                ID seriesMovieId = ID.of(req.getParameter("seriesmovieid").trim());

                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
                if(movieSeriesOptional.isPresent()) {

                    movieSeries = movieSeriesOptional.get();
                    Optional<SeriesMovie> seriesMovieOptional = movieSeries.getSeriesMovies().stream().filter(seriesMovie1 -> seriesMovie1.getId().equals(seriesMovieId)).findFirst();
                    if(seriesMovieOptional.isPresent()) {

                        seriesMovie = seriesMovieOptional.get();
                        Optional<Movie> movieOptional = MovieEditor.getMovie(seriesMovie.getMovieId());
                        if(movieOptional.isPresent() && movieOptional.get().getSeriesId().isPresent() && movieOptional.get().getSeriesId().get().equals(movieSeriesId)) {

                            movieSeries = movieSeriesOptional.get();
                            movie = movieOptional.get();
                            seriesMovie = seriesMovieOptional.orElseGet(SeriesMovie::new);
                        } else {

                            //Element nicht gefunden oder ungültig
                            throw new Exception();
                        }
                    } else {

                        //Element nicht gefunden oder ungültig
                        throw new Exception();
                    }
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                e.printStackTrace();
                model.with("error", "Der Film wurde nicht gefunden");
            }
        } else if(req.getParameter("seriesid") != null) {

            ID movieSeriesId = ID.of(req.getParameter("seriesid").trim());

            Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
            if(movieSeriesOptional.isPresent()) {

                movieSeries = movieSeriesOptional.get();
                seriesMovie = new SeriesMovie();
                seriesMovie.setId(ID.create());
                movie = new Movie();
                movie.setId(ID.create());
            }
        }
        model.with("addElement", addElement);
        model.with("movieSeries", movieSeries);
        model.with("seriesMovie", seriesMovie);
        model.with("movie", movie);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Dateiupload initalisieren
        req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),    //Temp Ordner
                1024L * 1024L * 50L,          //Maximale Dateigröße
                1024L * 1024L * 60L,       //Maximale größe der gesmten Anfrage
                1024 * 1024 * 5           //Dateigröße ab der in den Temp Ordner geschrieben wird
        ));

        //Optionale Parameter
        final ID seriesMovieId;
        String partDescription = "";
        String partNumber = "";

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        ID movieSeriesId = form.getId("seriesid", "Film Reihe");
        if(!addElement) {
            seriesMovieId = form.getId("seriesmovieid", "Film Box Film");
        } else {
            seriesMovieId = null;
        }
        ID movieId = form.getId("movieid", "Film ID");
        if(form.fieldNotEmpty("partDescription")) {

            partDescription = form.getString("partDescription", "Beschreibung", 1, 50);
        }
        if(form.fieldNotEmpty("partNumber")) {

            partNumber = form.getString("partNumber", "Nummer des Teils", Pattern.compile("^\\d+(\\.\\d+)?$"));
        }
        SeriesMovie.SeriesMovieType seriesMovieType = form.getEnum("type", "Film Typ", SeriesMovie.SeriesMovieType.class);

        form.getErrorMessages().forEach((k, v) -> System.out.println(k + " -> " + v));

        if (form.isSuccessful()) {

            Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
            if(movieSeriesOptional.isPresent()) {

                MovieSeries movieSeries = movieSeriesOptional.get();
                if(addElement) {

                    //neues Element hinzufügen
                    SeriesMovie seriesMovie = new SeriesMovie();
                    seriesMovie.setId(ID.create());
                    seriesMovie.setMovieId(movieId);
                    seriesMovie.setPartDescription(partDescription);
                    seriesMovie.setPartNumber(partNumber);
                    seriesMovie.setSeriesType(seriesMovieType);

                    int orderId = movieSeries.getSeriesMovies().stream().mapToInt(SeriesMovie::getOrderId).summaryStatistics().getMax() + 1;
                    if(orderId < 0) {

                        orderId = 0;
                    }
                    seriesMovie.setOrderId(orderId);
                    int timeOrder = movieSeries.getSeriesMovies().stream().mapToInt(SeriesMovie::getTimeOrder).summaryStatistics().getMax() + 1;
                    if(timeOrder < 0) {

                        timeOrder = 0;
                    }
                    seriesMovie.setTimeOrder(timeOrder);

                    //Film aktualisieren
                    Optional<Movie> movieOptional = MovieEditor.getMovie(movieId);
                    if(movieOptional.isPresent()) {

                        Movie movie = movieOptional.get();
                        movie.setInMovieSeries(movieSeriesId);
                        MovieEditor.updateMovie(movie);
                    }

                    //Filmreihe aktualisieren
                    movieSeries.getSeriesMovies().add(seriesMovie);
                    MovieSeriesEditor.updateMovieSeries(movieSeries);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Film wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeries.getId().get());
                } else {

                    //Element bearbeiten
                    Optional<SeriesMovie> seriesMovieOptional = movieSeries.getSeriesMovies().stream().filter(seriesMovie -> seriesMovie.getId().equals(seriesMovieId)).findFirst();
                    if (seriesMovieOptional.isPresent()) {

                        SeriesMovie seriesMovie = seriesMovieOptional.get();
                        seriesMovie.setMovieId(movieId);
                        seriesMovie.setPartDescription(partDescription);
                        seriesMovie.setPartNumber(partNumber);
                        seriesMovie.setSeriesType(seriesMovieType);

                        MovieSeriesEditor.updateMovieSeries(movieSeries);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Film wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/movie/movieseriesview?edit=1&id=" + movieSeries.getId().get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Film konnte nicht gefunden werden");
                        resp.sendRedirect("/movie/movieseries");
                    }
                }
            } else {

                //Filmbox nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Die Filmreihe wurde nicht gefunden");
                resp.sendRedirect("/movie/movieseries");
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/movieseries");
        }
    }
}
