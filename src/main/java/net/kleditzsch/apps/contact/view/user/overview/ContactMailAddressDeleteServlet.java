package net.kleditzsch.apps.contact.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.contact.model.contact.Contact;
import net.kleditzsch.apps.contact.model.contact.ContactGroup;
import net.kleditzsch.apps.contact.model.contact.MailAddress;
import net.kleditzsch.apps.contact.model.editor.ContactEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactMailAddressDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String groupIdStr = req.getParameter("group");
        String contactIdStr = req.getParameter("id");
        String mailIdStr = req.getParameter("mail");
        if(groupIdStr != null && contactIdStr != null && mailIdStr != null) {

            try {

                ID group = ID.of(groupIdStr);
                ID contact = ID.of(contactIdStr);
                ID mail = ID.of(mailIdStr);

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(group);
                if(contactGroupOptional.isPresent()) {

                    Optional<Contact> contactOptional = contactGroupOptional.get().getContacts().stream().filter(c -> c.getId().equals(contact)).findFirst();
                    if(contactOptional.isPresent()) {

                        Optional<MailAddress> mailAddressOptional = contactOptional.get().getMailAddress().stream().filter(m -> m.getId().equals(mail)).findFirst();
                        if(mailAddressOptional.isPresent()) {

                            contactOptional.get().getMailAddress().remove(mailAddressOptional.get());
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
            req.getSession().setAttribute("message", "Die Mailadresse wurde erfolgreich gelöscht");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Mailadresse konnte nicht gelöscht werden");
            resp.sendRedirect("/contact/contactview?group=" + groupIdStr + "&id=" + contactIdStr + "&edit=1");
        }
    }
}
