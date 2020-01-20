package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.icon.IconUtil;
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
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationRoomFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomform.html");
        JtwigModel model = JtwigModel.newModel();

        //Load List of Icon Files
        try {

            List<String> iconCategorys = IconUtil.listIconCategorys();
            model.with("iconCategorys", iconCategorys.stream().sorted().collect(Collectors.toList()));

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
        model.with("dashboard", req.getParameter("dash") != null);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean dashboard = false;

        FormValidation form = FormValidation.create(req);
        try {

            //Optionale Parameter
            ID roomId = null;

            boolean addElement = form.getBoolean("addElement", "neues Element");
            if (!addElement) {
                roomId = form.getId("id", "Raum ID");
            }
            String name = form.getString("name", "Name", 3, 50);
            String displayText = form.getString("displayText", "Anzeige Name", 3, 50);
            String description = form.getString("description", "Beschreibung", 0, 250);
            List<String> fileNames = FileUtil.listResourceDirectoryFileNames("/webserver/static/img/iconset");
            String iconFile = form.getString("iconFile", "Icon");
            if(!fileNames.contains(iconFile.substring(iconFile.indexOf('/') + 1))) {

                form.setInvalid("iconFile", "Die Icon Datei konnte nicht gefunden werden");
            }
            boolean disabled = form.optBoolean("disabled", "Deaktiviert", false);
            dashboard = form.fieldExists("dash");
            boolean defaultDashboard = form.optBoolean("defaultDashboard", "Standard Dashboard", false);

            if (form.isSuccessful()) {

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
                    if (!dashboard && roomEditor.getData().size() > 0) {

                        nextOrderId = roomEditor.getData().stream().mapToInt(Room::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                    }
                    room.setOrderId(nextOrderId);
                    room.setDisabled(disabled);
                    room.setDashboard(dashboard);
                    room.setDefaultDashboard(defaultDashboard);
                    roomEditor.getData().add(room);

                    req.getSession().setAttribute("success", true);
                    if(dashboard) {

                        req.getSession().setAttribute("message", "Das Dashboard wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/dashboard");
                    } else {

                        req.getSession().setAttribute("message", "Der Raum wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/room");
                    }
                } else {

                    //Element bearbeiten
                    Optional<Room> roomOptional = roomEditor.getById(roomId);
                    if (roomOptional.isPresent()) {

                        Room room = roomOptional.get();
                        room.setName(name);
                        room.setDisplayText(displayText);
                        room.setDescription(description);
                        room.setIconFile(iconFile);
                        room.setDisabled(disabled);
                        room.setDashboard(dashboard);
                        room.setDefaultDashboard(defaultDashboard);

                        req.getSession().setAttribute("success", true);
                        if(dashboard) {

                            req.getSession().setAttribute("message", "Das Dashboard wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/dashboard");
                        } else {

                            req.getSession().setAttribute("message", "Der Raum wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/room");
                        }
                    } else {

                        req.getSession().setAttribute("success", false);
                        if(dashboard) {

                            req.getSession().setAttribute("message", "Das Dashboard konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/dashboard");
                        } else {

                            req.getSession().setAttribute("message", "Der Raum konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/room");
                        }
                    }
                }

                lock.unlock();

            } else {

                //Eingaben n.i.O.
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                resp.sendRedirect("/automation/admin/" + (dashboard ? "dashboard" : "room"));
            }

        } catch (Exception e) {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Icondateien konnten nicht geladen werden");
            resp.sendRedirect("/automation/admin/" + (dashboard ? "dashboard" : "room"));
        }
    }
}
