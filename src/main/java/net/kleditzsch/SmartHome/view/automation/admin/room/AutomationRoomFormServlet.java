package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.util.iconutil.IconUtil;
import net.kleditzsch.SmartHome.util.file.FileUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationRoomFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomform.html");
        JtwigModel model = JtwigModel.newModel();

        //Load List of Icon Files
        try {

            Map<String, String> fileNamesMap = IconUtil.listIconFiles();
            model.with("fileNames", fileNamesMap);

        } catch (URISyntaxException e) {

            throw new IOException("Laden der Dateinamen fehlgeschlagen", e);
        }

        //Daten vorbereiten
        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        boolean addElement = true;
        Room room = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Raum laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Room> switchServerOptional = roomEditor.getById(id);
                if(switchServerOptional.isPresent()) {

                    room = switchServerOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Raum wurde nicht gefunden");
            }
        } else {

            room = new Room();
            room.setId(ID.create());

        }
        model.with("addElement", addElement);
        model.with("room", room);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String displayText = req.getParameter("displayText");
        String description = req.getParameter("description");
        String iconFile = req.getParameter("iconFile");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = true;
        ID id = null;

        //Daten prüfen
        boolean success = true;
        try {

            //ID
            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
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
            //ANzeigetext
            if(!(displayText != null && displayText.length() >= 3 && displayText.length() <= 50)) {

                success = false;
            }
            //Icon Datei
            List<String> fileNames = FileUtil.listResourceFolderFileNames("/webserver/static/img/iconset");
            if(!fileNames.contains(iconFile)) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

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

            if(addElement) {

                //neues Element hinzufügen
                Room room = new Room();
                room.setId(ID.create());
                room.setName(name);
                room.setDisplayText(displayText);
                room.setDescription(description);
                room.setIconFile(iconFile);
                int nextOrderId = 0;
                if (roomEditor.getData().size() > 0) {

                    nextOrderId = roomEditor.getData().stream().mapToInt(Room::getOrderId).summaryStatistics().getMax() + 1;
                }
                room.setOrderId(nextOrderId);
                room.setDisabled(disabled);
                roomEditor.getData().add(room);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Raum wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/automation/admin/room");
            } else {

                //Element bearbeiten
                Optional<Room> roomOptional = roomEditor.getById(id);
                if (roomOptional.isPresent()) {

                    Room room = roomOptional.get();
                    room.setName(name);
                    room.setDisplayText(displayText);
                    room.setDescription(description);
                    room.setIconFile(iconFile);
                    room.setDisabled(disabled);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Raum wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/automation/admin/room");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Raum konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/room");
                }
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/room");
        }
    }
}
