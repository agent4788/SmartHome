package net.kleditzsch.apps.automation.api.avm;

import net.kleditzsch.apps.automation.api.avm.Device.Components.*;
import net.kleditzsch.apps.automation.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;

/**
 * Fritz Box SmartHome HTTP API
 */
public class FritzBoxSmarthome {

    /**
     * Fritz Box Verbindungs Handler
     */
    private FritzBoxHandler fritzBoxHandler;

    /**
     * @param password Passwort
     */
    public FritzBoxSmarthome(String password) {

        fritzBoxHandler = new FritzBoxHandler("fritz.box", "", password);
    }

    /**
     * @param username Benutzername
     * @param password Passwort
     */
    public FritzBoxSmarthome(String username, String password) {

        fritzBoxHandler = new FritzBoxHandler("fritz.box", username, password);
    }

    /**
     * @param fritzBoxAddress Fritz Box Adresse
     * @param username Benutzername
     * @param password Passwort
     */
    public FritzBoxSmarthome(String fritzBoxAddress, String username, String password) {

        fritzBoxHandler = new FritzBoxHandler(fritzBoxAddress, username, password);
    }

    /**
     * neuen Login anstoßen
     */
    public void reLogin() throws InterruptedException {

        fritzBoxHandler.login();
    }

    /**
     * gibt eine Liste mit den Geräte IDs zurück
     *
     * @return Set mit den Geräte IDs
     */
    public List<String> getDeviceList() throws IOException, InterruptedException {

        String response = fritzBoxHandler.sendHttpRequest("/webservices/homeautoswitch.lua?switchcmd=getswitchlist");
        return Arrays.asList(response.split(","));
    }

    public List<SmarthomeDevice> listDevices() throws IOException, InterruptedException {

        List<SmarthomeDevice> smartHomeDevices = new ArrayList<>();
        List<String> devices = getDeviceList();
        if(devices.size() > 0) {

            String response = fritzBoxHandler.sendHttpRequest("/webservices/homeautoswitch.lua?switchcmd=getdevicelistinfos");

            //XML Daten auswerten
            try {

                Document doc = new SAXBuilder().build(new StringReader(response));
                Element root = doc.getRootElement();
                List<Element> deviceList = root.getChildren();
                for (Element e : deviceList) {

                    //Geräte
                    if (e.getName().equals("device")) {

                        String identifier = e.getAttribute("identifier").getValue().replaceAll("\\s", "");
                        Integer functionbitmask = Integer.parseInt(e.getAttribute("functionbitmask").getValue());
                        String fwversion = e.getAttribute("fwversion").getValue();
                        String manufacturer = e.getAttribute("manufacturer").getValue();
                        String productname = e.getAttribute("productname").getValue();

                        boolean present = (Integer.parseInt(e.getChild("present").getValue()) == 1);
                        String name = e.getChild("name").getValue();

                        Alert alert = null;
                        HKR hkr = null;
                        PowerMeter powerMeter = null;
                        Switch aSwitch = null;
                        TemperatureSensor temperatureSensor = null;

                        //Alarmsensor
                        if ((functionbitmask & 16) == 16) {

                            Element alertElement = e.getChild("alert");
                            alert = new Alert(
                                    identifier,
                                    fritzBoxHandler,
                                    alertElement.getChild("state").getValue().equals("1")
                            );
                        }

                        //HKR
                        if ((functionbitmask & 64) == 64) {

                            //TODO implementieren
                            hkr = new HKR();
                        }

                        //EnergyMeter
                        if ((functionbitmask & 128) == 128) {

                            Element powerMeterElement = e.getChild("powermeter");
                            double power, energy;
                            try {

                                power = Double.parseDouble(powerMeterElement.getChild("power").getValue());
                            } catch (NumberFormatException e1) {

                                power = 0.0;
                            }
                            try {

                                energy = Double.parseDouble(powerMeterElement.getChild("energy").getValue());
                            } catch (NumberFormatException e2) {

                                energy = 0.0;
                            }
                            powerMeter = new PowerMeter(
                                    identifier,
                                    fritzBoxHandler,
                                    power,
                                    energy
                            );
                        }

                        //Temperatur Sensor
                        if ((functionbitmask & 256) == 256) {

                            Element temperatureElement = e.getChild("temperature");
                            double temperature, offset;
                            try {

                                temperature = Double.parseDouble(temperatureElement.getChild("celsius").getValue()) / 10;
                            } catch (NumberFormatException e1) {

                                temperature = 0.0;
                            }
                            try {

                                offset = Double.parseDouble(temperatureElement.getChild("offset").getValue());
                            } catch (NumberFormatException e2) {

                                offset = 0.0;
                            }
                            temperatureSensor = new TemperatureSensor(
                                    identifier,
                                    fritzBoxHandler,
                                    temperature,
                                    offset
                            );
                        }

                        //Switch Socket
                        if ((functionbitmask & 512) == 512) {

                            Element switchElement = e.getChild("switch");
                            aSwitch = new Switch(
                                    identifier,
                                    fritzBoxHandler,
                                    (switchElement.getChild("state").getValue().equals("1") ? Switch.STATE.ON : Switch.STATE.OFF),
                                    (switchElement.getChild("mode").getValue().equals("auto") ? Switch.MODE.AUTO : Switch.MODE.MANUALLY),
                                    switchElement.getChild("lock").getValue().equals("1"),
                                    switchElement.getChild("devicelock").getValue().equals("1")
                            );
                        }

                        //DECT Repeater
                        /*if ((functionbitmask & 1024) == 1024) {

                            //nop
                        }*/

                        SmarthomeDevice shd = new SmarthomeDevice(
                                identifier,
                                fritzBoxHandler,
                                fwversion,
                                manufacturer,
                                productname,
                                functionbitmask,
                                present,
                                name,
                                alert,
                                hkr,
                                powerMeter,
                                aSwitch,
                                temperatureSensor
                        );
                        smartHomeDevices.add(shd);
                    }
                }
            } catch (JDOMException e) {

                LoggerUtil.getLogger(this.getClass()).log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        }
        return smartHomeDevices;
    }
}
