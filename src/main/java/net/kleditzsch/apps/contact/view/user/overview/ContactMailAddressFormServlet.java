package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.apps.contact.model.contact.Contact;
import net.kleditzsch.apps.contact.model.contact.ContactGroup;
import net.kleditzsch.apps.contact.model.contact.MailAddress;
import net.kleditzsch.apps.contact.model.editor.ContactEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactMailAddressFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        ID mailId = null;

        ID groupId = form.getId("group", "Gruppen ID");
        ID contactId = form.getId("id", "Kontakt ID");
        if(form.fieldNotEmpty("mailid")) {

            mailId = form.getId("mailid", "Telefonnummer ID");
        }
        String label = form.getString("label", "Label", 1, 20);
        String value = form.getString("mail", "Mailadresse", 3, 100);

        Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(groupId);
        if (form.isSuccessful() && contactGroupOptional.isPresent()) {

            ContactGroup contactGroup = contactGroupOptional.get();
            Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(contactId)).findFirst();
            if(contactOptional.isPresent()) {

                Contact contact = contactOptional.get();

                if(mailId == null) {

                    //Neues Element erstellen
                    MailAddress mailAddress = new MailAddress();
                    mailAddress.setId(ID.create());
                    mailAddress.setLabel(label);
                    mailAddress.setMailAddress(value);

                    contact.getMailAddress().add(mailAddress);
                    ContactEditor.updateContactGroup(contactGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Mailadresse wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                } else {

                    //Element bearbeiten
                    final ID finalMailId = mailId;
                    Optional<MailAddress> mailAddressOptional = contact.getMailAddress().stream().filter(m -> m.getId().equals(finalMailId)).findFirst();
                    if(mailAddressOptional.isPresent()) {

                        MailAddress mailAddress = mailAddressOptional.get();
                        mailAddress.setLabel(label);
                        mailAddress.setMailAddress(value);

                        ContactEditor.updateContactGroup(contactGroup);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Mailadresse wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                    } else {

                        //Gruppe nicht gefunden
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die Mailadresse konnte nicht gefunden werden");
                        resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                    }
                }
            } else {

                //Kontakt nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Der Kontakt wurde nicht gefunden");
                resp.sendRedirect("/contact/index");
            }
        } else {

            //Fehlerhafte Eingaben
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/contact/index");
        }
    }
}
