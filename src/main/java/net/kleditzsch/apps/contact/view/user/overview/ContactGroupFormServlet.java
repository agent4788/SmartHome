package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.contact.model.contact.ContactGroup;
import net.kleditzsch.apps.contact.model.editor.ContactEditor;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactGroupFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/contact/user/overview/groupform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        ContactGroup contactGroup = null;

        if(req.getParameter("id") != null) {

            addElement = false;
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(id);
                if(contactGroupOptional.isPresent()) {

                    contactGroup = contactGroupOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Gruppe wurde nicht gefunden");
            }
        } else {

            contactGroup = new ContactGroup();
            contactGroup.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("contactGroup", contactGroup);

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        ID contactGroupId = null;

        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            contactGroupId = form.getId("id", "Kontaktegruppen ID");
        }
        String name = form.getString("name", "Name", 3, 50);
        String description = form.optString("description", "Beschreibung", "", 3, 1000);

        if (form.isSuccessful()) {

            if(addElement) {

                //Neues Element erstellen
                ContactGroup contactGroup = new ContactGroup();
                contactGroup.setId(ID.create());
                contactGroup.setName(name);
                contactGroup.setDescription(description);

                ContactEditor.addContactGroup(contactGroup);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Kontaktegruppe wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/contact/index");
            } else {

                //Element bearbeiten
                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(contactGroupId);
                if(contactGroupOptional.isPresent()) {

                    ContactGroup contactGroup = contactGroupOptional.get();
                    contactGroup.setName(name);
                    contactGroup.setDescription(description);

                    ContactEditor.updateContactGroup(contactGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Kontaktegruppe wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/contact/index?");
                } else {

                    //Gruppe nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Kontaktegruppe konnte nicht gefunden werden");
                    resp.sendRedirect("/contact/index");
                }
            }
        } else {

            //Fehlerhafte Eingaben
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/contact/index");
        }
    }
}
