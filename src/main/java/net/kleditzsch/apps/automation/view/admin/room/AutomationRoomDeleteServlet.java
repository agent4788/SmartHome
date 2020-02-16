package net.kleditzsch.apps.automation.view.admin.room;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.room.Room;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationRoomDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        boolean dashboard = req.getParameter("dash") != null;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
            lock.lock();
            try {

                ID id = ID.of(idStr);

                Optional<Room> roomOptional = roomEditor.getById(id);
                if(roomOptional.isPresent()) {

                    success = roomEditor.delete(roomOptional.get());
                } else {

                    success = false;
                }

            } catch (Exception e) {

                success = false;
            } finally {

                lock.unlock();
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            if(req.getParameter("dash") != null) {

                req.getSession().setAttribute("message", "Das Dashboard wurde erfolgreich gelöscht");
                resp.sendRedirect("/automation/admin/dashboard");
            } else {

                req.getSession().setAttribute("message", "Der Raum wurde erfolgreich gelöscht");
                resp.sendRedirect("/automation/admin/room");
            }
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            if(dashboard) {

                req.getSession().setAttribute("message", "Das Dashboard konnte nicht gelöscht werden");
                resp.sendRedirect("/automation/admin/dashboard");
            } else {

                req.getSession().setAttribute("message", "Der Raum konnte nicht gelöscht werden");
                resp.sendRedirect("/automation/admin/room");
            }
        }
    }
}
