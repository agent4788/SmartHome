package net.kleditzsch.apps.automation.view.admin.room;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.room.Room;
import net.kleditzsch.SmartHome.utility.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationRoomOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));

                //Sortierungen anpassen
                RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
                ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
                lock.lock();

                Optional<Room> roomOptional = roomEditor.getById(id);
                if(roomOptional.isPresent()) {

                    List<Room> rooms = new ArrayList<>(roomEditor.getRoomsSorted());
                    int currentIndex = rooms.indexOf(roomOptional.get());
                    int newIndex = 0;
                    if(moveUp) {

                        //nach oben verschieben
                        newIndex = currentIndex -1;
                    } else {

                        //nach unten verschieben
                        newIndex = currentIndex + 1;
                    }
                    if(newIndex >= 0 && newIndex <= 10_000) {

                        //neu sortieren und Speichern;
                        CollectionUtil.moveItem(currentIndex, newIndex, rooms);
                        int orderId = 0;
                        for (Room room : rooms) {

                            Optional<Room> roomOptional1 = roomEditor.getById(room.getId());
                            if (roomOptional1.isPresent()) {

                                roomOptional1.get().setOrderId(orderId);
                                orderId++;
                            }
                        }

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/room");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Falscher Index");
                        resp.sendRedirect("/automation/admin/room");
                    }
                }

                lock.unlock();

            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/automation/admin/room");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/automation/admin/room");
        }
    }
}
