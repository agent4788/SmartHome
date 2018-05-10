package net.kleditzsch.SmartHome.view.automation.admin;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.global.settings.DoubleSetting;
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

public class AutomationSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = Application.getInstance().getSettings();
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

        String sunriseOffsetString = req.getParameter("sunriseOffset");
        String sunsetOffsetString = req.getParameter("sunsetOffset");
        String latitudeString = req.getParameter("latitude");
        String longitudeString = req.getParameter("longitude");

        String fbActiveString = req.getParameter("fbActive");
        String fbAddress = req.getParameter("fbAddress");
        String fbUser = req.getParameter("fbUser");
        String fbPassword = req.getParameter("fbPassword");

        String electricPriceString = req.getParameter("electricPrice");
        String waterPriceString = req.getParameter("waterPrice");

        String elementsAtPageString = req.getParameter("elementsAtPage");

        //Daten vorbereiten
        int sunriseOffset = 0, sunsetOffset = 0, elementsAtPage = 0;
        double latitude = 0, longitude = 0, electricPrice = 0, waterPrice = 0;
        boolean fbActive = false;

        //Daten prüfen
        boolean success = true;
        try {

            sunriseOffset = Integer.parseInt(sunriseOffsetString);
            if(!(sunriseOffset >= -60 && sunriseOffset <= 60)) {

                success = false;
            }
            sunsetOffset = Integer.parseInt(sunsetOffsetString);
            if(!(sunsetOffset >= -60 && sunsetOffset <= 60)) {

                success = false;
            }
            latitude = Double.parseDouble(latitudeString);
            if(!(latitude >= 0.0 && latitude <= 360.0)) {

                success = false;
            }
            longitude = Double.parseDouble(longitudeString);
            if(!(longitude >= 0.0 && longitude <= 360.0)) {

                success = false;
            }
            fbActive = fbActiveString != null && fbActiveString.equalsIgnoreCase("on");
            if(fbActive) {

                if(!(fbAddress.length() >= 1 && fbAddress.length() <= 100)) {

                    success = false;
                }
                if(!(fbUser.length() >= 1 && fbUser.length() <= 100)) {

                    success = false;
                }
                if(!(fbPassword.length() >= 1 && fbPassword.length() <= 100)) {

                    success = false;
                }
            }
            electricPrice = Double.parseDouble(electricPriceString);
            if(!(electricPrice >= 0.1 && electricPrice <= 10.0)) {

                success = false;
            }
            waterPrice = Double.parseDouble(waterPriceString);
            if(!(waterPrice >= 0.1 && waterPrice <= 10.0)) {

                success = false;
            }
            elementsAtPage = Integer.parseInt(elementsAtPageString);
            if(!(elementsAtPage >= 5 && elementsAtPage <= 100)) {

                success = false;
            }

        } catch (NumberFormatException e) {

            success = false;
        }

        if (success) {

            //Einstellungen speichern
            SettingsEditor se = Application.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            final int finalSunriseOffset = sunriseOffset;
            final int finalSunsetOffset = sunsetOffset;
            final double finalLongitude = longitude;
            final double finalLatitude = latitude;
            final boolean finalFbActive = fbActive;
            final String finalFbAddress = fbAddress;
            final String finalFbUser = fbUser;
            final String finalFbPassword = fbPassword;
            final double finalElectricPrice = electricPrice;
            final double finalWaterPrice = waterPrice;
            final int finalElementsAtPage = elementsAtPage;

            Optional<IntegerSetting> sunriseOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNRISE_OFFSET);
            sunriseOffsetOptional.ifPresent(setting -> setting.setValue(finalSunriseOffset));
            Optional<IntegerSetting> sunsetOffsetOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_SUNSET_OFFSET);
            sunsetOffsetOptional.ifPresent(setting -> setting.setValue(finalSunsetOffset));
            Optional<DoubleSetting> latitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LATITUDE);
            latitudeOptional.ifPresent(setting -> setting.setValue(finalLatitude));
            Optional<DoubleSetting> longitudeOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_LONGITUDE);
            longitudeOptional.ifPresent(setting -> setting.setValue(finalLongitude));

            Optional<BooleanSetting> fbActiveOptional = se.getBooleanSetting(SettingsEditor.AUTOMATION_FB_ACTIVE);
            fbActiveOptional.ifPresent(setting -> setting.setValue(finalFbActive));
            if(fbActive) {

                Optional<StringSetting> fbAddressOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_ADDRESS);
                fbAddressOptional.ifPresent(setting -> setting.setValue(finalFbAddress));
                Optional<StringSetting> fbUserOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_USER);
                fbUserOptional.ifPresent(setting -> setting.setValue(finalFbUser));
                Optional<StringSetting> fbPasswordOptional = se.getStringSetting(SettingsEditor.AUTOMATION_FB_PASSWORD);
                fbPasswordOptional.ifPresent(setting -> setting.setValue(finalFbPassword));
            }

            Optional<DoubleSetting> electricPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_ELECTRIC_PRICE);
            electricPriceOptional.ifPresent(setting -> setting.setValue(finalElectricPrice));
            Optional<DoubleSetting> waterPriceOptional = se.getDoubleSetting(SettingsEditor.AUTOMATION_ENERGY_WATER_PRICE);
            waterPriceOptional.ifPresent(setting -> setting.setValue(finalWaterPrice));

            Optional<IntegerSetting> elementsAtPageOptional = se.getIntegerSetting(SettingsEditor.AUTOMATION_PAGNATION_ELEMENTS_AT_PAGE);
            elementsAtPageOptional.ifPresent(setting -> setting.setValue(finalElementsAtPage));

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
