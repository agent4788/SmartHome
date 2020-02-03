package net.kleditzsch.SmartHome.view.automation.admin.actor;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.switchable.ScriptSingle;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationActorFormScriptSingleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/actor/actorformscriptsingle.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
        boolean addElement = true;
        ScriptSingle script = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Steckdose laden
            ReentrantReadWriteLock.ReadLock lock = actorEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<Actor> actorOptional = actorEditor.getById(id);
                if(actorOptional.isPresent() && actorOptional.get() instanceof ScriptSingle) {

                    script = (ScriptSingle) actorOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Script wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            script = new ScriptSingle();
            script.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("script", script);

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
        List<String> commands = new ArrayList<>();
        for(int i = 0; i < 100; i++) {

            if(req.getParameter("command_" + i) != null) {

                commands.add(req.getParameter("command_" + i));
            } else {

                break;
            }
        }
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = false;
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
            //Befehle
            for(String command : commands) {

                if(!(command != null && command.length() <= 100)) {

                    success = false;
                }
            }
            //Deaktiviert und Invertiert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            if(addElement) {

                //neues Element hinzufügen
                ScriptSingle script = new ScriptSingle();
                script.setId(ID.create());
                script.setName(name);
                script.setDescription(description);
                script.getCommand().clear();
                script.getCommand().addAll(commands);
                script.setDisabled(disabled);
                actorEditor.getData().add(script);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Script wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/automation/admin/actor");
            } else {

                //Element bearbeiten
                Optional<Actor> actorOptional = actorEditor.getById(id);
                if(actorOptional.isPresent() && actorOptional.get() instanceof ScriptSingle) {

                    ScriptSingle script = (ScriptSingle) actorOptional.get();
                    script.setName(name);
                    script.setDescription(description);
                    script.getCommand().clear();
                    script.getCommand().addAll(commands);
                    script.setDisabled(disabled);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Script wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/automation/admin/actor");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Script konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/actor");
                }
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
