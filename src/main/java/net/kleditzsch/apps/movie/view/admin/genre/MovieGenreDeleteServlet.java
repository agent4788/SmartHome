package net.kleditzsch.apps.movie.view.admin.genre;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.GenreEditor;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieGenreDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                GenreEditor ge = GenreEditor.createAndLoad();

                Optional<Genre> genreOptional = ge.getById(id);
                if(genreOptional.isPresent()) {

                    success = ge.delete(genreOptional.get());
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
            req.getSession().setAttribute("message", "Das Genre wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/admin/genre");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Genre konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/genre");
        }
    }
}
