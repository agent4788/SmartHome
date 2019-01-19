package net.kleditzsch.SmartHome.view.contact.user.overview;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.contact.contact.Contact;
import net.kleditzsch.SmartHome.model.contact.contact.ContactGroup;
import net.kleditzsch.SmartHome.model.contact.contact.CustomField;
import net.kleditzsch.SmartHome.model.contact.contact.MailAddress;
import net.kleditzsch.SmartHome.model.contact.editor.ContactEditor;
import net.kleditzsch.SmartHome.util.form.FormValidation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactCustomFieldFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        ID fieldId = null;

        ID groupId = form.getId("group", "Gruppen ID");
        ID contactId = form.getId("id", "Kontakt ID");
        if(form.fieldNotEmpty("fieldid")) {

            fieldId = form.getId("fieldid", "Feld ID");
        }
        String label = form.getString("label", "Label", 1, 20);
        String value = form.getString("value", "Wert", 3, 250);

        Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(groupId);
        if (form.isSuccessful() && contactGroupOptional.isPresent()) {

            ContactGroup contactGroup = contactGroupOptional.get();
            Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(contactId)).findFirst();
            if(contactOptional.isPresent()) {

                Contact contact = contactOptional.get();

                if(fieldId == null) {

                    //Neues Element erstellen
                    CustomField customField = new CustomField();
                    customField.setId(ID.create());
                    customField.setLabel(label);
                    customField.setValue(value);

                    contact.getCustomFields().add(customField);
                    ContactEditor.updateContactGroup(contactGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Benutzerfeld wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                } else {

                    //Element bearbeiten
                    final ID finalFieldId = fieldId;
                    Optional<CustomField> customFieldOptional = contact.getCustomFields().stream().filter(c -> c.getId().equals(finalFieldId)).findFirst();
                    if(customFieldOptional.isPresent()) {

                        CustomField customField = customFieldOptional.get();
                        customField.setLabel(label);
                        customField.setValue(value);

                        ContactEditor.updateContactGroup(contactGroup);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Benutzerfeld wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                    } else {

                        //Gruppe nicht gefunden
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die Benutzerfeld konnte nicht gefunden werden");
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
