package net.kleditzsch.applications.network.view.user.overview;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.network.model.devices.NetworkDeviceGroup;
import net.kleditzsch.applications.network.model.editor.NetworkDeviceEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class NetworkGroupDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("group");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(id);
                if(networkDeviceGroupOptional.isPresent()) {

                    success = NetworkDeviceEditor.deleteNetworkDeviceGroup(networkDeviceGroupOptional.get().getId());
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
            req.getSession().setAttribute("message", "Die Gerätegruppe wurde erfolgreich gelöscht");
            resp.sendRedirect("/network/index?edit=1");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Gerätegruppe konnte nicht gelöscht werden");
            resp.sendRedirect("/network/index?edit=1");
        }
    }
}
