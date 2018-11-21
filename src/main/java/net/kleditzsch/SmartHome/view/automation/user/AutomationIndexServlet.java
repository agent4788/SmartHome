package net.kleditzsch.SmartHome.view.automation.user;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        List<Room> rooms = roomEditor.getRoomsSorted();
        model.with("rooms", rooms);

        //aktiven Raum ermitteln
        Room activeRoom = null;
        if(req.getParameter("id") != null) {

            try {

                ID id = ID.of(req.getParameter("id"));
                Optional<Room> roomOptional = roomEditor.getById(id);
                if(roomOptional.isPresent()) {

                    activeRoom = roomOptional.get();
                }
            } catch (Exception e) {}
        }

        if(activeRoom == null && rooms.size() >= 1) {

            activeRoom = rooms.get(0);
        } else if (activeRoom == null) {

            model.with("success", false);
            model.with("message", "Der Raum wurde nicht gefunden oder es ist kein Raum Konfiguriert");
        }
        model.with("activeRoom", activeRoom);

        lock.unlock();

        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock sensorLock = sensorEditor.readLock();
        sensorLock.lock();

        model.with("sensorEditor", sensorEditor);

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        sensorLock.unlock();
    }
}
