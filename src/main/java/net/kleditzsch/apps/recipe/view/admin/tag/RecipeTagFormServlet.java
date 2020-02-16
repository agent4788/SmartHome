package net.kleditzsch.apps.recipe.view.admin.tag;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.recipe.model.editor.TagEditor;
import net.kleditzsch.apps.recipe.model.recipe.Tag;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeTagFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/admin/tag/tagform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        boolean addElement = true;
        Tag tag = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Tag> tagOptional = TagEditor.getTagById(id);
                if(tagOptional.isPresent()) {

                    tag = tagOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Tag wurde nicht gefunden");
            }
        } else {

            tag = new Tag();
            tag.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("tag", tag);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Optionale Parameter
        ID tagId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            tagId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        Tag.Color color = form.getEnum("color", "Farbe", Tag.Color.class);

        if (form.isSuccessful()) {

            if(addElement) {

                //neues Element hinzufügen
                Tag tag = new Tag();
                tag.setId(ID.create());
                tag.setName(name);
                tag.setColor(color);

                TagEditor.addTag(tag);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Tag wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/recipe/admin/tag");
            } else {

                //Element bearbeiten
                Optional<Tag> tagOptional = TagEditor.getTagById(tagId);
                if (tagOptional.isPresent()) {

                    Tag tag = tagOptional.get();
                    tag.setName(name);
                    tag.setColor(color);

                    TagEditor.updateTag(tag);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Medium wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/recipe/admin/tag");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Medium konnte nicht gefunden werden");
                    resp.sendRedirect("/recipe/admin/tag");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/recipe/admin/tag");
        }
    }
}
