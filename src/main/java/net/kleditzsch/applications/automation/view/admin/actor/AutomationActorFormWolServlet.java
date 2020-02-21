package net.kleditzsch.applications.automation.view.admin.actor;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.switchable.WakeOnLan;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;
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

public class AutomationActorFormWolServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/actor/actorformwol.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
        boolean addElement = true;
        WakeOnLan wakeOnLan = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Steckdose laden
            ReentrantReadWriteLock.ReadLock lock = actorEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<Actor> actorOptional = actorEditor.getById(id);
                if(actorOptional.isPresent() && actorOptional.get() instanceof WakeOnLan) {

                    wakeOnLan = (WakeOnLan) actorOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Wake on Lan Gerät wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            wakeOnLan = new WakeOnLan();
            wakeOnLan.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("wakeOnLan", wakeOnLan);

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
        String broadcastAddress = req.getParameter("broadcastAddress");
        String macAddress = req.getParameter("macAddress");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
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
            //IP Adresse
            if(!(broadcastAddress != null)) {
                System.out.println(broadcastAddress);
                success = false;
            } else {

                InetAddress.getByName(broadcastAddress);
            }
            //MAC Adresse
            if(!(macAddress != null && macAddress.matches("^[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}$"))) {

                success = false;
            }
            //Deaktiviert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            //prüfen ob Steckdose schon vorhanden
            final ID finalId = id;
            Optional<WakeOnLan> switchableOptional = actorEditor.getData().stream()
                    .filter(sw -> sw instanceof WakeOnLan)
                    .map(sw -> (WakeOnLan) sw)
                    .filter(sw -> {
                        return sw.getIpAddress().equals(broadcastAddress)
                                && sw.getMac().equalsIgnoreCase(macAddress)
                                && !sw.getId().equals(finalId);
                    })
                    .findFirst();
            if(!switchableOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    WakeOnLan wakeOnLan = new WakeOnLan();
                    wakeOnLan.setId(ID.create());
                    wakeOnLan.setName(name);
                    wakeOnLan.setDescription(description);
                    wakeOnLan.setIpAddress(broadcastAddress);
                    wakeOnLan.setMac(macAddress);
                    wakeOnLan.setDisabled(disabled);
                    actorEditor.getData().add(wakeOnLan);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Wake on Lan Gerät wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/actor");
                } else {

                    //Element bearbeiten
                    Optional<Actor> actorOptional = actorEditor.getById(id);
                    if(actorOptional.isPresent() && actorOptional.get() instanceof WakeOnLan) {

                        WakeOnLan wakeOnLan = (WakeOnLan) actorOptional.get();
                        wakeOnLan.setName(name);
                        wakeOnLan.setDescription(description);
                        wakeOnLan.setIpAddress(broadcastAddress);
                        wakeOnLan.setMac(macAddress);
                        wakeOnLan.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Das Wake on Lan Gerät wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/actor");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Das Wake on Lan Gerät konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/actor");
                    }
                }
            } else {

                //Steckdose bereits vorhanden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Es gibt bereits ein Wake on Lan Gerät mit der Adresse \"" + macAddress + "\"");
                resp.sendRedirect("/automation/admin/actor");
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/actor");
        }
    }
}
