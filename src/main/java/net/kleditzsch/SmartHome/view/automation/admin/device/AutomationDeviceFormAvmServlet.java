package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmEditor;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.AvmSocket;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
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

public class AutomationDeviceFormAvmServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/device/deviceformavm.html");
        JtwigModel model = JtwigModel.newModel();

        //Prüfen ob FritzBoxSupport aktiv
        if(Application.getInstance().getAutomation().getAvmEditor().isActive()) {

            //Liste der AVM Steckdosen laden
            AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();
            ReentrantReadWriteLock.ReadLock lock = avmEditor.readLock();
            lock.lock();

            List<SmarthomeDevice> smarthomeDevices = avmEditor.getDeviceList().stream()
                    .filter(shd -> shd.isSwitchSocket())
                    .collect(Collectors.toList());
            model.with("smarthomeDevices", smarthomeDevices);

            //Daten vorbereiten
            SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.ReadLock sweLock = swe.readLock();
            sweLock.lock();

            boolean addElement = true;
            AvmSocket socket = null;

            if(req.getParameter("id") != null) {

                addElement = false;

                //Steckdose laden
                try {

                    ID id = ID.of(req.getParameter("id").trim());

                    Optional<Switchable> switchableOptional = swe.getById(id);
                    if(switchableOptional.isPresent() && switchableOptional.get() instanceof AvmSocket) {

                        socket = (AvmSocket) switchableOptional.get();
                    } else {

                        //Element nicht gefunden
                        throw new Exception();
                    }

                } catch (Exception e) {

                    model.with("error", "Die Steckdose wurde nicht gefunden");
                }
            } else {

                socket = new AvmSocket();
                socket.setId(ID.create());
            }

            //Liste der verwendeten AINs erzeugen
            List<String> usedAins = swe.getData().stream()
                    .filter(dev -> dev instanceof AvmSocket)
                    .map(dev -> ((AvmSocket) dev).getIdentifier())
                    .collect(Collectors.toList());
            model.with("usedAins", usedAins);

            sweLock.unlock();

            model.with("addElement", addElement);
            model.with("socket", socket);

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));

            lock.unlock();
        } else {

            //Fritz Box Support inaktiv
            model.with("error", "Der Fritz Box Support ist deaktiviert");

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Prüfen ob FritzBoxSupport aktiv
        if(Application.getInstance().getAutomation().getAvmEditor().isActive()) {

            String idStr = req.getParameter("id");
            String addElementStr = req.getParameter("addElement");
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String identifier = req.getParameter("identifier");
            String disabledStr = req.getParameter("disabled");
            String inverseStr = req.getParameter("inverse");

            //Daten vorbereiten
            int port = 0;
            boolean addElement = true, disabled = false, inverse = false;
            ID id = null;

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
                //Identifier
                if(addElement) {

                    if(!(identifier != null && identifier.matches("^[a-fA-F0-9:\\s]+$"))) {

                        success = false;
                    } else {

                        identifier = identifier.replaceAll("\\s", "");
                    }
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

                AvmEditor avmEditor = Application.getInstance().getAutomation().getAvmEditor();
                ReentrantReadWriteLock.ReadLock avmLock = avmEditor.readLock();
                avmLock.lock();

                //Prüfen ob Identifier bei FritzBox bekannt
                final ID finalId = id;
                final String finalIdentifier = identifier;
                Optional<AvmSocket> switchableOptional = swe.getData().stream()
                        .filter(sw -> sw instanceof AvmSocket)
                        .map(sw -> (AvmSocket) sw)
                        .filter(sw -> {
                            return sw.getIdentifier().equals(finalIdentifier)
                                    && !sw.getId().equals(finalId);
                        })
                        .findFirst();
                if(!switchableOptional.isPresent()) {

                    if(addElement) {

                        Optional<SmarthomeDevice> smarthomeDeviceOptional = avmEditor.getDeviceByIdentifier(identifier);
                        if(!smarthomeDeviceOptional.isPresent()) {

                            //Steckdose bereits vorhanden
                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Die AVM Steckdose mit dem Identifier \"" + identifier + "\" ist der FritzBox nicht bekannt");
                            resp.sendRedirect("/automation/admin/device");
                        } else {

                            //neues Element hinzufügen
                            AvmSocket socket = new AvmSocket();
                            socket.setId(ID.create());
                            socket.setName(name);
                            socket.setDescription(description);
                            socket.setIdentifier(identifier);
                            socket.setDisabled(disabled);
                            socket.setInverse(inverse);
                            swe.getData().add(socket);

                            //TODO Sensorwerte erstellen

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Die AVM Steckdose wurde erfolgreich hinzugefügt");
                            resp.sendRedirect("/automation/admin/device");
                        }
                    } else {

                        //Element bearbeiten
                        Optional<Switchable> switchableOptional1 = swe.getById(id);
                        if(switchableOptional1.isPresent() && switchableOptional1.get() instanceof AvmSocket) {

                            AvmSocket socket = (AvmSocket) switchableOptional1.get();
                            socket.setName(name);
                            socket.setDescription(description);
                            socket.setDisabled(disabled);
                            socket.setInverse(inverse);

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Die TP-Link Steckdose wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/device");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Die AVM Steckdose konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/device");
                        }
                    }
                } else {

                    //Steckdose bereits vorhanden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Es gibt bereits eine AVM Steckdose mit dem Identifier \"" + identifier + "\"");
                    resp.sendRedirect("/automation/admin/device");
                }

                avmLock.unlock();
                lock.unlock();
            } else {

                //Eingaben n.i.O.
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                resp.sendRedirect("/automation/admin/device");
            }
        } else {

            //Fritz Box Support inaktiv
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der FritzBox Support ist deaktiviert");
            resp.sendRedirect("/automation/admin/device");
        }
    }
}
