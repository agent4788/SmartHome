package net.kleditzsch.applications.movie.view.admin.person;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.editor.PersonEditor;
import net.kleditzsch.applications.movie.model.movie.meta.Person;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MoviePersonDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                PersonEditor ae = PersonEditor.createAndLoad();

                Optional<Person> personOptional = ae.getById(id);
                if(personOptional.isPresent()) {

                    success = ae.delete(personOptional.get());
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
            req.getSession().setAttribute("message", "Die Person wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/admin/person");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Person konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/person");
        }
    }
}
