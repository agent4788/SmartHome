package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.*;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.ShutterElement;
import net.kleditzsch.SmartHome.util.file.FileUtil;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationRoomShutterElementFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomshutterelementform.html");
        JtwigModel model = JtwigModel.newModel();

        //Load List of Icon Files
        try {

            List<String> iconCategorys = IconUtil.listIconCategorys();
            model.with("iconCategorys", iconCategorys.stream().sorted().collect(Collectors.toList()));

            //liste der Font Aweasome Icons
            List<String> fontAwaesomeIcons = IconUtil.listFonAweasomeIcons();
            model.with("fontAwaesomeIcons", fontAwaesomeIcons);

        } catch (URISyntaxException e) {

            throw new IOException("Laden der Dateinamen fehlgeschlagen", e);
        }

        //Daten vorbereiten
        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        boolean addElement = true;
        Room room = null;
        ShutterElement roomElement = null;

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
                    if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof ShutterElement) {

                        roomElement = (ShutterElement) roomElementOptional.get();
                    } else {

                        model.with("error", "Das Raumelement wurde nicht gefunden");
                    }
                } else {

                    // neues Element erstellen
                    roomElement = new ShutterElement();
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

        //Rollläden auflisten
        if(room != null && roomElement != null) {

            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.ReadLock actorLock = actorEditor.readLock();
            actorLock.lock();

            Map<String, Shutter> shutters = actorEditor.getData().stream()
                    .filter(e -> e instanceof Shutter)
                    .map(e -> (Shutter) e)
                    .sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()))
                    .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
            model.with("shutters", shutters);
            List<String> usedElementIds = roomElement.getShutterIds().stream().map(ID::get).collect(Collectors.toList());
            model.with("usedElementIds", usedElementIds);
            model.with("usedElementCount", usedElementIds.size());

            actorLock.unlock();
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

        FormValidation form = FormValidation.create(req);
        ID roomId = form.getId("roomid", "Raum ID");
        ID id = form.getId("id", "ID");
        boolean addElement = form.optBoolean("addElement", "neues Element", false);
        String name = form.getString("name", "Name", 3, 50);
        String displayText = form.getString("displayText", "Anzeigename", 3, 50);
        String description = form.optString("description", "Beschreibung", "", 3, 250);
        boolean disabled = form.optBoolean("disabled", "Deaktiviert", false);
        String iconFile = form.getString("iconFile", "Icon");
        List<ID> shutterIds = new ArrayList<>();
        for(int i = 0; i < 100; i++) {

            if(form.fieldExists("shutterElement_" + i)) {

                shutterIds.add(form.getId("shutterElement_" + i, "Rolladen ID - " + i));
            }
        }
        String disabledStr = req.getParameter("disabled");

        //Icon Angabe validieren
        try {

            List<String> fileNames = FileUtil.listResourceDirectoryFileNames("/webserver/static/img/iconset");
            if(!fileNames.contains(iconFile.substring(iconFile.indexOf("/") + 1))) {

                form.setInvalid("iconFile", "Icon");
            }
        } catch (URISyntaxException e) {
            form.setInvalid("iconFile", "Icon");
        }

        //Button Art ermitteln
        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
        ReentrantReadWriteLock.ReadLock switchableLock = actorEditor.readLock();
        switchableLock.lock();

        if (form.isSuccessful()) {

            RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
            lock.lock();

            Optional<Room> roomOptional = roomEditor.getById(roomId);
            if(roomOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    ShutterElement roomElement = new ShutterElement();
                    roomElement.setId(ID.create());
                    roomElement.setName(name);
                    roomElement.setDisplayText(displayText);
                    roomElement.setDescription(description);
                    roomElement.setIconFile(iconFile);
                    int nextOrderId = 0;
                    if (roomOptional.get().getRoomElements().size() > 0) {

                        nextOrderId = roomOptional.get().getRoomElements().stream().mapToInt(RoomElement::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                    }
                    roomElement.getShutterIds().addAll(shutterIds);
                    roomElement.setOrderId(nextOrderId);
                    roomElement.setDisabled(disabled);
                    roomOptional.get().getRoomElements().add(roomElement);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Rollladen wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                } else {

                    //Element bearbeiten
                    final ID finalId = id;
                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(re -> re.getId().equals(finalId))
                            .findFirst();
                    if (roomElementOptional.isPresent() && roomElementOptional.get() instanceof ShutterElement) {

                        ShutterElement roomElement = (ShutterElement) roomElementOptional.get();
                        roomElement.setId(ID.create());
                        roomElement.setName(name);
                        roomElement.setDisplayText(displayText);
                        roomElement.setDescription(description);
                        roomElement.setIconFile(iconFile);
                        roomElement.getShutterIds().clear();
                        roomElement.getShutterIds().addAll(shutterIds);
                        roomElement.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Button wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Button konnte nicht gefunden werden");
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
            resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "?dash&" : "") + "roomid=" + roomId);
        }
        switchableLock.unlock();
    }
}
