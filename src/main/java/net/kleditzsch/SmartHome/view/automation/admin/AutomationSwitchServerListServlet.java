package net.kleditzsch.SmartHome.view.automation.admin;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;
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
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationSwitchServerListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/switchserverlist.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
        ReentrantReadWriteLock.ReadLock lock = sse.readLock();
        lock.lock();

        List<SwitchServer> switchServerList = new ArrayList<>();
        switchServerList.addAll(sse.getData());

        //Beispieldaten
        SwitchServer ss = new SwitchServer(ID.create(), "Test 1", "127.0.0.1", 800, false, false);
        ss.setDescription("123456");
        switchServerList.add(ss);
        for (int i = 2; i < 127; i++)
            switchServerList.add(new SwitchServer(ID.create(), "Test " + i, "127.0.0.1", 800, false, true));
        //Beispieldaten

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter");
            filterStr = filter;
            model.with("filterStr", filterStr);
            switchServerList = switchServerList.stream().filter(e -> e.getName().contains(filter)).collect(Collectors.toList());
        }

        //sortieren
        Collections.sort(switchServerList, Comparator.comparing(Element::getName));

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

        ListPagination<SwitchServer> pagination = new ListPagination<>(switchServerList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/automation/admin/switchserverlist?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/automation/admin/switchserverlist?index=");
        }
        model.with("pagination", pagination);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }
}
