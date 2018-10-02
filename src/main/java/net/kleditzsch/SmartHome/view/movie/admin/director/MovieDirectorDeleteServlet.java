package net.kleditzsch.SmartHome.view.movie.admin.director;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DirectorEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieDirectorDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                DirectorEditor de = DirectorEditor.createAndLoad();

                Optional<Director> directorOptional = de.getById(id);
                if(directorOptional.isPresent()) {

                    success = de.delete(directorOptional.get());
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
            req.getSession().setAttribute("message", "Der Regiseur wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/admin/director");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Regiseur konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/director");
        }
    }
}
