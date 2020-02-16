package net.kleditzsch.apps.network.view.user.overview;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.network.model.devices.NetworkDevice;
import net.kleditzsch.apps.network.model.devices.NetworkDeviceGroup;
import net.kleditzsch.apps.network.model.editor.NetworkDeviceEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class NetworkDeviceDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String groupStr = req.getParameter("group");
        String devStr = req.getParameter("devid");
        if(groupStr != null && devStr != null) {

            try {

                ID groupId = ID.of(groupStr);
                ID devId = ID.of(devStr);

                Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(groupId);
                if(networkDeviceGroupOptional.isPresent()) {

                    Optional<NetworkDevice> networkDeviceOptional = networkDeviceGroupOptional.get().getDevices().stream().filter(dev -> dev.getId().equals(devId)).findFirst();
                    if(networkDeviceOptional.isPresent()) {

                        networkDeviceGroupOptional.get().getDevices().remove(networkDeviceOptional.get());
                        success = NetworkDeviceEditor.updateNetworDeviceGroup(networkDeviceGroupOptional.get());
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
            req.getSession().setAttribute("message", "Das gerät wurde erfolgreich gelöscht");
            resp.sendRedirect("/network/index?edit=1");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Gerät konnte nicht gelöscht werden");
            resp.sendRedirect("/network/index?edit=1");
        }
    }
}
