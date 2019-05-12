package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.DistanceValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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

public class AutomationSensorValuesDistanceFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvaluesdistanceform.html");
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

                    DistanceValue sensorValue = (DistanceValue) sensorValueOptional.get();
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

        FormValidation form = FormValidation.create(req);

        ID id = form.getId("id", "Sensor Wert ID");
        String name = form.getString("name", "Name", 3, 50);
        String description = form.getString("description", "Beschreibung", 0, 250);
        double offset = form.getDouble("offset", "Offset", -100_000, 100_000);
        int timeout = form.getInteger("timeout", "Timeout", 0, 32_000_000);

        if (form.isSuccessful()) {

            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
            lock.lock();

            //Element bearbeiten
            Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
            if (sensorValueOptional.isPresent()) {

                DistanceValue sensorValue = (DistanceValue) sensorValueOptional.get();
                sensorValue.setName(name);
                sensorValue.setDescription(description);
                sensorValue.setOffset(offset);
                sensorValue.setTimeout(timeout);

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
