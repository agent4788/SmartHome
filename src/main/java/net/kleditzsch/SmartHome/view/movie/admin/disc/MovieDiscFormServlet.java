package net.kleditzsch.SmartHome.view.movie.admin.disc;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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

            //Disc laden
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

        //Optionale Parameter
        ID discId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            discId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        String description = form.getString("description", "Titel", 0, 250);
        Disc.Quality quality = form.getEnum("quality", "Qualität", Disc.Quality.class);

        if (form.isSuccessful()) {

            DiscEditor de = DiscEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Disc disc = new Disc();
                disc.setId(ID.create());
                disc.setName(name);
                disc.setDescription(description);
                disc.setQuality(quality);
                if (de.getData().size() > 0) {

                    int nextLevel = de.getData().stream().mapToInt(Disc::getOrderId).summaryStatistics().getMax() + 1;
                    nextLevel = nextLevel >= 0 ? nextLevel : 0;
                    disc.setOrderId(nextLevel);
                }

                de.add(disc);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Medium wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/disc");
            } else {

                //Element bearbeiten
                Optional<Disc> discOptional = de.getById(discId);
                if (discOptional.isPresent()) {

                    Disc disc = discOptional.get();
                    disc.setName(name);
                    disc.setDescription(description);
                    disc.setQuality(quality);
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
