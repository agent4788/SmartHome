package net.kleditzsch.apps.automation.view.admin.settings;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.settings.DoubleSetting;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.settings.StringSetting;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
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
        Optional<IntegerSetting> sunriseOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNRISE_OFFSET);
        sunriseOffsetOptional.ifPresent(setting -> model.with("sunriseOffset", setting.getValue()));
        Optional<IntegerSetting> sunsetOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNSET_OFFSET);
        sunsetOffsetOptional.ifPresent(setting -> model.with("sunsetOffset", setting.getValue()));
        Optional<DoubleSetting> latitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LATITUDE);
        latitudeOptional.ifPresent(setting -> model.with("latitude", setting.getValue()));
        Optional<DoubleSetting> longitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LONGITUDE);
        longitudeOptional.ifPresent(setting -> model.with("longitude", setting.getValue()));

        Optional<BooleanSetting> fbActiveOptional = se.getBooleanSetting(SettingsEditor.AUTOMATION_FB_ACTIVE);
        fbActiveOptional.ifPresent(setting -> model.with("fbActive", setting.getValue()));
        Optional<StringSetting> fbAddressOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_ADDRESS);
        fbAddressOptional.ifPresent(setting -> model.with("fbAddress", setting.getValue()));
        Optional<StringSetting> fbUserOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_USER);
        fbUserOptional.ifPresent(setting -> model.with("fbUser", setting.getValue()));
        Optional<StringSetting> fbPasswordOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_PASSWORD);
        fbPasswordOptional.ifPresent(setting -> model.with("fbPassword", setting.getValue()));

        Optional<BooleanSetting> mqttActiveOptional = se.getBooleanSetting(SettingsEditor.AUTOMATION_MQTT_ACTIVE);
        mqttActiveOptional.ifPresent(setting -> model.with("mqttActive", setting.getValue()));
        Optional<StringSetting> brokerAddressOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_ADDRESS);
        brokerAddressOptional.ifPresent(setting -> model.with("brokerAddress", setting.getValue()));
        Optional<IntegerSetting> brokerPortOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PORT);
        brokerPortOptional.ifPresent(setting -> model.with("brokerPort", setting.getValue()));
        Optional<StringSetting> brokerUsernameOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_USERNAME);
        brokerUsernameOptional.ifPresent(setting -> model.with("brokerUsername", setting.getValue()));
        Optional<StringSetting> brokerPasswordOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PASSWORD);
        brokerPasswordOptional.ifPresent(setting -> model.with("brokerPassword", setting.getValue()));
        Optional<StringSetting> clientIdOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_CLIENT_ID);
        clientIdOptional.ifPresent(setting -> model.with("clientId", setting.getValue()));

        Optional<DoubleSetting> electricPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_ELECTRIC_PRICE);
        electricPriceOptional.ifPresent(setting -> model.with("electricPrice", setting.getValue()));
        Optional<DoubleSetting> waterPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_WATER_PRICE);
        waterPriceOptional.ifPresent(setting -> model.with("waterPrice", setting.getValue()));

        Optional<IntegerSetting> elementsAtPageOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE);
        elementsAtPageOptional.ifPresent(setting -> model.with("elementsAtPage", setting.getValue()));

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
            Optional<IntegerSetting> sunriseOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNRISE_OFFSET);
            sunriseOffsetOptional.ifPresent(setting -> setting.setValue(sunriseOffset));
            Optional<IntegerSetting> sunsetOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNSET_OFFSET);
            sunsetOffsetOptional.ifPresent(setting -> setting.setValue(sunsetOffset));
            Optional<DoubleSetting> latitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LATITUDE);
            latitudeOptional.ifPresent(setting -> setting.setValue(latitude));
            Optional<DoubleSetting> longitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LONGITUDE);
            longitudeOptional.ifPresent(setting -> setting.setValue(longitude));

            Optional<BooleanSetting> fbActiveOptional = se.getBooleanSetting(SettingsEditor.AUTOMATION_FB_ACTIVE);
            fbActiveOptional.ifPresent(setting -> setting.setValue(fbActive));
            if(fbActive) {

                Optional<StringSetting> fbAddressOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_ADDRESS);
                fbAddressOptional.ifPresent(setting -> setting.setValue(fbAddress));
                Optional<StringSetting> fbUserOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_USER);
                fbUserOptional.ifPresent(setting -> setting.setValue(fbUser));
                Optional<StringSetting> fbPasswordOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_PASSWORD);
                fbPasswordOptional.ifPresent(setting -> setting.setValue(fbPassword));
            }

            Optional<BooleanSetting> mqttActiveOptional = se.getBooleanSetting(SettingsEditor.AUTOMATION_MQTT_ACTIVE);
            mqttActiveOptional.ifPresent(setting -> setting.setValue(mqttActive));
            if(mqttActive) {

                Optional<StringSetting> fbAddressOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_ADDRESS);
                fbAddressOptional.ifPresent(setting -> setting.setValue(brokerAddress));
                Optional<IntegerSetting> brokerPortOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PORT);
                brokerPortOptional.ifPresent(setting -> setting.setValue(brokerPort));
                Optional<StringSetting> brokerUsernameOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_USERNAME);
                brokerUsernameOptional.ifPresent(setting -> setting.setValue(brokerUsername));
                Optional<StringSetting> brokerPasswordOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_PASSWORD);
                brokerPasswordOptional.ifPresent(setting -> setting.setValue(brokerPassword));
                Optional<StringSetting> clientIdOptional = se.getStringSetting(SettingsEditor.AUTOMATION_MQTT_BROKER_CLIENT_ID);
                clientIdOptional.ifPresent(setting -> setting.setValue(clientId));
            }

            Optional<DoubleSetting> electricPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_ELECTRIC_PRICE);
            electricPriceOptional.ifPresent(setting -> setting.setValue(electricPrice));
            Optional<DoubleSetting> waterPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_WATER_PRICE);
            waterPriceOptional.ifPresent(setting -> setting.setValue(waterPrice));

            Optional<IntegerSetting> elementsAtPageOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE);
            elementsAtPageOptional.ifPresent(setting -> setting.setValue(elementsAtPage));

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
