package net.kleditzsch.SmartHome.view.movie.user.movie;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;

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
                    Path logoFile = Paths.get("upload/cover").resolve(movieOptional.get().getCoverFile());
                    Files.delete(logoFile);
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
            req.getSession().setAttribute("message", "Der Schauspieler wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/movie");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Schauspieler konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/movie");
        }
    }
}
