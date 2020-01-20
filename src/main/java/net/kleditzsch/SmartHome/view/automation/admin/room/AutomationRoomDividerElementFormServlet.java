package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.DividerElement;
import net.kleditzsch.SmartHome.util.icon.IconUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationRoomDividerElementFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomdividerelementform.html");
        JtwigModel model = JtwigModel.newModel();

        //liste der Font Aweasome Icons
        List<String> fontAwaesomeIcons = IconUtil.listFonAweasomeIcons();
        model.with("fontAwaesomeIcons", fontAwaesomeIcons);

        //Daten vorbereiten
        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        boolean addElement = true;
        Room room = null;
        DividerElement roomElement = null;

        if(req.getParameter("roomid") != null) {

            //Raum laden
            try {

                ID roomid = ID.of(req.getParameter("roomid").trim());

                Optional<Room> roomOptional = roomEditor.getById(roomid);
                if(roomOptional.isPresent()) {

                    room = roomOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

                if(req.getParameter("id") != null) {

                    //bestehendes Element laden
                    addElement = false;

                    ID id = ID.of(req.getParameter("id").trim());
                    Optional<RoomElement> roomElementOptional = room.getRoomElements().stream().filter(element -> element.getId().equals(id)).findFirst();
                    if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof DividerElement) {

                        roomElement = (DividerElement) roomElementOptional.get();
                    } else {

                        model.with("error", "Das Raumelement wurde nicht gefunden");
                    }
                } else {

                    // neues Element erstellen
                    roomElement = new DividerElement();
                    roomElement.setId(ID.create());
                }

                model.with("addElement", addElement);
                model.with("roomelement", roomElement);
                model.with("room", room);

            } catch (Exception e) {

                model.with("error", "Der Raum wurde nicht gefunden");
            }
        } else {

            model.with("error", "Der Raum wurde nicht gefunden");
        }

        lock.unlock();

        model.with("dashboard", req.getParameter("dash") != null);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean dashboard = req.getParameter("dash") != null;

        String roomStr = req.getParameter("roomid");
        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String displayText = req.getParameter("displayText");
        String description = req.getParameter("description");
        String icon = req.getParameter("icon");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = true;
        ID id = null, roomId = null;

        //Daten prüfen
        boolean success = true;
        try {

            //ID
            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(roomStr != null)) {

                success = false;
            }
            roomId = ID.of(roomStr);
            //Element hinzufügen
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            //Name
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            //Anzeigetext
            if(!(displayText != null && displayText.length() >= 3 && displayText.length() <= 50)) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //Icon
            if(!(icon != null && IconUtil.listFonAweasomeIcons().contains(icon.trim()))) {

                success = false;
            }
            //Deaktiviert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
            lock.lock();

            Optional<Room> roomOptional = roomEditor.getById(roomId);
            if(roomOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    DividerElement roomElement = new DividerElement();
                    roomElement.setId(ID.create());
                    roomElement.setName(name);
                    roomElement.setDisplayText(displayText);
                    roomElement.setDescription(description);
                    roomElement.setIcon(icon);
                    int nextOrderId = 0;
                    if (roomOptional.get().getRoomElements().size() > 0) {

                        nextOrderId = roomOptional.get().getRoomElements().stream().mapToInt(RoomElement::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                    }
                    roomElement.setOrderId(nextOrderId);
                    roomElement.setDisabled(disabled);
                    roomOptional.get().getRoomElements().add(roomElement);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Trennlinie wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") +"roomid=" + roomId.get());
                } else {

                    //Element bearbeiten
                    final ID finalId = id;
                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(re -> re.getId().equals(finalId))
                            .findFirst();
                    if (roomElementOptional.isPresent() && roomElementOptional.get() instanceof DividerElement) {

                        DividerElement roomElement = (DividerElement) roomElementOptional.get();
                        roomElement.setId(ID.create());
                        roomElement.setName(name);
                        roomElement.setDisplayText(displayText);
                        roomElement.setDescription(description);
                        roomElement.setIcon(icon);
                        roomElement.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Trennlinie wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die Trennlinie konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    }
                }
            } else {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Der Raum wurde nicht gefunden");
                resp.sendRedirect("/automation/admin/" + (dashboard ? "dashboard" : "room"));
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomStr);
        }
    }
}
