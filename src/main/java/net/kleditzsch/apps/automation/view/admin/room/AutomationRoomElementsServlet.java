package net.kleditzsch.apps.automation.view.admin.room;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.ListPagination;
import net.kleditzsch.apps.automation.model.editor.RoomEditor;
import net.kleditzsch.apps.automation.model.room.Interface.RoomElement;
import net.kleditzsch.apps.automation.model.room.Room;
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

public class AutomationRoomElementsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roomelements.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        try {

            if(req.getParameter("roomid") != null) {

                ID id = ID.of(req.getParameter("roomid").trim());

                Optional<Room> switchServerOptional = roomEditor.getById(id);
                if(switchServerOptional.isPresent()) {

                    Room room = switchServerOptional.get();
                    model.with("room", room);

                    List<RoomElement> roomElements = new ArrayList<>(room.getRoomElements());

                    //filtern
                    String filterStr = null;
                    if(req.getParameter("filter") != null) {

                        String filter = req.getParameter("filter").trim();
                        filterStr = filter;
                        model.with("filterStr", filterStr);
                        roomElements = roomElements.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
                    }

                    //sortieren
                    roomElements.sort(Comparator.comparing(RoomElement::getOrderId));

                    //Blätterfunktion
                    int index = 0;
                    if (req.getParameter("index") != null) {

                        index = Integer.parseInt(req.getParameter("index"));
                    }

                    int elementsAtPage = 25;
                    SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
                    ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                    settingsLock.lock();
                    elementsAtPage = settingsEditor.getIntegerSetting(Settings.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE).getValue();
                    settingsLock.unlock();

                    ListPagination<RoomElement> pagination = new ListPagination<>(roomElements, elementsAtPage, index);
                    if(filterStr != null) {

                        pagination.setBaseLink("/automation/admin/roomelements?roomid=" + HtmlEscapers.htmlEscaper().escape(room.getId().get()) + "&filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
                    } else {

                        pagination.setBaseLink("/automation/admin/roomelements?roomid=" + HtmlEscapers.htmlEscaper().escape(room.getId().get()) + "&index=");
                    }
                    model.with("pagination", pagination);
                    model.with("maxOrderId", roomElements.stream().mapToInt(RoomElement::getOrderId).summaryStatistics().getMax());
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }
            }
        } catch (Exception e) {

            model.with("success", false);
            model.with("message", "Der Raum wurde nicht gefunden");
        }

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        model.with("dashboard", req.getParameter("dash") != null);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }
}
