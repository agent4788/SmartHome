package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.UserAtHomeValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
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
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSensorValuesUserAtHomeFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvaluesuserathomeform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
        boolean addElement = true;
        UserAtHomeValue sensorValue = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                if(sensorValueOptional.isPresent()) {

                    sensorValue = (UserAtHomeValue) sensorValueOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Sensorwert wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            sensorValue = new UserAtHomeValue(ID.create(), "Identifier", "Name ...");
            sensorValue.setUseExternalDataSource(false);
        }
        model.with("addElement", addElement);
        model.with("sensorValue", sensorValue);

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
        String identifier = req.getParameter("identifier");
        String description = req.getParameter("description");
        String ipAddress = req.getParameter("ipAddress");
        String timeoutStr = req.getParameter("timeout");

        //Daten vorbereiten
        boolean addElement = true;
        int timeout = 0;
        ID id = null;

        //Daten prüfen
        boolean success = true;
        try {

            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //neues Element
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            if(addElement) {

                //IP Adresse
                if(!(ipAddress != null)) {

                    success = false;
                } else {

                    InetAddress.getByName(ipAddress);
                }

                //Identifier
                if(!(identifier != null && identifier.matches("^[a-zA-Z0-9\\-]{3,50}$"))) {

                    success = false;
                }
            } else {

                if(ipAddress != null) {

                    //IP Adresse
                    if(!(ipAddress != null)) {

                        success = false;
                    } else {

                        InetAddress.getByName(ipAddress);
                    }
                } else {

                    //Timeout
                    timeout = Integer.parseInt(timeoutStr);
                    if(!(timeout >= 10000 && timeout <= 86_400_000)) {

                        success = false;
                    }
                }
            }

        } catch (NumberFormatException | UnknownHostException e) {

            success = false;
        }

        if (success) {

            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
            lock.lock();

            if(addElement) {

                Optional<SensorValue> sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                if(!sensorValueOptional.isPresent()) {

                    //neues Element
                    UserAtHomeValue sensorValue = new UserAtHomeValue(ID.create(), identifier, name);
                    sensorValue.setDescription(description);
                    sensorValue.setUseExternalDataSource(false);
                    sensorValue.setIpAddress(ipAddress);

                    sensorEditor.getData().add(sensorValue);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Sensorwert wurde erfolgreich erstellt");
                    resp.sendRedirect("/automation/admin/sensorvalues");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Sensorwert ist bereits vorhanden");
                    resp.sendRedirect("/automation/admin/sensorvalues");
                }

            } else {

                //Element bearbeiten
                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                if (sensorValueOptional.isPresent()) {

                    UserAtHomeValue sensorValue = (UserAtHomeValue) sensorValueOptional.get();
                    sensorValue.setName(name);
                    sensorValue.setDescription(description);
                    if(sensorValue.isUseExternalDataSource()) {

                        sensorValue.setTimeout(timeout);
                    } else {

                        sensorValue.setIpAddress(ipAddress);
                    }

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Sensorwert wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/automation/admin/sensorvalues");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Sensorwert konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/sensorvalues");
                }
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/sensorvalues");
        }
    }
}
