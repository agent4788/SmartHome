package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.util.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationRoomElementOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null && req.getParameter("roomid") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));
                ID roomId = ID.of(req.getParameter("roomid"));

                //Sortierungen anpassen
                RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
                ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
                lock.lock();

                Optional<Room> roomOptional = roomEditor.getById(roomId);
                if(roomOptional.isPresent()) {

                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream().filter(re -> re.getId().equals(id)).findFirst();
                    if(roomElementOptional.isPresent()) {


                        List<RoomElement> roomElements = roomOptional.get().getRoomElements().stream()
                                .sorted(Comparator.comparingInt(RoomElement::getOrderId))
                                .collect(Collectors.toList());
                        int currentIndex = roomElements.indexOf(roomElementOptional.get());
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
                            CollectionUtil.moveItem(currentIndex, newIndex, roomElements);
                            int orderId = 0;
                            for (RoomElement roomElement : roomElements) {

                                Optional<RoomElement> roomElementOptional1 = roomOptional.get().getRoomElements().stream()
                                        .filter(re -> re.getId().equals(roomElement.getId()))
                                        .findFirst();
                                if (roomElementOptional1.isPresent()) {

                                    roomElementOptional1.get().setOrderId(orderId);
                                    orderId++;
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/roomelements?roomid=" + roomId.get());
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Falscher Index");
                            resp.sendRedirect("/automation/admin/roomelements?roomid=" + roomId.get());
                        }
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Das Raumelement konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/roomelements?roomid=" + roomId.get());
                    }
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Raum wurde nicht gefunden");
                    resp.sendRedirect("/automation/admin/room");
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
