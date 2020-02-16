package net.kleditzsch.apps.automation.view.admin.room;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.room.Interface.RoomElement;
import net.kleditzsch.apps.automation.model.room.Room;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationRoomElementDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        boolean dashboard = req.getParameter("dash") != null;

        String idStr = req.getParameter("id");
        String roomIdStr = req.getParameter("roomid");
        if(idStr != null && roomIdStr != null) {

            try {

                ID id = ID.of(idStr);
                ID roomId = ID.of(roomIdStr);
                RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
                ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
                lock.lock();

                Optional<Room> roomOptional = roomEditor.getById(roomId);
                if(roomOptional.isPresent()) {

                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(re -> re.getId().equals(id))
                            .findFirst();
                    if(roomElementOptional.isPresent()) {

                        roomOptional.get().getRoomElements().remove(roomElementOptional.get());
                    } else {

                        success = false;
                    }
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
            req.getSession().setAttribute("message", "Das Raumelement wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomIdStr);
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Raumelement konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/" + (dashboard ? "dashboard" : "room"));
        }
    }
}
