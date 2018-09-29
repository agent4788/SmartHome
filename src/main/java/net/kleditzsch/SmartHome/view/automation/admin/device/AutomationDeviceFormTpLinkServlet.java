package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.ActualPowerValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.CurrentValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.EnergyValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.VoltageValue;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationDeviceFormTpLinkServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/device/deviceformtplink.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
        boolean addElement = true;
        TPlinkSocket socket = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Steckdose laden
            ReentrantReadWriteLock.ReadLock lock = swe.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<Switchable> switchableOptional = swe.getById(id);
                if(switchableOptional.isPresent() && switchableOptional.get() instanceof TPlinkSocket) {

                    socket = (TPlinkSocket) switchableOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Steckdose wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            socket = new TPlinkSocket();
            socket.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("socket", socket);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String ipAddress = req.getParameter("ipAddress");
        String portStr = req.getParameter("port");
        String typeStr = req.getParameter("type");
        String disabledStr = req.getParameter("disabled");
        String inverseStr = req.getParameter("inverse");

        //Daten vorbereiten
        int port = 0;
        boolean addElement = true, disabled = false, inverse = false;
        ID id = null;
        TPlinkSocket.SOCKET_TYPE type = null;

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
            //IP Adresse
            if(!(ipAddress != null)) {

                success = false;
            } else {

                InetAddress.getByName(ipAddress);
            }
            //Port
            port = Integer.parseInt(portStr);
            if(!(port >= 0 && port <= 65_535)) {

                success = false;
            }
            //Typ (nur beim erstellen)
            if(addElement) {

                type = TPlinkSocket.SOCKET_TYPE.valueOf(typeStr);
            }
            //Deaktiviert und Invertiert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");
            inverse = inverseStr != null && inverseStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = swe.writeLock();
            lock.lock();

            //prüfen ob Steckdose schon vorhanden
            final int finalPort = port;
            final ID finalId = id;
            Optional<TPlinkSocket> switchableOptional = swe.getData().stream()
                    .filter(sw -> sw instanceof TPlinkSocket)
                    .map(sw -> (TPlinkSocket) sw)
                    .filter(sw -> {
                        return sw.getIpAddress().equals(ipAddress)
                                && sw.getPort() == finalPort
                                && !sw.getId().equals(finalId);
                    })
                    .findFirst();
            if(!switchableOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    TPlinkSocket socket = new TPlinkSocket();
                    socket.setId(ID.create());
                    socket.setName(name);
                    socket.setDescription(description);
                    socket.setIpAddress(ipAddress);
                    socket.setPort(port);
                    socket.setSocketType(type);
                    socket.setDisabled(disabled);
                    socket.setInverse(inverse);

                    //bei HS110 Sensorwerte erstellen
                    if(type == TPlinkSocket.SOCKET_TYPE.HS110) {

                        VoltageValue voltageValue = new VoltageValue(ID.create(), ID.create().get(), name);
                        voltageValue.setSystemValue(true);
                        voltageValue.setDescription("TP-Link Spannungs Sensor - " + name + " - IP " + ipAddress + ":" + port);
                        CurrentValue currentValue = new CurrentValue(ID.create(), ID.create().get(), name);
                        currentValue.setSystemValue(true);
                        currentValue.setDescription("TP-Link Strom Sensor - " + name + " - IP " + ipAddress + ":" + port);
                        ActualPowerValue actualPowerValue = new ActualPowerValue(ID.create(), ID.create().get(), name);
                        actualPowerValue.setSystemValue(true);
                        actualPowerValue.setDescription("TP-Link Energie Sensor - " + name + " - IP " + ipAddress + ":" + port);;
                        EnergyValue energyValue = new EnergyValue(ID.create(), ID.create().get(), name);
                        energyValue.setSystemValue(true);
                        energyValue.setDescription("TP-Link Energieverbrauch Sensor - " + name + " - IP " + ipAddress + ":" + port);

                        socket.setVoltageSensorId(voltageValue.getId());
                        socket.setCurrentSensorId(currentValue.getId());
                        socket.setPowerSensorId(actualPowerValue.getId());
                        socket.setEnergySensorId(energyValue.getId());

                        //Sensoren speichern
                        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                        ReentrantReadWriteLock.WriteLock sensorLock = sensorEditor.writeLock();
                        sensorLock.lock();

                        sensorEditor.getData().add(voltageValue);
                        sensorEditor.getData().add(currentValue);
                        sensorEditor.getData().add(actualPowerValue);
                        sensorEditor.getData().add(energyValue);

                        sensorLock.unlock();
                    }

                    swe.getData().add(socket);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die TP-Link Steckdose wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/device");
                } else {

                    //Element bearbeiten
                    Optional<Switchable> switchableOptional1 = swe.getById(id);
                    if(switchableOptional1.isPresent() && switchableOptional1.get() instanceof TPlinkSocket) {

                        TPlinkSocket socket = (TPlinkSocket) switchableOptional1.get();
                        socket.setName(name);
                        socket.setDescription(description);
                        socket.setIpAddress(ipAddress);
                        socket.setPort(port);
                        socket.setDisabled(disabled);
                        socket.setInverse(inverse);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die TP-Link Steckdose wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/device");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die TP-Link Steckdose konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/device");
                    }
                }
            } else {

                //Steckdose bereits vorhanden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Es gibt bereits eine TP-Link Steckdose mit der Adresse \"" + ipAddress + ":" + port + "\"");
                resp.sendRedirect("/automation/admin/device");
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
