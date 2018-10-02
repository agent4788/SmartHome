package net.kleditzsch.SmartHome.view.movie.admin.actor;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieActorDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                ActorEditor ae = ActorEditor.createAndLoad();

                Optional<Actor> actorOptional = ae.getById(id);
                if(actorOptional.isPresent()) {

                    success = ae.delete(actorOptional.get());
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
            resp.sendRedirect("/movie/admin/actor");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Schauspieler konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/actor");
        }
    }
}
