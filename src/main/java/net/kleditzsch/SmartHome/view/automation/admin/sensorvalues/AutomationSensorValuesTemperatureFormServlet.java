package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.DistanceValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.TemperatureValue;
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
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSensorValuesTemperatureFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvaluestemperatureform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();

        if(req.getParameter("id") != null) {

            ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                if(sensorValueOptional.isPresent()) {

                    TemperatureValue sensorValue = (TemperatureValue) sensorValueOptional.get();
                    model.with("sensorValue", sensorValue);
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

            model.with("error", "Der Sensorwert wurde nicht gefunden");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String offsetStr = req.getParameter("offset");

        //Daten vorbereiten
        double offset = 0.0;
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
            offset = Double.parseDouble(offsetStr);
            if(!(offset >= -25 && offset <= 25)) {

                success = false;
            }

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
            lock.lock();

            //Element bearbeiten
            Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
            if (sensorValueOptional.isPresent()) {

                TemperatureValue sensorValue = (TemperatureValue) sensorValueOptional.get();
                sensorValue.setName(name);
                sensorValue.setDescription(description);
                sensorValue.setOffset(offset);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Sensorwert wurde erfolgreich bearbeitet");
                resp.sendRedirect("/automation/admin/sensorvalues");
            } else {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Der Sensorwert konnte nicht gefunden werden");
                resp.sendRedirect("/automation/admin/sensorvalues");
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
