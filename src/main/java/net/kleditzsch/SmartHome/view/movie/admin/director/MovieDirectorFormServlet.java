package net.kleditzsch.SmartHome.view.movie.admin.director;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DirectorEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;
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

public class MovieDirectorFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/director/directorform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        DirectorEditor de = DirectorEditor.createAndLoad();
        boolean addElement = true;
        Director director = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Director> directorOptional = de.getById(id);
                if(directorOptional.isPresent()) {

                    director = directorOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Regiseur wurde nicht gefunden");
            }
        } else {

            director = new Director();
            director.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("director", director);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Optionale Parameter
        ID directorId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            directorId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        String description = form.getString("description", "Titel", 0, 250);

        if (form.isSuccessful()) {

            DirectorEditor de = DirectorEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Director director = new Director();
                director.setId(ID.create());
                director.setName(name);
                director.setDescription(description);
                de.add(director);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Regiseur wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/director");
            } else {

                //Element bearbeiten
                Optional<Director> directorOptional = de.getById(directorId);
                if (directorOptional.isPresent()) {

                    Director director = directorOptional.get();
                    director.setName(name);
                    director.setDescription(description);
                    de.update(director);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Regiseur wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/director");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Regiseur konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/director");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/director");
        }
    }
}
