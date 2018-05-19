package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationDeviceDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
                ReentrantReadWriteLock.WriteLock lock = swe.writeLock();
                lock.lock();

                Optional<Switchable> switchServerOptional = swe.getData().stream().filter(sw -> sw.getId().equals(id)).findFirst();
                if(switchServerOptional.isPresent()) {

                    swe.getData().remove(switchServerOptional.get());
                } else {

                    success = false;
                }

                lock.unlock();

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Das Gerät wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/device");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Gerät konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/device");
        }
    }
}
