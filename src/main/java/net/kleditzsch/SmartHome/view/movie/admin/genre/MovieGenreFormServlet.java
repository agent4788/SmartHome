package net.kleditzsch.SmartHome.view.movie.admin.genre;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.GenreEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Genre;
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

public class MovieGenreFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/genre/genreform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        GenreEditor ge = GenreEditor.createAndLoad();
        boolean addElement = true;
        Genre genre = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Genre> genreOptional = ge.getById(id);
                if(genreOptional.isPresent()) {

                    genre = genreOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Genre wurde nicht gefunden");
            }
        } else {

            genre = new Genre();
            genre.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("genre", genre);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Optionale Parameter
        ID genreId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            genreId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        String description = form.getString("description", "Titel", 0, 250);

        if (form.isSuccessful()) {

            GenreEditor ge = GenreEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                Genre genre = new Genre();
                genre.setId(ID.create());
                genre.setName(name);
                genre.setDescription(description);
                ge.add(genre);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Genre wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/genre");
            } else {

                //Element bearbeiten
                Optional<Genre> genreOptional = ge.getById(genreId);
                if (genreOptional.isPresent()) {

                    Genre genre = genreOptional.get();
                    genre.setName(name);
                    genre.setDescription(description);
                    ge.update(genre);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Genre wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/genre");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Genre konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/genre");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/genre");
        }
    }
}
