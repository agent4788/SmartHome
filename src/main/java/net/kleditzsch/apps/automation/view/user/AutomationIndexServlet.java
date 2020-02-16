package net.kleditzsch.apps.automation.view.user;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.apps.automation.model.room.Room;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationIndexServlet extends HttpServlet {

    public final String DASHBOARD_COOKIE_NAME = "smarthome_dash";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        List<Room> rooms = roomEditor.getRoomsSorted().stream().filter(room -> !room.isDisabled()).collect(Collectors.toList());
        model.with("rooms", rooms);

        List<Room> dashboards = roomEditor.listDashboards().stream().filter(e -> !e.isDisabled()).collect(Collectors.toList());
        model.with("dashboards", dashboards);

        //aktives Dashboard ermitteln
        Room activeDashboard = null;
        Optional<Cookie> cookieOptional;
        if(req.getCookies() != null) {

            cookieOptional = Arrays.asList(req.getCookies()).stream().filter(c -> c.getName().equalsIgnoreCase(DASHBOARD_COOKIE_NAME)).findFirst();
        } else {

            cookieOptional = Optional.empty();
        }
        if(req.getParameter("id") != null) {

            //über ID Parameter (höchste Priorität)
            try {

                ID id = ID.of(req.getParameter("id"));
                Optional<Room> dashboardOptional = roomEditor.getById(id);
                if(dashboardOptional.isPresent() && dashboardOptional.get().isDashboard()) {

                    activeDashboard = dashboardOptional.get();
                }
            } catch (Exception e) {}

            //gewähltes Dashboard über Cookie speichern
            if(activeDashboard != null) {

                Cookie cookie = new Cookie(DASHBOARD_COOKIE_NAME, activeDashboard.getId().get());
                cookie.setMaxAge(365 * 24 * 60 * 60);
                resp.addCookie(cookie);
            }
        } else if (cookieOptional.isPresent()) {

            //über Cookie (Prio 2.)
            try {

                ID id = ID.of(cookieOptional.get().getValue());
                Optional<Room> dashboardOptional = roomEditor.getById(id);
                if(dashboardOptional.isPresent() && dashboardOptional.get().isDashboard()) {

                    activeDashboard = dashboardOptional.get();
                }
            } catch (Exception e) {}
        }

        //Wenn kein Dashboard geladen, prüfen ob ein Standard Dashboard verfügbar ist
        if(activeDashboard == null) {

            Optional<Room> defaultDashboardOptional = roomEditor.getDefaultDashboard();
            if(defaultDashboardOptional.isPresent() && defaultDashboardOptional.get().isDashboard()) {

                activeDashboard = defaultDashboardOptional.get();
            }
        }

        lock.unlock();

        if(activeDashboard != null && !activeDashboard.isDisabled()) {

            model.with("activeDashboard", activeDashboard);
        } else if(activeDashboard.isDisabled()) {

            model.with("success", false);
            model.with("message", "Das Dashboard ist deaktiviert!");
        } else {

            model.with("success", false);
            model.with("message", "Kein Dashboard konfiguriert!");
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock sensorLock = sensorEditor.readLock();
        sensorLock.lock();

        model.with("sensorEditor", sensorEditor);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        sensorLock.unlock();
    }
}
