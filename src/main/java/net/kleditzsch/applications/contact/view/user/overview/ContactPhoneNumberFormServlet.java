package net.kleditzsch.applications.contact.view.user.overview;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.applications.contact.model.contact.Contact;
import net.kleditzsch.applications.contact.model.contact.ContactGroup;
import net.kleditzsch.applications.contact.model.contact.PhoneNumber;
import net.kleditzsch.applications.contact.model.editor.ContactEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactPhoneNumberFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        ID phoneNumberId = null;

        ID groupId = form.getId("group", "Gruppen ID");
        ID contactId = form.getId("id", "Kontakt ID");
        if(form.fieldNotEmpty("phoneid")) {

            phoneNumberId = form.getId("phoneid", "Telefonnummer ID");
        }
        String label = form.getString("device", "Gerät", 1, 20);
        String value = form.getString("phone", "Telefonnummer", 3, 100);

        Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(groupId);
        if (form.isSuccessful() && contactGroupOptional.isPresent()) {

            ContactGroup contactGroup = contactGroupOptional.get();
            Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(contactId)).findFirst();
            if(contactOptional.isPresent()) {

                Contact contact = contactOptional.get();

                if(phoneNumberId == null) {

                    //Neues Element erstellen
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setId(ID.create());
                    phoneNumber.setDevice(label);
                    phoneNumber.setPhoneNumber(value);

                    contact.getPhoneNumber().add(phoneNumber);
                    ContactEditor.updateContactGroup(contactGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Telefonnummer wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                } else {

                    //Element bearbeiten
                    final ID finalPhoneNumberId = phoneNumberId;
                    Optional<PhoneNumber> phoneNumberOptional = contact.getPhoneNumber().stream().filter(p -> p.getId().equals(finalPhoneNumberId)).findFirst();
                    if(phoneNumberOptional.isPresent()) {

                        PhoneNumber phoneNumber = phoneNumberOptional.get();
                        phoneNumber.setDevice(label);
                        phoneNumber.setPhoneNumber(value);

                        ContactEditor.updateContactGroup(contactGroup);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Telefonnummer wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/contact/contactview?group=" + groupId.get() + "&id=" + contactId.get() + "&edit=1");
                    } else {

                        //Gruppe nicht gefunden
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die Telefonnummer konnte nicht gefunden werden");
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
