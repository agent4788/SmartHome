package net.kleditzsch.applications.automation.view.admin.settings;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        model.with("sunriseOffset", se.getIntegerSetting(Settings.AUTOMATION_SUNRISE_OFFSET).getValue());
        model.with("sunsetOffset", se.getIntegerSetting(Settings.AUTOMATION_SUNSET_OFFSET).getValue());
        model.with("latitude", se.getDoubleSetting(Settings.AUTOMATION_LATITUDE).getValue());
        model.with("longitude", se.getDoubleSetting(Settings.AUTOMATION_LONGITUDE).getValue());

        model.with("fbActive", se.getBooleanSetting(Settings.AUTOMATION_FB_ACTIVE).getValue());
        model.with("fbAddress", se.getStringSetting(Settings.AUTOMATION_FB_ADDRESS).getValue());
        model.with("fbUser", se.getStringSetting(Settings.AUTOMATION_FB_USER).getValue());
        model.with("fbPassword", se.getStringSetting(Settings.AUTOMATION_FB_PASSWORD).getValue());

        model.with("mqttActive", se.getBooleanSetting(Settings.AUTOMATION_MQTT_ACTIVE).getValue());
        model.with("brokerAddress", se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_ADDRESS).getValue());
        model.with("brokerPort", se.getIntegerSetting(Settings.AUTOMATION_MQTT_BROKER_PORT).getValue());
        model.with("brokerUsername", se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_USERNAME).getValue());
        model.with("brokerPassword", se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_PASSWORD).getValue());
        model.with("clientId", se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_CLIENT_ID).getValue());

        model.with("electricPrice", se.getDoubleSetting(Settings.AUTOMATION_ENERGY_ELECTRIC_PRICE).getValue());
        model.with("waterPrice", se.getDoubleSetting(Settings.AUTOMATION_ENERGY_WATER_PRICE).getValue());

        model.with("elementsAtPage", se.getIntegerSetting(Settings.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE).getValue());

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

        FormValidation form = FormValidation.create(req);
        final int sunriseOffset = form.getInteger("sunriseOffset", "Offset Sonnenaufgang", -60, 60);
        final int sunsetOffset = form.getInteger("sunsetOffset", "Offset Sonnenauntergang", -60, 60);
        final double latitude = form.getDouble("latitude", "Breitengrad", 0.0, 360.0);
        final double longitude = form.getDouble("longitude", "Längengrad", 0.0, 260.0);

        final boolean fbActive = form.optBoolean("fbActive", "FritzBox Support Aktiv", false);
        final String fbAddress = form.optString("fbAddress", "FritzBox Adresse", "", 3, 100);
        final String fbUser = form.optString("fbUser", "FitzBox Benutzer", "", 3, 100);
        final String fbPassword = form.optString("fbPassword", "FritzBox Passwort", "", 3, 100);

        final boolean mqttActive = form.optBoolean("mqttActive", "MQTT Support Aktiv", false);
        final String brokerAddress = form.optString("brokerAddress", "MQTT Broker Adresse", "", 3, 100);
        final int brokerPort = form.optInteger("brokerPort", "MQTT Broker Port", 1883,0, 65535);
        final String brokerUsername = form.optString("brokerUsername", "MQTT Broker Benutzer", "", 0, 100);
        final String brokerPassword = form.optString("brokerPassword", "MQTT Broker Passwort", "", 0, 100);
        final String clientId = form.optString("clientId", "MQTT Client ID", "", 3, 100);

        final double electricPrice = form.getDouble("electricPrice", "Strompreis", 0.01, 10.0);
        final double waterPrice = form.getDouble("waterPrice", "Wasserpreis", 0.01, 10.0);

        final int elementsAtPage = form.getInteger("elementsAtPage", "Elemente pro Seite", 5, 100);

        if (form.isSuccessful()) {

            //Einstellungen speichern
            SettingsEditor se = SmartHome.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            se.getIntegerSetting(Settings.AUTOMATION_SUNRISE_OFFSET).setValue(sunriseOffset);
            se.getIntegerSetting(Settings.AUTOMATION_SUNSET_OFFSET).setValue(sunsetOffset);
            se.getDoubleSetting(Settings.AUTOMATION_LATITUDE).setValue(latitude);
            se.getDoubleSetting(Settings.AUTOMATION_LONGITUDE).setValue(longitude);

            se.getBooleanSetting(Settings.AUTOMATION_FB_ACTIVE).setValue(fbActive);
            if(fbActive) {

                se.getStringSetting(Settings.AUTOMATION_FB_ADDRESS).setValue(fbAddress);
                se.getStringSetting(Settings.AUTOMATION_FB_USER).setValue(fbUser);
                se.getStringSetting(Settings.AUTOMATION_FB_PASSWORD).setValue(fbPassword);
            }

            se.getBooleanSetting(Settings.AUTOMATION_MQTT_ACTIVE).setValue(mqttActive);
            if(mqttActive) {

                se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_ADDRESS).setValue(brokerAddress);
                se.getIntegerSetting(Settings.AUTOMATION_MQTT_BROKER_PORT).setValue(brokerPort);
                se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_USERNAME).setValue(brokerUsername);
                se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_PASSWORD).setValue(brokerPassword);
                se.getStringSetting(Settings.AUTOMATION_MQTT_BROKER_CLIENT_ID).setValue(clientId);
            }

            se.getDoubleSetting(Settings.AUTOMATION_ENERGY_ELECTRIC_PRICE).setValue(electricPrice);
            se.getDoubleSetting(Settings.AUTOMATION_ENERGY_WATER_PRICE).setValue(waterPrice);

            se.getIntegerSetting(Settings.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE).setValue(elementsAtPage);

            lock.unlock();

            req.getSession().setAttribute("success", true);
            resp.sendRedirect("/automation/admin/settings");
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            resp.sendRedirect("/automation/admin/settings");
        }
    }
}
