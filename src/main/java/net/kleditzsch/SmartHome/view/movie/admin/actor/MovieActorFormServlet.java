package net.kleditzsch.SmartHome.view.movie.admin.actor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class MovieActorFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/actor/actorform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        ActorEditor ae = ActorEditor.createAndLoad();
        boolean addElement = true;
        Actor actor = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Actor> actorOptional = ae.getById(id);
                if(actorOptional.isPresent()) {

                    actor = actorOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Schauspieler wurde nicht gefunden");
            }
        } else {

            actor = new Actor();
            actor.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("actor", actor);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String description = req.getParameter("description");

        //Daten vorbereiten
        boolean addElement = true;
        ID id = null;

        //Daten prüfen
        boolean success = true;
        try {

            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            ActorEditor ae = ActorEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Actor actor = new Actor();
                actor.setId(ID.create());
                actor.setName(name);
                actor.setDescription(description);
                ae.add(actor);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Schauspieler wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/actor");
            } else {

                //Element bearbeiten
                Optional<Actor> actorOptional = ae.getById(id);
                if (actorOptional.isPresent()) {

                    Actor actor = actorOptional.get();
                    actor.setName(name);
                    actor.setDescription(description);
                    ae.update(actor);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Schauspieler wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/actor");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Schauspieler konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/actor");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/actor");
        }
    }
}
