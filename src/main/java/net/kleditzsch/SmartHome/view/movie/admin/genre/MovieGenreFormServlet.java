package net.kleditzsch.SmartHome.view.movie.admin.genre;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.GenreEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Genre;
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
                Optional<Genre> genreOptional = ge.getById(id);
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
