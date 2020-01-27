package net.kleditzsch.SmartHome.view.automation.admin.device;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.automation.device.switchable.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
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

public class AutomationDeviceListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/device/devicelist.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
        ReentrantReadWriteLock.ReadLock lock = swe.readLock();
        lock.lock();

        List<Switchable> switchableList = new ArrayList<>(swe.getData());

        //filtern
        String filterStr = null;
        int filterType = -1;

        //Typ filtern
        if (req.getParameter("filtertype") != null) {

            try {

                filterType = Integer.parseInt(req.getParameter("filtertype"));
                switch (filterType) {

                    case 1:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof TPlinkSocket)
                                .collect(Collectors.toList());
                        break;
                    case 2:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof AvmSocket)
                                .collect(Collectors.toList());
                        break;
                    case 3:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof MqttSingle)
                                .collect(Collectors.toList());
                        break;
                    case 4:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof MqttDouble)
                                .collect(Collectors.toList());
                        break;
                    case 5:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof ScriptSingle)
                                .collect(Collectors.toList());
                        break;
                    case 6:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof ScriptDouble)
                                .collect(Collectors.toList());
                        break;
                    case 7:

                        switchableList = switchableList.stream()
                                .filter(e -> e instanceof WakeOnLan)
                                .collect(Collectors.toList());
                        break;
                }
            } catch (NumberFormatException e) {}
        }

        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            switchableList = switchableList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //sortieren
        switchableList.sort(Comparator.comparing(Element::getName));

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

        ListPagination<Switchable> pagination = new ListPagination<>(switchableList, elementsAtPage, index);
        if(filterStr != null && filterType > 0) {

            pagination.setBaseLink("/automation/admin/device?filter=" + HtmlEscapers.htmlEscaper() + "&filtertype=" + filterType + "&index=");
        } else if (filterStr == null && filterType > 0) {

            pagination.setBaseLink("/automation/admin/device?filtertype=" + filterType + "&index=");
        }  else if (filterStr != null && filterType == -1) {

            pagination.setBaseLink("/automation/admin/device?filter=" + HtmlEscapers.htmlEscaper() + "&index=");
        } else {

            pagination.setBaseLink("/automation/admin/device?index=");
        }
        model.with("filterType", filterType);
        model.with("filterStr", filterStr);
        model.with("pagination", pagination);

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Prüfen ob Module Aktiv aktiv
        model.with("fritzBoxAktive", Application.getInstance().getAutomation().getAvmEditor().isActive());
        model.with("mqttAktive", Application.getInstance().getAutomation().getMqttService().isActive());

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }
}
