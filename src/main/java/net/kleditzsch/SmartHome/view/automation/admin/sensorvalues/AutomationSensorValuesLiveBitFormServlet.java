package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.BiStateValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.LiveBitValue;
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

public class AutomationSensorValuesLiveBitFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvalueslivebitform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();

        if(req.getParameter("id") != null) {

            ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof LiveBitValue) {

                    LiveBitValue sensorValue = (LiveBitValue) sensorValueOptional.get();
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
        String trueText = req.getParameter("trueText");
        String falseText = req.getParameter("falseText");
        String timeoutStr = req.getParameter("timeout");

        //Daten vorbereiten
        long timeout = 0;
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
            if(!(trueText != null && trueText.length() >= 1 && trueText.length() <= 15)) {

                success = false;
            }
            if(!(falseText != null && falseText.length() >= 1 && falseText.length() <= 15)) {

                success = false;
            }
            timeout = Long.parseLong(timeoutStr);
            if(!(timeout >= 10000 && timeout <= 86_400_000)) {

                success = false;
            }

        } catch (NumberFormatException e) {

            success = false;
        }

        if (success) {

            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
            lock.lock();

            //Element bearbeiten
            Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
            if (sensorValueOptional.isPresent()) {

                LiveBitValue sensorValue = (LiveBitValue) sensorValueOptional.get();
                sensorValue.setName(name);
                sensorValue.setDescription(description);
                sensorValue.setTrueText(trueText);
                sensorValue.setFalseText(falseText);
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
