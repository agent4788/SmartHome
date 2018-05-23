package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSensorValuesDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
                lock.lock();

                Optional<SensorValue> sensorValueOptional = sensorEditor.getById(id);
                if(sensorValueOptional.isPresent() && !sensorValueOptional.get().isSystemValue()) {

                    sensorEditor.getData().remove(sensorValueOptional.get());
                } else {

                    success = false;
                }

                lock.unlock();

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Der Sensorwert wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/sensorvalues");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Sensorwert konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/sensorvalues");
        }
    }
}
