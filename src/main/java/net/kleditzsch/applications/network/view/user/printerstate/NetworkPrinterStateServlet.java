package net.kleditzsch.applications.network.view.user.printerstate;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.applications.network.api.printer.PrinterState;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

public class NetworkPrinterStateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/network/user/printerstate/printerstate.html");
        JtwigModel model = JtwigModel.newModel();

        //IP Speichern wenn angegeben
        if(req.getParameter("ip") != null) {

            FormValidation form = FormValidation.create(req);
            String ip = form.getString("ip", "IP Adresse", Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"));
            if(form.isSuccessful()) {

                SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
                ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                settingsLock.lock();
                settingsEditor.getStringSetting(Settings.NETWORK_PRINTER_STATE_IP).setValue(ip);
                settingsLock.unlock();
                model.with("ipChanged", true);
            }
        }

        //Einstellung laden
        String printerIp = "";
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        printerIp = settingsEditor.getStringSetting(Settings.NETWORK_PRINTER_STATE_IP).getValue();
        settingsLock.unlock();

        //Daten vom Drucker laden
        if(!printerIp.equals("0.0.0.0")) {

            try {

                PrinterState printer = new PrinterState(printerIp);
                printer.requestData();

                model.with("printer", printer);
                model.with("printerIp", printerIp);
            } catch (IOException e) {

                //Drucker konnte nicht erreicht werden
                model.with("connectionError", true);
                MessageEditor.addMessage(new Message("network", Message.Type.warning, "Der Drucker \"" + printerIp + "\" konnte nicht erreicht werden", e));
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
