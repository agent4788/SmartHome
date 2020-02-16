package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.contact.model.contact.Contact;
import net.kleditzsch.apps.contact.model.contact.ContactGroup;
import net.kleditzsch.apps.contact.model.editor.ContactEditor;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactContactViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/contact/user/overview/contactview.html");
        JtwigModel model = JtwigModel.newModel();

        boolean edit = false;
        if(req.getParameter("edit") != null) {

            edit = true;
        }
        model.with("edit", edit);

        //Daten laden
        try {

            ID group = ID.of(req.getParameter("group"));
            ID id = ID.of(req.getParameter("id"));
            Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(group);
            if(contactGroupOptional.isPresent()) {

                ContactGroup contactGroup = contactGroupOptional.get();
                Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(id)).findFirst();
                if(contactOptional.isPresent()) {

                    model.with("contactGroup", contactGroup);
                    model.with("contact", contactOptional.get());
                } else {

                    //Kontakt nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Kontakt konnte nicht gefunden werden");
                }
            } else {

                //Kontaktgruppe nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Die Kontaktgruppe konnte nicht gefunden werden");
            }
        } catch (Exception e) {

            //Fehlerhafte ID
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte ID");
        }

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

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
}
