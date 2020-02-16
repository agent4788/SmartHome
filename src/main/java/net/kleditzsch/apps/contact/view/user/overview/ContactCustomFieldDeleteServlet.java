package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.contact.model.contact.Contact;
import net.kleditzsch.apps.contact.model.contact.ContactGroup;
import net.kleditzsch.apps.contact.model.contact.CustomField;
import net.kleditzsch.apps.contact.model.editor.ContactEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactCustomFieldDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String groupIdStr = req.getParameter("group");
        String contactIdStr = req.getParameter("id");
        String fieldIdStr = req.getParameter("field");
        if(groupIdStr != null && contactIdStr != null && fieldIdStr != null) {

            try {

                ID group = ID.of(groupIdStr);
                ID contact = ID.of(contactIdStr);
                ID field = ID.of(fieldIdStr);

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(group);
                if(contactGroupOptional.isPresent()) {

                    Optional<Contact> contactOptional = contactGroupOptional.get().getContacts().stream().filter(c -> c.getId().equals(contact)).findFirst();
                    if(contactOptional.isPresent()) {

                        Optional<CustomField> customFieldOptional = contactOptional.get().getCustomFields().stream().filter(c -> c.getId().equals(field)).findFirst();
                        if(customFieldOptional.isPresent()) {

                            contactOptional.get().getCustomFields().remove(customFieldOptional.get());
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
            req.getSession().setAttribute("message", "Das Benutzerfeld wurde erfolgreich gelöscht");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Benutzerfeld konnte nicht gelöscht werden");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        }
    }
}
