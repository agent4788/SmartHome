package net.kleditzsch.SmartHome.view.movie.admin.person;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.PersonEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Person;
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

public class MoviePersonFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/person/personform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        PersonEditor ae = PersonEditor.createAndLoad();
        boolean addElement = true;
        Person person = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Person> personOptional = ae.getById(id);
                if(personOptional.isPresent()) {

                    person = personOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Person wurde nicht gefunden");
            }
        } else {

            person = new Person();
            person.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("person", person);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Optionale Parameter
        ID actorId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            actorId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        String description = form.getString("description", "Titel", 0, 250);

        if (form.isSuccessful()) {

            PersonEditor ae = PersonEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Person person = new Person();
                person.setId(ID.create());
                person.setName(name);
                person.setDescription(description);
                ae.add(person);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Person wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/person");
            } else {

                //Element bearbeiten
                Optional<Person> personOptional = ae.getById(actorId);
                if (personOptional.isPresent()) {

                    Person person = personOptional.get();
                    person.setName(name);
                    person.setDescription(description);
                    ae.update(person);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Person wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/person");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Person konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/person");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/person");
        }
    }
}
