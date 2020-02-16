package net.kleditzsch.apps.automation.view.admin.sensorvalues;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.Element;
import net.kleditzsch.apps.automation.model.device.sensor.*;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.virtual.*;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.ListPagination;
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

public class AutomationSensorValuesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvalueslist.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
        lock.lock();

        List<SensorValue> sensorValuesList = new ArrayList<>(sensorEditor.getData());

        //filtern
        String filterStr = null;
        int filterType = -1;

        //Typ filtern
        if (req.getParameter("filtertype") != null) {

            try {

                filterType = Integer.parseInt(req.getParameter("filtertype"));
                switch (filterType) {

                    case 1:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof ActualPowerValue)
                                .collect(Collectors.toList());
                        break;
                    case 2:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof AirPressureValue)
                                .collect(Collectors.toList());
                        break;
                    case 3:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof AltitudeValue)
                                .collect(Collectors.toList());
                        break;
                    case 4:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof BatteryLevelValue)
                                .collect(Collectors.toList());
                        break;
                    case 5:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof BiStateValue)
                                .collect(Collectors.toList());
                        break;
                    case 6:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof CounterValue)
                                .collect(Collectors.toList());
                        break;
                    case 7:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof CurrentValue)
                                .collect(Collectors.toList());
                        break;
                    case 8:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof DistanceValue)
                                .collect(Collectors.toList());
                        break;
                    case 9:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof DurationValue)
                                .collect(Collectors.toList());
                        break;
                    case 10:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof EnergyValue)
                                .collect(Collectors.toList());
                        break;
                    case 11:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof GasAmountValue)
                                .collect(Collectors.toList());
                        break;
                    case 12:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof HumidityValue)
                                .collect(Collectors.toList());
                        break;
                    case 13:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof InputValue)
                                .collect(Collectors.toList());
                        break;
                    case 14:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof LightIntensityValue)
                                .collect(Collectors.toList());
                        break;
                    case 15:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof LiveBitValue)
                                .collect(Collectors.toList());
                        break;
                    case 16:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof MoistureValue)
                                .collect(Collectors.toList());
                        break;
                    case 17:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof StringValue)
                                .collect(Collectors.toList());
                        break;
                    case 18:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof TemperatureValue)
                                .collect(Collectors.toList());
                        break;
                    case 19:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof UserAtHomeValue)
                                .collect(Collectors.toList());
                        break;
                    case 20:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VoltageValue)
                                .collect(Collectors.toList());
                        break;
                    case 21:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof WaterAmountValue)
                                .collect(Collectors.toList());
                        break;
                    case 101:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualActualPowerValue)
                                .collect(Collectors.toList());
                        break;
                    case 102:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualEnergyValue)
                                .collect(Collectors.toList());
                        break;
                    case 103:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualGasAmountValue)
                                .collect(Collectors.toList());
                        break;
                    case 104:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualLightIntensityValue)
                                .collect(Collectors.toList());
                        break;
                    case 105:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualTemperatureValue)
                                .collect(Collectors.toList());
                        break;
                    case 106:

                        sensorValuesList = sensorValuesList.stream()
                                .filter(e -> e instanceof VirtualWaterAmountValue)
                                .collect(Collectors.toList());
                        break;
                }
            } catch (NumberFormatException e) {}
        }

        //Name filtern
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            sensorValuesList = sensorValuesList.stream()
                    .filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList());
        }

        //sortieren
        sensorValuesList.sort(Comparator.comparing(Element::getName));

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        ListPagination<SensorValue> pagination = new ListPagination<>(sensorValuesList, elementsAtPage, index);
        if(filterStr != null && filterType > 0) {

            pagination.setBaseLink("/automation/admin/sensorvalues?filter=" + HtmlEscapers.htmlEscaper() + "&filtertype=" + filterType + "&index=");
        } else if (filterStr == null && filterType > 0) {

            pagination.setBaseLink("/automation/admin/sensorvalues?filtertype=" + filterType + "&index=");
        }  else if (filterStr != null && filterType == -1) {

            pagination.setBaseLink("/automation/admin/sensorvalues?filter=" + HtmlEscapers.htmlEscaper() + "&index=");
        } else {

            pagination.setBaseLink("/automation/admin/sensorvalues?index=");
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

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }
}
