package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.contact.model.contact.Contact;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ContactContactFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/contact/user/overview/contactform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        ContactGroup contactGroup = null;
        Contact contact = null;

        if(req.getParameter("group") != null && req.getParameter("id") != null) {

            addElement = false;
            try {

                ID groupId = ID.of(req.getParameter("group").trim());

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(groupId);
                if(contactGroupOptional.isPresent()) {

                    contactGroup = contactGroupOptional.get();

                    ID contactId = ID.of(req.getParameter("id").trim());
                    Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(contactId)).findFirst();
                    if(contactOptional.isPresent()) {

                        contact = contactOptional.get();
                    } else {

                        model.with("error", "Kontakt nicht gefunden");
                    }
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Gruppe wurde nicht gefunden");
            }
        } else if(req.getParameter("group") != null) {

            try {

                ID groupId = ID.of(req.getParameter("group").trim());

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(groupId);
                if(contactGroupOptional.isPresent()) {

                    contactGroup = contactGroupOptional.get();

                    contact = new Contact();
                    contact.setId(ID.create());
                }
            } catch (Exception e) {

                model.with("error", "Die Gruppe wurde nicht gefunden");
            }
        } else {

            model.with("error", "Ungültige Angaben");
        }
        model.with("addElement", addElement);
        model.with("contactGroup", contactGroup);
        model.with("contact", contact);
        model.with("today", LocalDate.of(1900, 1, 1).format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

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
        ID contactId = null;

        boolean addElement = form.getBoolean("addElement", "neues Element");
        ID contactGroupId = form.getId("group", "Kontaktgruppen ID");
        if(!addElement) {
            contactId = form.getId("id", "Kontakt ID");
        }
        String name = form.getString("name", "Name", 3, 50);
        String companie = form.optString("companie", "Firma", "", 3, 50);
        String webpage = form.optString("webpage", "Webseite", "", 3, 500);
        LocalDate birthDay = form.optLocalDate("birthDay", "Webseite", LocalDate.of(1980, 1, 1), DateTimeFormatter.ofPattern("yyyy-MM-dd"), LocalDate.of(1900, 1, 1), LocalDate.now());
        String address = form.optString("address", "Adresse", "", 0, 1000);
        String notice = form.optString("notice", "Notizen", "", 0, 1000);

        if (form.isSuccessful()) {

            if(addElement) {

                //Neues Element erstellen
                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(contactGroupId);
                if(contactGroupOptional.isPresent()) {

                    Contact contact = new Contact();
                    contact.setId(ID.create());
                    contact.setName(name);
                    contact.setCompanie(companie);
                    contact.setWebpage(webpage);
                    contact.setBirthDay(birthDay);
                    contact.setAddress(address);
                    contact.setDescription(notice);

                    contactGroupOptional.get().getContacts().add(contact);
                    ContactEditor.updateContactGroup(contactGroupOptional.get());

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Kontakt wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/contact/contactview?group=" + contactGroupId.get() + "&id=" + contact.getId().get());
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Kontaktegruppe nicht gefunden");
                    resp.sendRedirect("/contact/index");
                }
            } else {

                //Element bearbeiten
                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(contactGroupId);
                if(contactGroupOptional.isPresent() && contactId != null) {

                    final ID finalContactId = contactId;
                    Optional<Contact> contactOptional = contactGroupOptional.get().getContacts().stream().filter(c -> c.getId().equals(finalContactId)).findFirst();
                    if(contactOptional.isPresent()) {

                        Contact contact = contactOptional.get();
                        contact.setName(name);
                        contact.setCompanie(companie);
                        contact.setWebpage(webpage);
                        contact.setBirthDay(birthDay);
                        contact.setAddress(address);
                        contact.setDescription(notice);

                        ContactEditor.updateContactGroup(contactGroupOptional.get());

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Kontakt wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/contact/contactview?group=" + contactGroupId.get() + "&id=" + contactId.get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Kontakt nicht gefunden");
                        resp.sendRedirect("/contact/index");
                    }
                } else {

                    //Gruppe nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Kontaktegruppe nicht gefunden");
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
