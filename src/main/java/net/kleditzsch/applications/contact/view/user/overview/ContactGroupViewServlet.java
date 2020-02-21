package net.kleditzsch.applications.contact.view.user.overview;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.ListPagination;
import net.kleditzsch.applications.contact.model.contact.Contact;
import net.kleditzsch.applications.contact.model.contact.ContactGroup;
import net.kleditzsch.applications.contact.model.editor.ContactEditor;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ContactGroupViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/contact/user/overview/groupview.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.CONTACT_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
        settingsLock.unlock();

        //Daten laden
        try {

            ID id = ID.of(req.getParameter("id"));
            Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(id);
            if(contactGroupOptional.isPresent()) {

                List<Contact> contacts = contactGroupOptional.get().getContacts().stream().sorted().collect(Collectors.toList());
                ListPagination<Contact> pagination = new ListPagination<>(contacts, elementsAtPage);
                model.with("pagination", pagination);
                model.with("contactGroup", contactGroupOptional.get());
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
