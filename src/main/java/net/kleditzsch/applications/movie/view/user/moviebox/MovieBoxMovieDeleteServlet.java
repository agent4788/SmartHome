package net.kleditzsch.applications.movie.view.user.moviebox;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.editor.MovieBoxEditor;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.movie.Movie;
import net.kleditzsch.applications.movie.model.movie.MovieBox;
import net.kleditzsch.applications.movie.model.movie.meta.BoxMovie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieBoxMovieDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        ID movieBoxId = null;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<Movie> movieOptional = MovieEditor.getMovie(id);
                if(movieOptional.isPresent()) {

                    Movie movie = movieOptional.get();

                    //Film aus der Box löschen
                    if(movie.getBoxId().isPresent()) {

                        Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(movie.getBoxId().get());
                        if(movieBoxOptional.isPresent()) {

                            //Film aus Box entfernen
                            MovieBox movieBox = movieBoxOptional.get();
                            movieBoxId = movieBox.getId();
                            Optional<BoxMovie> boxMovieOptional = movieBox.getBoxMovies().stream()
                                    .filter(boxMovie -> boxMovie.getMovieId().equals(movie.getId()))
                                    .findFirst();
                            boxMovieOptional.ifPresent(boxMovie -> movieBox.getBoxMovies().remove(boxMovie));

                            //Filmbox aktualisieren
                            if(MovieBoxEditor.updateMovieBox(movieBox)) {

                                //Film löschen
                                success = MovieEditor.deleteMovie(movie.getId());

                                //Cover Datei löschen (wenn der Film ein eigenes Cover hatte)
                                if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0 && !movieBox.getCoverFile().equals(movie.getCoverFile())) {

                                    Path logoFile = Paths.get("upload/cover").resolve(movie.getCoverFile());
                                    try {

                                        Files.delete(logoFile);
                                    } catch (Exception e) {}
                                }
                            } else {

                                success = false;
                            }
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
            if(movieBoxId != null) {

                resp.sendRedirect("/movie/movieboxview?edit=1&id=" + movieBoxId.get());
            }
            resp.sendRedirect("/movie/moviebox");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Film konnte nicht gelöscht werden");
            if(movieBoxId != null) {

                resp.sendRedirect("/movie/movieboxview?edit=1id=" + movieBoxId.get());
            }
            resp.sendRedirect("/movie/moviebox");
        }
    }
}
