package net.kleditzsch.SmartHome.view.global.admin.settings;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
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

public class GlobalSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden


        //Meldung
        if(req.getSession().getAttribute("success") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            req.getSession().removeAttribute("success");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /**
        String strPort = req.getParameter("port");
        String strSecurePort = req.getParameter("securePort");
        String storePassword = req.getParameter("storePassword");
        String managerPassword = req.getParameter("managerPassword");

        //Daten vorbereiten
        int port = 0, securePort = 0;

        //Daten prüfen
        boolean success = true;
        try {

            port = Integer.parseInt(strPort);
            if(!(port >= 0 && port <= 65535)) {

                success = false;
            }
            securePort = Integer.parseInt(strSecurePort);
            if(!(securePort >= 0 && securePort <= 65535)) {

                success = false;
            }
            if(storePassword.length() > 100) {

                success = false;
            }
            if(managerPassword.length() > 100) {

                success = false;
            }
        } catch (Exception e) {

            success = false;
        }

        if (success) {

            //Einstellungen speichern
            SettingsEditor se = Application.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            final int finalPort = port;
            final int finalSecurePort = securePort;
            Optional<IntegerSetting> portOptional = se.getIntegerSetting(SettingsEditor.SERVER_PORT);
            portOptional.ifPresent(setting -> setting.setValue(finalPort));
            Optional<IntegerSetting> securePortOptional = se.getIntegerSetting(SettingsEditor.SERVER_SECURE_PORT);
            securePortOptional.ifPresent(setting -> setting.setValue(finalSecurePort));
            if(storePassword.length() > 0) {
                Optional<StringSetting> storePasswordOptional = se.getStringSetting(SettingsEditor.SERVER_KEY_STORE_PASSWORD);
                storePasswordOptional.ifPresent(setting -> setting.setValue(storePassword));
            }
            if(managerPassword.length() > 0) {

                Optional<StringSetting> managerPasswordOptional = se.getStringSetting(SettingsEditor.SERVER_KEY_MANAGER_PASSWORD);
                managerPasswordOptional.ifPresent(setting -> setting.setValue(managerPassword));
            }

            lock.unlock();

            req.getSession().setAttribute("success", true);
            resp.sendRedirect("/admin/settings");
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            resp.sendRedirect("/admin/settings");
        }
         */
        resp.sendRedirect("/admin/settings");
    }
}
