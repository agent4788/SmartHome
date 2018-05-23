package net.kleditzsch.SmartHome.view.automation.admin.switchserver;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;
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

public class AutomationSwitchServerFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/switchserver/switchserverform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
        boolean addElement = true;
        SwitchServer switchServer = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            ReentrantReadWriteLock.ReadLock lock = sse.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<SwitchServer> switchServerOptional = sse.getById(id);
                if(switchServerOptional.isPresent()) {

                    switchServer = switchServerOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Schaltserver wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            switchServer = new SwitchServer(ID.create(), "Name ...", "", 1000, true);
        }
        model.with("addElement", addElement);
        model.with("switchServer", switchServer);

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
        String timeoutStr = req.getParameter("timeout");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        int port = 0, timeout = 0;
        boolean addElement = true, disabled = true;
        ID id = null;

        //Daten prüfen
        boolean success = true;
        try {

            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            if(!(ipAddress != null)) {

                success = false;
            } else {

                InetAddress.getByName(ipAddress);
            }
            port = Integer.parseInt(portStr);
            if(!(port >= 0 && port <= 65_535)) {

                success = false;
            }
            timeout = Integer.parseInt(timeoutStr);
            if(!(timeout >= 0 && timeout <= 10_000)) {

                success = false;
            }
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            SwitchServerEditor sse = Application.getInstance().getAutomation().getSwitchServerEditor();
            ReentrantReadWriteLock.WriteLock lock = sse.writeLock();
            lock.lock();

            //prüfen ob Schaltserver schon vorhanden
            final int finalPort = port;
            final ID finalId = id;
            Optional<SwitchServer> ssOptional = sse.getData().stream()
                    .filter(ss -> {
                        return ss.getIpAddress().equals(ipAddress)
                                && ss.getPort() == finalPort
                                && !ss.getId().equals(finalId);
                    })
                    .findFirst();
            if(ssOptional.isPresent()) {

                //Schaltserver bereits vorhanden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Es gibt bereits einen Schaltserver mit der Adresse \"" + ipAddress + ":" + port + "\"");
                resp.sendRedirect("/automation/admin/switchserver");
            } else {

                if(addElement) {

                    //neues Element hinzufügen
                    SwitchServer switchServer = new SwitchServer(ID.create(), name, ipAddress, port, disabled);
                    switchServer.setDescription(description);
                    switchServer.setTimeout(timeout);
                    sse.getData().add(switchServer);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Schaltserver wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/switchserver");
                } else {

                    //Element bearbeiten
                    Optional<SwitchServer> switchServerOptional = sse.getById(id);
                    if (switchServerOptional.isPresent()) {

                        SwitchServer switchServer = switchServerOptional.get();
                        switchServer.setName(name);
                        switchServer.setDescription(description);
                        switchServer.setIpAddress(ipAddress);
                        switchServer.setPort(port);
                        switchServer.setTimeout(timeout);
                        switchServer.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Schaltserver wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/switchserver");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Schaltserver konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/switchserver");
                    }
                }
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/switchserver");
        }
    }
}
