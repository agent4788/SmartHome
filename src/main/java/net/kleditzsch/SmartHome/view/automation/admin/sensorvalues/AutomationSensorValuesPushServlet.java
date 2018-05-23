package net.kleditzsch.SmartHome.view.automation.admin.sensorvalues;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.*;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.util.formatter.DateTimeFormatUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSensorValuesPushServlet extends HttpServlet {

    /**
     * Empfängt Sensorwerte und speichert diese in der Datenbank ab
     *
     * Parameter:
     * Typ (type):      Typ ID
     * Identifier (id): Identifizierung
     * Wert (value):    Messwert
     *
     * Typen:
     *   1 : aktuelle Energie
     *      Wert: Milliwartt (mW) -> double
     *   2 : Luftdruck
     *      Luftruck (hPa) -> double
     *   3 : Standorthöhe
     *      Höhe über NN (m) -> double
     *   4 : Akku Ladezustand
     *      Ladezustand (%) -> double
     *   5 : Doppelstatus
     *      Status (wahr/falsch) -> boolean
     *   6 : Zähler
     *      Zahl (+ addieren, - subtrahieren) -> int
     *   7 : Strom
     *      Stromstärke (mA) -> double
     *   8 : Anstand
     *      Längenmaß (mm) -> double
     *   9 : Laufzeit
     *      Laufzeit (s) -> long
     *      [add Addieren (s) -> int]
     *  10 : Energieverbrauch
     *      Verbrauch (kWh) -> double
     *  11 : Gasmenge
     *      Verbrauch (l oder dm³) -> double
     *  12 : Luftfeuchtigkeit
     *      Luftfeuchte (%) -> double
     *  13 : Eingang
     *      Status (wahr/falsch) -> boolean
     *  14 : Lichstärke
     *      Lichtstärke (%) -> double
     *  15 : Lebenszeichen
     *      Lebenszeichen -> boolean
     *  16 : Feuchtigkeit
     *      Feuchtigkeit (%) -> double
     *  17 : Zeichenkette
     *      Zeichenkette (1 bis 50 Zeichen) -> String
     *  18 : Temperatur
     *      Temperatur (°C) -> double
     *  19 : Benutzer zu Hause
     *      Lebenszeichen -> boolean
     *  20 : Spannung
     *      Spannung (mV) -> double
     *  21 : Wassermenge
     *      Verbrauch (l oder dm³) -> double
     *
     * @param req Anfrageobjekt
     * @param resp Antwortobjekt
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template rendern
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);

        boolean success = true;
        if (req.getParameter("type") != null
                && req.getParameter("id") != null
                && req.getParameter("id").matches("^[a-zA-Z0-9\\-]{3,50}$")
                && req.getParameter("value") != null) {

            try {

                //Allgemeine Daten
                int typeId = Integer.parseInt(req.getParameter("type"));
                String identifier = req.getParameter("id");
                String value = req.getParameter("value");

                double doubleValue;
                int intValue;
                long longValue;
                boolean booleanValue;
                Optional<SensorValue> sensorValueOptional;

                SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
                lock.lock();

                //Sensorwert speichern
                try {

                    switch (typeId) {

                        case 1: //aktuelle Energie

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0 || doubleValue > 100_000_000) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof ActualPowerValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                ActualPowerValue sensorValue = (ActualPowerValue) sensorValueOptional.get();
                                sensorValue.pushActualPower(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                ActualPowerValue sensorValue = new ActualPowerValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushActualPower(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 2: //Luftdruck

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 500 || doubleValue > 1500) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof AirPressureValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                AirPressureValue sensorValue = (AirPressureValue) sensorValueOptional.get();
                                sensorValue.pushAirPressure(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                AirPressureValue sensorValue = new AirPressureValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushAirPressure(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 3: //Standorthöhe

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < -10_000 || doubleValue > 10_000) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof AltitudeValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                AltitudeValue sensorValue = (AltitudeValue) sensorValueOptional.get();
                                sensorValue.pushAltitude(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                AltitudeValue sensorValue = new AltitudeValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushAltitude(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 4: //Akku Ladezustand

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0  || doubleValue > 100) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof BatteryLevelValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                BatteryLevelValue sensorValue = (BatteryLevelValue) sensorValueOptional.get();
                                sensorValue.pushBatteryLevel(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                BatteryLevelValue sensorValue = new BatteryLevelValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushBatteryLevel(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 5: //Doppelstatus

                            booleanValue = value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true");

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof BiStateValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                BiStateValue sensorValue = (BiStateValue) sensorValueOptional.get();
                                sensorValue.pushState(booleanValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                BiStateValue sensorValue = new BiStateValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.setTrueText("on");
                                sensorValue.setFalseText("off");
                                sensorValue.pushState(booleanValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 6: //Zähler

                            longValue = Long.parseLong(value);

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof CounterValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                CounterValue sensorValue = (CounterValue) sensorValueOptional.get();
                                BigInteger counterValue = sensorValue.getCounterValue();
                                sensorValue.pushConterValue(counterValue.add(BigInteger.valueOf(longValue)));
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                CounterValue sensorValue = new CounterValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                BigInteger counterValue = sensorValue.getCounterValue();
                                sensorValue.pushConterValue(counterValue.add(BigInteger.valueOf(longValue)));

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 7: //Strom

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0  || doubleValue > 100_000_000) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof CurrentValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                CurrentValue sensorValue = (CurrentValue) sensorValueOptional.get();
                                sensorValue.pushCurrent(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                CurrentValue sensorValue = new CurrentValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushCurrent(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 8: //Abstand

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < -100_000  || doubleValue > 100_000) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof DistanceValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                DistanceValue sensorValue = (DistanceValue) sensorValueOptional.get();
                                sensorValue.pushDistance(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                DistanceValue sensorValue = new DistanceValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushDistance(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 9: //Laufzeit

                            boolean add = false;
                            intValue = 0;
                            longValue = 0;
                            if (req.getParameter("add") != null) {

                                intValue = Integer.parseInt(req.getParameter("add"));
                                add = true;
                                if(intValue < 0) {

                                    success = false;
                                    break;
                                }
                            } else {

                                longValue = Long.parseLong(value);
                                if(longValue < 0) {

                                    success = false;
                                    break;
                                }
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof DurationValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                DurationValue sensorValue = (DurationValue) sensorValueOptional.get();
                                if(add) {

                                    long durationValue = sensorValue.getDuration();
                                    sensorValue.pushDuration(durationValue + intValue);
                                } else {

                                    sensorValue.pushDuration(longValue);
                                }
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                DurationValue sensorValue = new DurationValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                if(add) {

                                    long durationValue = sensorValue.getDuration();
                                    sensorValue.pushDuration(durationValue + intValue);
                                } else {

                                    sensorValue.pushDuration(longValue);
                                }

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 10: //Energie

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof EnergyValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                EnergyValue sensorValue = (EnergyValue) sensorValueOptional.get();
                                sensorValue.pushEnergy(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                EnergyValue sensorValue = new EnergyValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushEnergy(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 11: //Gasmenge

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof GasAmountValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                GasAmountValue sensorValue = (GasAmountValue) sensorValueOptional.get();
                                sensorValue.pushGasAmount(sensorValue.getGasAmount() + doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                GasAmountValue sensorValue = new GasAmountValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushGasAmount(sensorValue.getGasAmount() + doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 12: //Luftfeuchte

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0 || doubleValue > 100) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof HumidityValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                HumidityValue sensorValue = (HumidityValue) sensorValueOptional.get();
                                sensorValue.pushHumidity(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                HumidityValue sensorValue = new HumidityValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushHumidity(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 13: //Eingang

                            booleanValue = value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true");

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof InputValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                InputValue sensorValue = (InputValue) sensorValueOptional.get();
                                sensorValue.pushState(booleanValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                InputValue sensorValue = new InputValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushState(booleanValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 14: //Lichtstärke

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0 || doubleValue > 100) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof LightIntensityValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                LightIntensityValue sensorValue = (LightIntensityValue) sensorValueOptional.get();
                                sensorValue.pushLightIntensity(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                LightIntensityValue sensorValue = new LightIntensityValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushLightIntensity(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 15: //Lebenszeichen

                            booleanValue = value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true");

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof LiveBitValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                LiveBitValue sensorValue = (LiveBitValue) sensorValueOptional.get();
                                sensorValue.pushState(booleanValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                LiveBitValue sensorValue = new LiveBitValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushState(booleanValue);
                                sensorValue.setTimeout(900_000);
                                sensorValue.setTrueText("on");
                                sensorValue.setFalseText("off");

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 16: //Feuchtigkeit

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0 || doubleValue > 100) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof MoistureValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                MoistureValue sensorValue = (MoistureValue) sensorValueOptional.get();
                                sensorValue.pushMoisture(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                MoistureValue sensorValue = new MoistureValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushMoisture(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 17: //Zeichenkette

                            if(value.length() < 1 || value.length() > 50) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof StringValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                StringValue sensorValue = (StringValue) sensorValueOptional.get();
                                sensorValue.pushString(value);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                StringValue sensorValue = new StringValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushString(value);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 18: //Temperatur

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < -274 || doubleValue > 10_000) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof TemperatureValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                TemperatureValue sensorValue = (TemperatureValue) sensorValueOptional.get();
                                sensorValue.pushTemperature(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                TemperatureValue sensorValue = new TemperatureValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushTemperature(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 19: //Benutzer zu Hause

                            booleanValue = value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true");

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof UserAtHomeValue
                                    && ((UserAtHomeValue) sensorValueOptional.get()).isUseExternalDataSource()
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                UserAtHomeValue sensorValue = (UserAtHomeValue) sensorValueOptional.get();
                                sensorValue.pushAtHome(booleanValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                UserAtHomeValue sensorValue = new UserAtHomeValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushAtHome(booleanValue);
                                sensorValue.setUseExternalDataSource(true);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 20: //Spannung

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof VoltageValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                VoltageValue sensorValue = (VoltageValue) sensorValueOptional.get();
                                sensorValue.pushVoltage(doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                VoltageValue sensorValue = new VoltageValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushVoltage(doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        case 21: //Wassermenge

                            doubleValue = Double.parseDouble(value);
                            if(doubleValue < 0) {

                                success = false;
                                break;
                            }

                            sensorValueOptional = sensorEditor.getByIdentifier(identifier);
                            if(sensorValueOptional.isPresent()
                                    && sensorValueOptional.get() instanceof WaterAmountValue
                                    && !sensorValueOptional.get().isSystemValue()) {

                                //wert Speichern
                                WaterAmountValue sensorValue = (WaterAmountValue) sensorValueOptional.get();
                                sensorValue.pushWaterAmount(sensorValue.getWaterAmount() + doubleValue);
                            } else if (!sensorValueOptional.isPresent()) {

                                //neuen Sensorwert erzeugen und speichern
                                WaterAmountValue sensorValue = new WaterAmountValue(ID.create(), identifier, identifier);
                                sensorValue.setDescription("Automatisch erstellt am " + DateTimeFormatUtil.format(LocalDateTime.now()));
                                sensorValue.pushWaterAmount(sensorValue.getWaterAmount() + doubleValue);

                                sensorEditor.getData().add(sensorValue);
                            } else {

                                success = false;
                            }
                            break;
                        default:

                            success = false;
                            break;
                    }
                } catch (Exception e) {

                    success = false;
                }

                lock.unlock();

            } catch (NumberFormatException e) {

                success = false;
            }
        } else {

            success = false;
        }
        resp.getWriter().append(success ? "1" : "0");
    }
}
