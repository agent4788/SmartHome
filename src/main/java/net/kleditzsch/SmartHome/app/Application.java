package net.kleditzsch.SmartHome.app;

import net.kleditzsch.SmartHome.util.api.avm.Device.Components.Alert;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.PowerMeter;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.Switch;
import net.kleditzsch.SmartHome.util.api.avm.Device.Components.TemperatureSensor;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.util.api.avm.FritzBoxSmarthome;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Startpunkt der Anwendung
 *
 * @author Oliver Kleditzsch
 */
public class Application {

    public static void main(String[] args) throws IOException, InterruptedException, JDOMException {

        //Logger Konfigurieren
        LoggerUtil.setLogLevel(Level.FINEST);
        LoggerUtil.setLogFileLevel(Level.OFF);

        FritzBoxSmarthome fritzBoxSmarthome = new FritzBoxSmarthome("192.168.115.250", "oliver", "1988oliver");
        fritzBoxSmarthome.listDevices()
                .stream()
                .filter(dev -> dev.getIdentifier().equals("08761 0174723"))
                .forEach(shd -> {

                    System.out.println("################# Element #################");

                    System.out.println("Name: " + shd.getName());
                    System.out.println("Identifier: " + shd.getIdentifier());
                    System.out.println("Hersteller: " + shd.getManufacturer());
                    System.out.println("Hersteller Bezeichung: " + shd.getProductName());
                    System.out.println("Erreichbar: " + shd.isPresent());
                    System.out.println("Firmware: " + shd.getFirmwareVersion());

                    Optional<Switch> oSwitch = shd.getSwitch();
                    oSwitch.ifPresent(aSwitch -> {

                        System.out.println("");
                        System.out.println("Switch --------------------------------------");
                        System.out.println("Status: " + aSwitch.getState());
                        System.out.println("Modus: " + aSwitch.getMode());
                        System.out.println("Lock: " + aSwitch.isLock());
                        System.out.println("Gerätelock: " + aSwitch.isDeviceLock());
                    });

                    Optional<PowerMeter> optionalPowerMeter = shd.getPowerMeter();
                    optionalPowerMeter.ifPresent(powerMeter -> {

                        System.out.println("");
                        System.out.println("Energiemesser -------------------------------");
                        System.out.println("Leistung: " + powerMeter.getPower());
                        System.out.println("Energie: " + powerMeter.getEnergy());
                    });

                    Optional<TemperatureSensor> optionalTemperatureSensor = shd.getTemperatureSensor();
                    optionalTemperatureSensor.ifPresent(temperatureSensor -> {

                        System.out.println("");
                        System.out.println("Temperatur ----------------------------------");
                        System.out.println("Temperatur: " + temperatureSensor.getTemperature());
                        System.out.println("Offset: " + temperatureSensor.getOffset());
                        System.out.println("Temperatur with Offset: " + (temperatureSensor.getTemperature() + temperatureSensor.getOffset()));
                    });
                });
    }
}
