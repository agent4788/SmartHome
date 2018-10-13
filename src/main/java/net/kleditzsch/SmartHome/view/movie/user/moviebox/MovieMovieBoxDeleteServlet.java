package net.kleditzsch.SmartHome.view.movie.user.moviebox;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.meta.BoxMovie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieMovieBoxDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(id);
                if(movieBoxOptional.isPresent()) {

                    MovieBox movieBox = movieBoxOptional.get();

                    //Filme inkl. ggf. Cover löschen
                    for(BoxMovie boxMovie : movieBox.getBoxMovies()) {

                        Optional<Movie> movieOptional = MovieEditor.getMovie(boxMovie.getMovieId());
                        if(movieOptional.isPresent()) {

                            Movie movie = movieOptional.get();
                            MovieEditor.deleteMovie(movie.getId());

                            if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0 && !movieBox.getCoverFile().equals(movie.getCoverFile())) {

                                Path logoFile = Paths.get("upload/cover").resolve(movie.getCoverFile());
                                Files.delete(logoFile);
                            }
                        }
                    }

                    //Filmbox ggf. incl Cover löschen
                    success = MovieBoxEditor.deleteMovieBox(movieBox.getId());

                    //Cover Datei löschen
                    if(movieBox.getCoverFile() != null && movieBox.getCoverFile().length() > 0) {

                        Path logoFile = Paths.get("upload/cover").resolve(movieBox.getCoverFile());
                        Files.delete(logoFile);
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
            req.getSession().setAttribute("message", "Die Film Box wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/moviebox");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Film Box konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/moviebox");
        }
    }
}
