package net.kleditzsch.applications.movie.view.user.movie;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.movie.Movie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieMovieDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<Movie> movieOptional = MovieEditor.getMovie(id);
                if(movieOptional.isPresent()) {

                    success = MovieEditor.deleteMovie(movieOptional.get().getId());

                    //Cover Datei löschen
                    if(movieOptional.get().getCoverFile() != null && movieOptional.get().getCoverFile().length() > 0) {

                        Path logoFile = Paths.get("upload/cover").resolve(movieOptional.get().getCoverFile());
                        try {

                            Files.delete(logoFile);
                        } catch (Exception e) {}
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
            resp.sendRedirect("/movie/movie");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Film konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/movie");
        }
    }
}
