package net.kleditzsch.SmartHome.view.network.user.printerstate;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.model.network.devices.NetworkDevice;
import net.kleditzsch.SmartHome.model.network.devices.NetworkDeviceGroup;
import net.kleditzsch.SmartHome.model.network.editor.NetworkDeviceEditor;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

                SettingsEditor settingsEditor = Application.getInstance().getSettings();
                ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
                settingsLock.lock();
                Optional<StringSetting> printerStateSettingOptional = settingsEditor.getStringSetting(SettingsEditor.NETWORK_PRINTER_STATE_IP);
                printerStateSettingOptional.ifPresent(setting -> setting.setValue(ip));
                settingsLock.unlock();
                model.with("ipChanged", true);
            }
        }

        //Einstellung laden
        String printerIp = "";
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> printerStateSettingOptional = settingsEditor.getStringSetting(SettingsEditor.NETWORK_PRINTER_STATE_IP);
        if (printerStateSettingOptional.isPresent()) {

            printerIp = printerStateSettingOptional.get().getValue();
        }
        settingsLock.unlock();

        //Daten vom Drucker laden
        if(!printerIp.isBlank()) {

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
