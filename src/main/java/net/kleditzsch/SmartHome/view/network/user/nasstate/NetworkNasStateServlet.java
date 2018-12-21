package net.kleditzsch.SmartHome.view.network.user.nasstate;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.api.nas.NasState;
import net.kleditzsch.SmartHome.util.api.printer.PrinterState;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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
import java.util.regex.Pattern;

public class NetworkNasStateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/network/user/nasstate/nasstate.html");
        JtwigModel model = JtwigModel.newModel();

        //IP Speichern wenn angegeben
        if(req.getParameter("ip") != null) {

            FormValidation form = FormValidation.create(req);
            String ip = form.getString("ip", "IP Adresse", Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"));
            if(form.isSuccessful()) {

                SettingsEditor settingsEditor = Application.getInstance().getSettings();
                ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                settingsLock.lock();
                Optional<StringSetting> nasStateSettingOptional = settingsEditor.getStringSetting(SettingsEditor.NETWORK_NAS_STATE_IP);
                nasStateSettingOptional.ifPresent(setting -> setting.setValue(ip));
                settingsLock.unlock();
                model.with("ipChanged", true);
            }
        }

        //Einstellung laden
        String nasIp = "";
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> nasStateSettingOptional = settingsEditor.getStringSetting(SettingsEditor.NETWORK_NAS_STATE_IP);
        if (nasStateSettingOptional.isPresent()) {

            nasIp = nasStateSettingOptional.get().getValue();
        }
        settingsLock.unlock();

        //Daten vom Drucker laden
        if(!nasIp.equals("0.0.0.0")) {

            try {

                NasState nas = new NasState(nasIp);
                nas.requestData();

                model.with("nas", nas);
                model.with("nasIp", nasIp);
            } catch (IOException e) {

                //NAS konnte nicht erreicht werden
                model.with("connectionError", true);
                MessageEditor.addMessage(new Message("network", Message.Type.warning, "Die NAS \"" + nasIp + "\" konnte nicht erreicht werden", e));
            }
        } else {

            //keine IP eingestellt
            model.with("noIp", true);
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
