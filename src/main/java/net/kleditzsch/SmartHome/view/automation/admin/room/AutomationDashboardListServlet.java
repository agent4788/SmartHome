package net.kleditzsch.SmartHome.view.automation.admin.room;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

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

public class AutomationDashboardListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/dashboardlist.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        List<Room> roomList = new ArrayList<>(roomEditor.listDashboards());

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            roomList = roomList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //sortieren
        roomList.sort(Comparator.comparing(Room::getOrderId));

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        ListPagination<Room> pagination = new ListPagination<>(roomList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/automation/admin/dashboard?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/automation/admin/dashboard?index=");
        }
        model.with("pagination", pagination);

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }
}
