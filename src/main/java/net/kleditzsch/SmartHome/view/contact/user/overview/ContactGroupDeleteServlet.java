package net.kleditzsch.SmartHome.view.contact.user.overview;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.contact.contact.ContactGroup;
import net.kleditzsch.SmartHome.model.contact.editor.ContactEditor;
import net.kleditzsch.SmartHome.model.network.devices.NetworkDeviceGroup;
import net.kleditzsch.SmartHome.model.network.editor.NetworkDeviceEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ContactGroupDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<ContactGroup> contactGroupOptional = ContactEditor.getContactGroupById(id);
                if(contactGroupOptional.isPresent()) {

                    success = ContactEditor.deleteContactGroup(contactGroupOptional.get().getId());
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
            req.getSession().setAttribute("message", "Die Kontaktgruppe wurde erfolgreich gelöscht");
            resp.sendRedirect("/contact/index");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Kontaktgruppe konnte nicht gelöscht werden");
            resp.sendRedirect("/contact/index");
        }
    }
}
