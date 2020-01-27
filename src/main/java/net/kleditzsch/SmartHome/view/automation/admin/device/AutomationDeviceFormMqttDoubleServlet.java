package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.MqttDouble;
import net.kleditzsch.SmartHome.model.automation.device.switchable.MqttSingle;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
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
import java.util.regex.Pattern;

public class AutomationDeviceFormMqttDoubleServlet extends HttpServlet {

    /**
     * Namenskonvention für
     */
    private Pattern pattern = Pattern.compile("^[a-zA-Z]{3,50}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/device/deviceformmqttdouble.html");
        JtwigModel model = JtwigModel.newModel();

        //Prüfen ob MQTT aktiv
        if(Application.getInstance().getAutomation().getMqttService().isActive()) {

            //Daten vorbereiten
            SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
            boolean addElement = true;
            MqttDouble mqttDevice = null;

            if(req.getParameter("id") != null) {

                addElement = false;

                //Steckdose laden
                ReentrantReadWriteLock.ReadLock lock = swe.readLock();
                try {

                    ID id = ID.of(req.getParameter("id").trim());
                    lock.lock();

                    Optional<Switchable> switchableOptional = swe.getById(id);
                    if(switchableOptional.isPresent() && switchableOptional.get() instanceof MqttDouble) {

                        mqttDevice = (MqttDouble) switchableOptional.get();
                    } else {

                        //Element nicht gefunden
                        throw new Exception();
                    }

                } catch (Exception e) {

                    model.with("error", "Das MQTT Gerät wurde nicht gefunden");
                } finally {

                    lock.unlock();
                }
            } else {

                mqttDevice = new MqttDouble();
                mqttDevice.setId(ID.create());
            }
            model.with("addElement", addElement);
            model.with("mqttDevice", mqttDevice);

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));
        } else {

            //Fritz Box Support inaktiv
            model.with("error", "Der MQTT Support ist deaktiviert");

            //Template rendern
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            template.render(model, new WriterOutputStream(resp.getWriter()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Prüfen ob MQTT aktiv
        if(Application.getInstance().getAutomation().getMqttService().isActive()) {

            FormValidation form = FormValidation.create(req);
            ID id = form.getId("id", "ID");
            boolean addElement = form.optBoolean("addElement", "neues Element", false);
            String name = form.getString("name", "Name", 3, 50);
            String description = form.optString("description", "Beschreibung", "", 3, 250);
            boolean disabled = form.optBoolean("disabled", "Deaktivier", false);
            boolean inverse = form.optBoolean("inverse", "Invertiert", false);
            String mqttName = form.getString("mqttname", "MQTT Name", 3, 50);

            if (form.isSuccessful()) {

                SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
                ReentrantReadWriteLock.WriteLock lock = swe.writeLock();
                lock.lock();

                if(checkMqttName(mqttName, swe)) {

                    if (addElement) {

                        //neues Element hinzufügen
                        MqttDouble mqtt = new MqttDouble();
                        mqtt.setId(ID.create());
                        mqtt.setName(name);
                        mqtt.setDescription(description);
                        mqtt.setDisabled(disabled);
                        mqtt.setInverse(inverse);
                        mqtt.setMqttName(mqttName);
                        swe.getData().add(mqtt);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Das MQTT Gerät wurde erfolgreich hinzugefügt");
                        resp.sendRedirect("/automation/admin/device");
                    } else {

                        //Element bearbeiten
                        Optional<Switchable> switchableOptional1 = swe.getById(id);
                        if (switchableOptional1.isPresent() && switchableOptional1.get() instanceof MqttDouble) {

                            MqttDouble mqtt = (MqttDouble) switchableOptional1.get();
                            mqtt.setName(name);
                            mqtt.setDescription(description);
                            mqtt.setDisabled(disabled);
                            mqtt.setInverse(inverse);
                            mqtt.setMqttName(mqttName);

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Das MQTT Gerät wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/device");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Das MQTT Gerät konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/device");
                        }
                    }
                } else {

                    //MQTT Name ungültig
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der MQTT Name ist ungültig oder wird bereits verwendet!");
                    resp.sendRedirect("/automation/admin/device");
                }

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
            req.getSession().setAttribute("message", "Der MQTT Support ist deaktiviert");
            resp.sendRedirect("/automation/admin/device");
        }
    }

    /**
     * prüft den MQTT Name auf seine Gültigkeit
     *
     * @param mqttName MQTT Name
     * @param swe Switchable Editor
     * @return true wenn gültig
     */
    private boolean checkMqttName(String mqttName, SwitchableEditor swe) {

        if(pattern.matcher(mqttName).find()) {

            Optional<MqttDouble> mqttDoubleOptional = swe.getData().stream()
                    .filter(e -> e instanceof MqttDouble)
                    .map(e -> (MqttDouble) e)
                    .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                    .findFirst();

            if(mqttDoubleOptional.isPresent()) {

                return false;
            } else {

                Optional<MqttSingle> mqttSingleOptional = swe.getData().stream()
                        .filter(e -> e instanceof MqttSingle)
                        .map(e -> (MqttSingle) e)
                        .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                        .findFirst();

                if(mqttSingleOptional.isPresent()) {

                    return false;
                } else {

                    return true;
                }
            }
        }
        return false;
    }
}
