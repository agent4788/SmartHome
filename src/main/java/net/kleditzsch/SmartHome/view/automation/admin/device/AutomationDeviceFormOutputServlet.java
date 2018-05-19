package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Output;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationDeviceFormOutputServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/device/deviceformoutput.html");
        JtwigModel model = JtwigModel.newModel();

        SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
        ReentrantReadWriteLock.ReadLock ssLock = sse.readLock();
        ssLock.lock();

        if(sse.getData().size() > 0) {

            //Schaltserver Liste
            List<SwitchServer> switchServers = sse.getData().stream().sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName())).collect(Collectors.toList());
            model.with("switchServerList", switchServers);

            //Daten vorbereiten
            SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.ReadLock sweLock = swe.readLock();
            sweLock.lock();

            boolean addElement = true;
            Output output = null;

            if(req.getParameter("id") != null) {

                addElement = false;

                //Ausgang laden
                try {

                    ID id = ID.of(req.getParameter("id").trim());

                    Optional<Switchable> switchableOptional = swe.getById(id);
                    if(switchableOptional.isPresent() && switchableOptional.get() instanceof Output) {

                        output = (Output) switchableOptional.get();
                    } else {

                        //Element nicht gefunden
                        throw new Exception();
                    }

                } catch (Exception e) {

                    model.with("error", "Der Ausgang wurde nicht gefunden");
                }
            } else {

                output = new Output();
                output.setId(ID.create());
            }
            model.with("addElement", addElement);
            model.with("output", output);

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));

            sweLock.unlock();
        } else {

            //kein Schaltserver konfiguriert
            model.with("error", "Es ist kein Schaltserver konfiguriert");

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));
        }

        ssLock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String switchServerIdStr = req.getParameter("switchserver");
        String pinStr = req.getParameter("pin");
        String disabledStr = req.getParameter("disabled");
        String inverseStr = req.getParameter("inverse");

        //Daten vorbereiten
        int pin = 0;
        boolean addElement = true, disabled = false, inverse = false;
        ID id = null, switchServerId = null;

        //Daten prüfen
        boolean success = true;
        try {

            //ID
            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            //neues Element
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            //Name
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //Schaltserver ID
            if(addElement) {

                if(!(switchServerIdStr != null)) {

                    success = false;
                }
                switchServerId = ID.of(switchServerIdStr);
            }
            //Pin
            pin = Integer.parseInt(pinStr);
            if(!(pin >= 0 && pin <= 1000)) {

                success = false;
            }
            //Deaktiviert und Invertiert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");
            inverse = inverseStr != null && inverseStr.equalsIgnoreCase("on");

        } catch (NumberFormatException e) {

            success = false;
        }

        if (success) {

            SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = swe.writeLock();
            lock.lock();

            if(addElement) {

                //Prüfen ob Schaltserver ID existiert
                SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
                ReentrantReadWriteLock.ReadLock ssLock = sse.readLock();
                ssLock.lock();

                Optional<SwitchServer> switchServerOptional = sse.getById(switchServerId);
                if (switchServerOptional.isPresent()) {

                    //prüfen ob Steckdose schon vorhanden
                    final int finalPin = pin;
                    final ID finalId = id, finalSwitchServerId = switchServerId;
                    Optional<Output> switchableOptional = swe.getData().stream()
                            .filter(sw -> sw instanceof Output)
                            .map(sw -> (Output) sw)
                            .filter(sw -> {
                                return sw.getPin() == finalPin
                                        && sw.getSwitchServerId().equals(finalSwitchServerId)
                                        && !sw.getId().equals(finalId);
                            })
                            .findFirst();

                    if(!switchableOptional.isPresent()) {

                        //neues Element hinzufügen
                        Output output = new Output();
                        output.setId(ID.create());
                        output.setName(name);
                        output.setDescription(description);
                        output.setSwitchServerId(switchServerId);
                        output.setPin(pin);
                        output.setDisabled(disabled);
                        output.setInverse(inverse);
                        swe.getData().add(output);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Ausgang wurde erfolgreich hinzugefügt");
                        resp.sendRedirect("/automation/admin/device");

                    } else {

                        //Ausgang bereits vorhanden
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Den Ausgang gibt es bereits");
                        resp.sendRedirect("/automation/admin/device");
                    }
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Schaltserver konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/device");
                }

                ssLock.unlock();
            } else {

                //Element bearbeiten
                Optional<Switchable> switchableOptional1 = swe.getById(id);
                if(switchableOptional1.isPresent() && switchableOptional1.get() instanceof Output) {

                    Output socket = (Output) switchableOptional1.get();
                    socket.setName(name);
                    socket.setDescription(description);
                    socket.setPin(pin);
                    socket.setDisabled(disabled);
                    socket.setInverse(inverse);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Ausgang wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/automation/admin/device");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Ausgang konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/device");
                }
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/device");
        }
    }
}
