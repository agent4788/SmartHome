package net.kleditzsch.apps.automation.view.admin.room;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.apps.automation.model.room.Interface.RoomElement;
import net.kleditzsch.apps.automation.model.room.Room;
import net.kleditzsch.apps.automation.model.room.element.SensorElement;
import net.kleditzsch.SmartHome.utility.file.FileUtil;
import net.kleditzsch.SmartHome.utility.icon.IconUtil;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationRoomSensorElementFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomsensorelementform.html");
        JtwigModel model = JtwigModel.newModel();

        //Load List of Icon Files
        try {

            List<String> iconCategorys = IconUtil.listIconCategorys();
            model.with("iconCategorys", iconCategorys.stream().sorted().collect(Collectors.toList()));

        } catch (URISyntaxException e) {

            throw new IOException("Laden der Dateinamen fehlgeschlagen", e);
        }

        //Daten vorbereiten
        RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        boolean addElement = true;
        Room room = null;
        SensorElement roomElement = null;

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
                    if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof SensorElement) {

                        roomElement = (SensorElement) roomElementOptional.get();
                    } else {

                        model.with("error", "Das Raumelement wurde nicht gefunden");
                    }
                } else {

                    // neues Element erstellen
                    roomElement = new SensorElement();
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

        //Schaltbare Elemente auflisten
        if(room != null && roomElement != null) {

            SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.ReadLock sensorLock = sensorEditor.readLock();
            sensorLock.lock();

            List<SensorValue> sensorValues = sensorEditor.getData().stream()
                    .filter(sv -> sv instanceof SensorValue && !(sv instanceof VirtualSensorValue))
                    .sorted(Comparator.comparing(SensorValue::getName))
                    .collect(Collectors.toList());
            model.with("sensorValues", sensorValues);

            sensorLock.unlock();
        }

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
        String iconFile = req.getParameter("iconFile");
        String firstSensorValueIdStr = req.getParameter("firstSensorValueId");
        String secondSensorValueIdStr = req.getParameter("secondSensorValueId");
        String thirdSensorValueIdStr = req.getParameter("thirdSensorValueId");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = true;
        ID id = null, roomId = null, firstSensorValueId = null;
        Optional<ID> secondSensorValueId = null, thirdSensorValueId = null;

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
            //Icon Datei
            List<String> fileNames = FileUtil.listResourceDirectoryFileNames("/webserver/static/img/iconset");
            if(!fileNames.contains(iconFile.substring(iconFile.indexOf("/") + 1))) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //Sensor IDs
            if(!(firstSensorValueIdStr != null)) {

                success = false;
            }
            firstSensorValueId = ID.of(firstSensorValueIdStr);
            if(!(secondSensorValueIdStr != null)) {

                success = false;
            }
            if(secondSensorValueIdStr.equals("empty")) {

                secondSensorValueId = Optional.empty();
            } else {

                secondSensorValueId = Optional.of(ID.of(secondSensorValueIdStr));
            }
            if(!(thirdSensorValueIdStr != null)) {

                success = false;
            }
            if(thirdSensorValueIdStr.equals("empty")) {

                thirdSensorValueId = Optional.empty();
            } else {

                thirdSensorValueId = Optional.of(ID.of(thirdSensorValueIdStr));
            }

            //Deaktiviert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
            lock.lock();

            Optional<Room> roomOptional = roomEditor.getById(roomId);
            if(roomOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    SensorElement roomElement = new SensorElement();
                    roomElement.setId(ID.create());
                    roomElement.setName(name);
                    roomElement.setDisplayText(displayText);
                    roomElement.setDescription(description);
                    roomElement.setIconFile(iconFile);
                    roomElement.setFirstSensorValueId(firstSensorValueId);
                    if(secondSensorValueId.isPresent()) {

                        roomElement.setSecondSensorValueId(secondSensorValueId.get());
                    }
                    if(thirdSensorValueId.isPresent()) {

                        roomElement.setThirdSensorValueId(thirdSensorValueId.get());
                    }
                    int nextOrderId = 0;
                    if (roomOptional.get().getRoomElements().size() > 0) {

                        nextOrderId = roomOptional.get().getRoomElements().stream().mapToInt(RoomElement::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                    }
                    roomElement.setOrderId(nextOrderId);
                    roomElement.setDisabled(disabled);
                    roomOptional.get().getRoomElements().add(roomElement);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Sensorelement wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                } else {

                    //Element bearbeiten
                    final ID finalId = id;
                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(re -> re.getId().equals(finalId))
                            .findFirst();
                    if (roomElementOptional.isPresent() && roomElementOptional.get() instanceof SensorElement) {

                        SensorElement roomElement = (SensorElement) roomElementOptional.get();
                        roomElement.setId(ID.create());
                        roomElement.setName(name);
                        roomElement.setDisplayText(displayText);
                        roomElement.setDescription(description);
                        roomElement.setIconFile(iconFile);
                        roomElement.setFirstSensorValueId(firstSensorValueId);
                        if(secondSensorValueId.isPresent()) {

                            roomElement.setSecondSensorValueId(secondSensorValueId.get());
                        } else {

                            roomElement.setSecondSensorValueId(null);
                        }
                        if(thirdSensorValueId.isPresent()) {

                            roomElement.setThirdSensorValueId(thirdSensorValueId.get());
                        } else {

                            roomElement.setThirdSensorValueId(null);
                        }
                        roomElement.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Das Sensorelement wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Das Sensorelement konnte nicht gefunden werden");
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
