package net.kleditzsch.SmartHome.view.movie.admin.disc;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
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

public class MovieDiscFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/disc/discform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        DiscEditor de = DiscEditor.createAndLoad();
        boolean addElement = true;
        Disc disc = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Disc> discOptional = de.getById(id);
                if(discOptional.isPresent()) {

                    disc = discOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Medium wurde nicht gefunden");
            }
        } else {

            disc = new Disc();
            disc.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("disc", disc);

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

            DiscEditor de = DiscEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Disc disc = new Disc();
                disc.setId(ID.create());
                disc.setName(name);
                disc.setDescription(description);
                de.add(disc);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Medium wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/disc");
            } else {

                //Element bearbeiten
                Optional<Disc> discOptional = de.getById(id);
                if (discOptional.isPresent()) {

                    Disc disc = discOptional.get();
                    disc.setName(name);
                    disc.setDescription(description);
                    de.update(disc);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Medium wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/disc");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Medium konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/director");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/disc");
        }
    }
}
