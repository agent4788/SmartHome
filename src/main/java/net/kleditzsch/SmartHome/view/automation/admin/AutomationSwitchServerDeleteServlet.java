package net.kleditzsch.SmartHome.view.automation.admin;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSwitchServerDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
                ReentrantReadWriteLock.WriteLock lock = sse.writeLock();
                lock.lock();

                Optional<SwitchServer> switchServerOptional = sse.getData().stream().filter(ss -> ss.getId().equals(id)).findFirst();
                if(switchServerOptional.isPresent()) {

                    sse.getData().remove(switchServerOptional.get());
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
            req.getSession().setAttribute("message", "Der Schaltserver wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/switchserverlist");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Schaltserver konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/switchserverlist");
        }
    }
}
