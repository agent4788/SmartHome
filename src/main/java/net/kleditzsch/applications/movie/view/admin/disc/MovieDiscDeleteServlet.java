package net.kleditzsch.applications.movie.view.admin.disc;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.editor.DiscEditor;
import net.kleditzsch.applications.movie.model.movie.meta.Disc;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieDiscDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                DiscEditor de = DiscEditor.createAndLoad();

                Optional<Disc> discOptional = de.getById(id);
                if(discOptional.isPresent()) {

                    success = de.delete(discOptional.get());
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
            req.getSession().setAttribute("message", "Das Medium wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/admin/disc");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Medium konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/disc");
        }
    }
}
