package net.kleditzsch.SmartHome.view.automation.admin.timer;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationTimerDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                SwitchTimerEditor switchTimerEditor = Application.getInstance().getAutomation().getSwitchTimerEditor();
                ReentrantReadWriteLock.WriteLock lock = switchTimerEditor.writeLock();
                lock.lock();

                Optional<SwitchTimer> switchTimerOptional = switchTimerEditor.getById(id);
                if(switchTimerOptional.isPresent()) {

                    success = switchTimerEditor.delete(switchTimerOptional.get());
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
            req.getSession().setAttribute("message", "Der Timer wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/timer");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Timer konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/timer");
        }
    }
}
