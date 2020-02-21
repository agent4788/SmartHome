package net.kleditzsch.apps.network.view.user.nasstate;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.network.api.nas.NasState;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

                SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
                ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                settingsLock.lock();
                settingsEditor.getStringSetting(Settings.NETWORK_NAS_STATE_IP).setValue(ip);
                settingsLock.unlock();
                model.with("ipChanged", true);
            }
        }

        //Einstellung laden
        String nasIp = "";
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        nasIp = settingsEditor.getStringSetting(Settings.NETWORK_NAS_STATE_IP).getValue();
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
