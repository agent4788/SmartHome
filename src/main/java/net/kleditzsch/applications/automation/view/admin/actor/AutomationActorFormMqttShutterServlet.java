package net.kleditzsch.applications.automation.view.admin.actor;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.Interface.MqttElement;
import net.kleditzsch.applications.automation.model.device.actor.shutter.MqttShutter;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;
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

public class AutomationActorFormMqttShutterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/actor/actorformmqttshutter.html");
        JtwigModel model = JtwigModel.newModel();

        //Prüfen ob MQTT aktiv
        if(SmartHome.getInstance().getAutomation().getMqttService().isActive()) {

            //Daten vorbereiten
            ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
            boolean addElement = true;
            MqttShutter mqttDevice = null;

            if(req.getParameter("id") != null) {

                addElement = false;

                //Steckdose laden
                ReentrantReadWriteLock.ReadLock lock = actorEditor.readLock();
                try {

                    ID id = ID.of(req.getParameter("id").trim());
                    lock.lock();

                    Optional<Actor> actorOptional = actorEditor.getById(id);
                    if(actorOptional.isPresent() && actorOptional.get() instanceof MqttShutter) {

                        mqttDevice = (MqttShutter) actorOptional.get();
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

                mqttDevice = new MqttShutter();
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
        if(SmartHome.getInstance().getAutomation().getMqttService().isActive()) {

            FormValidation form = FormValidation.create(req);
            ID id = form.getId("id", "ID");
            boolean addElement = form.optBoolean("addElement", "neues Element", false);
            String name = form.getString("name", "Name", 3, 50);
            String description = form.optString("description", "Beschreibung", "", 3, 250);
            boolean disabled = form.optBoolean("disabled", "Deaktiviert", false);
            String mqttName = form.getString("mqttname", "MQTT Name", Pattern.compile("^[a-zA-Z0-9]{3,50}$"), 3, 50);

            if (form.isSuccessful()) {

                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
                ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
                lock.lock();

                if(checkMqttName(mqttName, id, actorEditor)) {

                    if(addElement) {

                        //neues Element hinzufügen
                        MqttShutter mqtt = new MqttShutter();
                        mqtt.setId(ID.create());
                        mqtt.setName(name);
                        mqtt.setDescription(description);
                        mqtt.setDisabled(disabled);
                        mqtt.setMqttName(mqttName);
                        actorEditor.getData().add(mqtt);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Das MQTT Gerät wurde erfolgreich hinzugefügt");
                        resp.sendRedirect("/automation/admin/actor");
                    } else {

                        //Element bearbeiten
                        Optional<Actor> actorOptional = actorEditor.getById(id);
                        if (actorOptional.isPresent() && actorOptional.get() instanceof MqttShutter) {

                            MqttShutter mqtt = (MqttShutter) actorOptional.get();
                            mqtt.setName(name);
                            mqtt.setDescription(description);
                            mqtt.setDisabled(disabled);
                            mqtt.setMqttName(mqttName);

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Das MQTT Gerät wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/actor");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Das MQTT Gerät konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/actor");
                        }
                    }
                } else {

                    //MQTT Name ungültig
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der MQTT Name wird bereits verwendet!");
                    resp.sendRedirect("/automation/admin/actor");
                }

                lock.unlock();
            } else {

                //Eingaben n.i.O.
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                resp.sendRedirect("/automation/admin/actor");
            }
        } else {

            //Fritz Box Support inaktiv
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der MQTT Support ist deaktiviert");
            resp.sendRedirect("/automation/admin/actor");
        }
    }

    /**
     * prüft den MQTT Name auf seine Gültigkeit
     *
     * @param mqttName MQTT Name
     * @param selfId eigene ID
     * @param actorEditor Switchable Editor
     * @return true wenn gültig
     */
    private boolean checkMqttName(String mqttName, ID selfId, ActorEditor actorEditor) {

        Optional<MqttElement> mqttElementOptional = actorEditor.getData().stream()
                .filter(e -> e instanceof MqttElement)
                .filter(e -> !e.getId().equals(selfId))
                .map(e -> (MqttElement) e)
                .filter(e -> e.getMqttName().equalsIgnoreCase(mqttName))
                .findFirst();

        if (mqttElementOptional.isPresent()) {

            return false;
        }
        return true;
    }
}