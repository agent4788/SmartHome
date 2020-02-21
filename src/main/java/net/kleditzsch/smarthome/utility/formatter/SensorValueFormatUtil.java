package net.kleditzsch.smarthome.utility.formatter;

import net.kleditzsch.smarthome.utility.datetime.TimeUtil;
import net.kleditzsch.applications.automation.model.device.sensor.*;
import net.kleditzsch.applications.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.applications.automation.model.device.sensor.virtual.*;

import java.math.BigInteger;

/**
 * Formatiert die Sensorwerte zur lesbaren Anzeige
 */
public abstract class SensorValueFormatUtil {

    /**
     * formatiert den Sensorwert zur Anzeige
     *
     * @param sensorValue Sensorwert
     * @return lesbare Zeichenkette
     */
    public static String format(SensorValue sensorValue) {

        String formatedValue = "";
        if (sensorValue instanceof ActualPowerValue) {

            double value = ((ActualPowerValue) sensorValue).getActualPower();
            if((value / 1000000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((value / 1000000), 2) + " kW";
            } else if((value / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((value / 1000), 2) + " W";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(value, 1) + " mW";
            }
        } else if (sensorValue instanceof AirPressureValue) {

            double airPressure = ((AirPressureValue) sensorValue).getAirPressure();
            formatedValue = NumberFormatUtil.numberFormat(airPressure, 0) + " hPa";
        } else if (sensorValue instanceof AltitudeValue) {

            double altitude = ((AltitudeValue) sensorValue).getAltitude();
            formatedValue = NumberFormatUtil.numberFormat(altitude, 1) + " m";
        } else if (sensorValue instanceof BatteryLevelValue) {

            double batteryLevel = ((BatteryLevelValue) sensorValue).getBatteryLevel();
            formatedValue = NumberFormatUtil.numberFormat(batteryLevel, 0) + " %";
        } else if (sensorValue instanceof BiStateValue) {

            boolean state = ((BiStateValue) sensorValue).getState();
            String trueText = ((BiStateValue) sensorValue).getTrueText();
            String falseText = ((BiStateValue) sensorValue).getFalseText();

            formatedValue = state ? trueText : falseText;
        } else if (sensorValue instanceof CounterValue) {

            BigInteger value = ((CounterValue) sensorValue).getCounterValue();
            return NumberFormatUtil.numberFormat(value, 0);
        } else if (sensorValue instanceof CurrentValue) {

            double current = ((CurrentValue) sensorValue).getCurrent();
            if((current / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((current / 1000), 2) + " A";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(current, 0) + " mA";
            }
        } else if (sensorValue instanceof DistanceValue) {

            double distance = ((DistanceValue) sensorValue).getDistanceWithOffset();
            if((distance / 1000000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((distance / 1000000), 2) + " m";
            } else if((distance / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((distance / 1000), 2) + " m";
            } else if((distance / 10) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((distance / 10), 1) + " cm";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(distance, 0) + " mm";
            }
        } else if (sensorValue instanceof DurationValue) {

            long duration = ((DurationValue) sensorValue).getDuration();
            formatedValue = TimeUtil.formatSeconds(duration, true);
        } else if (sensorValue instanceof EnergyValue) {

            double energy = ((EnergyValue) sensorValue).getEnergy();
            if((energy / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((energy / 1000), 2) + " kWh";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(energy, 0) + " Wh";
            }
        } else if (sensorValue instanceof GasAmountValue) {

            double gasAmount = ((GasAmountValue) sensorValue).getGasAmount();
            if((gasAmount / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((gasAmount / 1000), 2) + " m³";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(gasAmount, 0) + " dm³";
            }
        } else if (sensorValue instanceof HumidityValue) {

            double humidity = ((HumidityValue) sensorValue).getHumidity();
            formatedValue = NumberFormatUtil.numberFormat(humidity, 1) + " %";
        } else if (sensorValue instanceof InputValue) {

            boolean state = ((InputValue) sensorValue).getState();
            return state ? "1" : "0";
        } else if (sensorValue instanceof LightIntensityValue) {

            double lightIntensity = ((LightIntensityValue) sensorValue).getLightIntensity();
            formatedValue = NumberFormatUtil.numberFormat(lightIntensity, 0) + " %";
        } else if (sensorValue instanceof LiveBitValue) {

            boolean state = ((LiveBitValue) sensorValue).getState();
            String trueText = ((LiveBitValue) sensorValue).getTrueText();
            String falseText = ((LiveBitValue) sensorValue).getFalseText();

            formatedValue = state ? trueText : falseText;
        } else if (sensorValue instanceof MoistureValue) {

            double moisure = ((MoistureValue) sensorValue).getMoisture();
            formatedValue = NumberFormatUtil.numberFormat(moisure, 0) + " %";
        } else if (sensorValue instanceof StringValue) {

            return ((StringValue) sensorValue).getString();
        } else if (sensorValue instanceof TemperatureValue) {

            double temperature = ((TemperatureValue) sensorValue).getTemperatureWithOffset();
            formatedValue = NumberFormatUtil.numberFormat(temperature, 1) + " °C";
        } else if (sensorValue instanceof UserAtHomeValue) {

            boolean userAtHome = ((UserAtHomeValue) sensorValue).isAtHome();
            return userAtHome ? "zu Hause" : "nicht zu Hause";
        } else if (sensorValue instanceof VoltageValue) {

            double voltage = ((VoltageValue) sensorValue).getVoltage();
            if((voltage / 1000000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((voltage / 1000000), 2) + " kV";
            } else if((voltage / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((voltage / 1000), 1) + " V";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(voltage, 0) + " mV";
            }
        } else if (sensorValue instanceof WaterAmountValue) {

            double waterAmount = ((WaterAmountValue) sensorValue).getWaterAmount();
            if((waterAmount / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((waterAmount / 1000), 2) + " m³";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(waterAmount, 0) + " dm³";
            }
        } else if (sensorValue instanceof VirtualActualPowerValue) {

            double value = ((VirtualActualPowerValue) sensorValue).getSum();
            if((value / 1000000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((value / 1000000), 2) + " kW";
            } else if((value / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((value / 1000), 2) + " W";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(value, 1) + " mW";
            }
        } else if (sensorValue instanceof VirtualEnergyValue) {

            double energy = ((VirtualEnergyValue) sensorValue).getSum();
            if((energy / 1000000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((energy / 1000000), 2) + " kWh";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(energy / 1000, 0) + " Wh";
            }
        } else if (sensorValue instanceof VirtualGasAmountValue) {

            double gasAmount = ((VirtualGasAmountValue) sensorValue).getSum();
            if((gasAmount / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((gasAmount / 1000), 2) + " m³";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(gasAmount, 0) + " dm³";
            }
        } else if (sensorValue instanceof VirtualLightIntensityValue) {

            double lightIntensity = ((VirtualLightIntensityValue) sensorValue).getAverage();
            formatedValue = NumberFormatUtil.numberFormat(lightIntensity, 0) + " %";
        } else if (sensorValue instanceof VirtualTemperatureValue) {

            double temperature = ((VirtualTemperatureValue) sensorValue).getAverage();
            formatedValue = NumberFormatUtil.numberFormat(temperature, 1) + " °C";
        } else if (sensorValue instanceof VirtualWaterAmountValue) {

            double waterAmount = ((VirtualWaterAmountValue) sensorValue).getSum();
            if((waterAmount / 1000) > 1.0) {

                formatedValue = NumberFormatUtil.numberFormat((waterAmount / 1000), 2) + " m³";
            } else {

                formatedValue = NumberFormatUtil.numberFormat(waterAmount, 0) + " dm³";
            }
        } else {

            formatedValue = "unbekannt";
        }
        return formatedValue;
    }

    /**
     * formatiert den Sensortyp zur Anzeige
     *
     * @param sensorValue Sensorwert
     * @return lesbare Zeichenkette
     */
    public static String formatType(SensorValue sensorValue) {

        if (sensorValue instanceof ActualPowerValue) {

            return "aktuelle Energie";
        } else if (sensorValue instanceof AirPressureValue) {

            return "Luftdruck";
        } else if (sensorValue instanceof AltitudeValue) {

            return "Standorthöhe";
        } else if (sensorValue instanceof BatteryLevelValue) {

            return "Akku Ladezustand";
        } else if (sensorValue instanceof BiStateValue) {

            return "Doppelstatus";
        } else if (sensorValue instanceof CounterValue) {

            return "Zähler";
        } else if (sensorValue instanceof CurrentValue) {

            return "Strom";
        } else if (sensorValue instanceof DistanceValue) {

            return "Entfernung";
        } else if (sensorValue instanceof DurationValue) {

            return "Laufzeit";
        } else if (sensorValue instanceof EnergyValue) {

            return "Energieverbrauch";
        } else if (sensorValue instanceof GasAmountValue) {

            return "Gas Menge";
        } else if (sensorValue instanceof HumidityValue) {

            return "Luftfeuchte";
        } else if (sensorValue instanceof InputValue) {

            return "Eingang";
        } else if (sensorValue instanceof LightIntensityValue) {

            return "Lichtstärke";
        } else if (sensorValue instanceof LiveBitValue) {

            return "Lebenszeichen";
        } else if (sensorValue instanceof MoistureValue) {

            return "Feuchtigkeit";
        } else if (sensorValue instanceof StringValue) {

            return "Zeichenkette";
        } else if (sensorValue instanceof TemperatureValue) {

            return "Temperatur";
        } else if (sensorValue instanceof UserAtHomeValue) {

            return "Benutzer zu Hause";
        } else if (sensorValue instanceof VoltageValue) {

            return "Spannung";
        } else if (sensorValue instanceof WaterAmountValue) {

            return "Wassermenge";
        } else if (sensorValue instanceof VirtualActualPowerValue) {

            return "Virtuelle aktuelle Energie";
        } else if (sensorValue instanceof VirtualEnergyValue) {

            return "Virtueller Energieverbrauch";
        } else if (sensorValue instanceof VirtualGasAmountValue) {

            return "Virtuelle Gas Menge";
        } else if (sensorValue instanceof VirtualLightIntensityValue) {

            return "Virtuelle Lichstärke";
        } else if (sensorValue instanceof VirtualTemperatureValue) {

            return "Virtuelle Temperatur";
        } else if (sensorValue instanceof VirtualWaterAmountValue) {

            return "Virtuelle Wasser Menge";
        } else {

            return "unbekannt";
        }
    }
}
