package net.kleditzsch.applications.contact.view.user.overview;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.contact.model.contact.Contact;
import net.kleditzsch.applications.contact.model.contact.ContactGroup;
import net.kleditzsch.applications.contact.model.contact.PhoneNumber;
import net.kleditzsch.applications.contact.model.editor.ContactEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactPhoneNumberDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String groupIdStr = req.getParameter("group");
        String contactIdStr = req.getParameter("id");
        String phoneIdStr = req.getParameter("phone");
        if(groupIdStr != null && contactIdStr != null && phoneIdStr != null) {

            try {

                ID group = ID.of(groupIdStr);
                ID contact = ID.of(contactIdStr);
                ID phone = ID.of(phoneIdStr);

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(group);
                if(contactGroupOptional.isPresent()) {

                    Optional<Contact> contactOptional = contactGroupOptional.get().getContacts().stream().filter(c -> c.getId().equals(contact)).findFirst();
                    if(contactOptional.isPresent()) {

                        Optional<PhoneNumber> phoneNumberOptional = contactOptional.get().getPhoneNumber().stream().filter(p -> p.getId().equals(phone)).findFirst();
                        if(phoneNumberOptional.isPresent()) {

                            contactOptional.get().getPhoneNumber().remove(phoneNumberOptional.get());
                            success = ContactEditor.updateContactGroup(contactGroupOptional.get());
                        } else {

                            success = false;
                        }
                    } else {

                        success = false;
                    }
                } else {

                    success = false;
                }

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Telefonnummer wurde erfolgreich gelöscht");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Telefonnummer konnte nicht gelöscht werden");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        }
    }
}
