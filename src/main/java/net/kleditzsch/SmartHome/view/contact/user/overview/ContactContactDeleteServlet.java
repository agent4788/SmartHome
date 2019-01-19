package net.kleditzsch.SmartHome.view.contact.user.overview;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.contact.contact.Contact;
import net.kleditzsch.SmartHome.model.contact.contact.ContactGroup;
import net.kleditzsch.SmartHome.model.contact.contact.CustomField;
import net.kleditzsch.SmartHome.model.contact.editor.ContactEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactContactDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String groupIdStr = req.getParameter("group");
        String contactIdStr = req.getParameter("id");
        if(groupIdStr != null && contactIdStr != null) {

            try {

                ID group = ID.of(groupIdStr);
                ID contact = ID.of(contactIdStr);

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(group);
                if(contactGroupOptional.isPresent()) {

                    ContactGroup contactGroup = contactGroupOptional.get();
                    Optional<Contact> contactOptional = contactGroup.getContacts().stream().filter(c -> c.getId().equals(contact)).findFirst();
                    if(contactOptional.isPresent()) {

                        contactGroup.getContacts().remove(contactOptional.get());
                        success = ContactEditor.updateContactGroup(contactGroup);
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
            req.getSession().setAttribute("message", "Der Kontakt wurde erfolgreich gelöscht");
            resp.sendRedirect("/contact/groupview?id=" + groupIdStr);
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Kontakt konnte nicht gelöscht werden");
            resp.sendRedirect("/contact/groupview?id=" + groupIdStr);
        }
    }
}
