package net.kleditzsch.apps.automation.api.tplink;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

/**
 * TP-Link HS100
 *
 * @author Oliver Kleditzsch
 */
public class HS110 extends HS100 {

    private static final String COMMAND_ENERGY = "{\"emeter\":{\"get_realtime\":null}}";

    /**
     * Energiedaten
     */
    public class EnergyData {

        /**
         * aktuelle Spannung in Volt
         */
        private double nowVoltage;

        /**
         * aktueller Strom in Ampere
         */
        private double nowCurrent;

        /**
         * aktuelle Leistung in Watt
         */
        private double nowPower;

        /**
         * Energieverbrauch in Kilo Watt Stunden
         */
        private double energy;

        public EnergyData() {
        }

        /**
         * @param nowVoltage aktuelle Spannung in Volt
         * @param nowCurrent aktueller Strom in Ampere
         * @param nowPower aktuelle Leistung in Watt
         * @param energy Energieverbrauch in Kilo Watt Stunden
         */
        public EnergyData(double nowVoltage, double nowCurrent, double nowPower, double energy) {
            this.nowVoltage = nowVoltage;
            this.nowCurrent = nowCurrent;
            this.nowPower = nowPower;
            this.energy = energy;
        }

        /**
         * gibt die aktuelle Spannung zurück
         *
         * @return aktuelle Spannung in Volt
         */
        public double getNowVoltage() {
            return nowVoltage;
        }

        /**
         * setzt die aktuelle Spannung
         *
         * @param nowVoltage aktuelle Spannung in Volt
         */
        private void setNowVoltage(double nowVoltage) {
            this.nowVoltage = nowVoltage;
        }

        /**
         * gibt den aktuellen Strom zurück
         *
         * @return aktueller Strom in Ampere
         */
        public double getNowCurrent() {
            return nowCurrent;
        }

        /**
         * setzt den aktuellen Strom
         *
         * @param nowCurrent aktueller Strom in Ampere
         */
        private void setNowCurrent(double nowCurrent) {
            this.nowCurrent = nowCurrent;
        }

        /**
         * gibt die aktuelle Leistung zurück
         *
         * @return aktuelle Leistung in Watt
         */
        public double getNowPower() {
            return nowPower;
        }

        /**
         * setzt die aktuelle Leistung
         *
         * @param nowPower aktuelle Leistung in Watt
         */
        private void setNowPower(double nowPower) {
            this.nowPower = nowPower;
        }

        /**
         * gibt den Energieverbrauch zurück
         *
         * @return Energieverbrauch in Kilo Watt Stunden
         */
        public double getEnergy() {
            return energy;
        }

        /**
         * setzt den Energieverbrauch
         *
         * @param energy Energieverbrauch in Kilo Watt Stunden
         */
        private void setEnergy(double energy) {
            this.energy = energy;
        }
    }

    /**
     * @param ip IP Adresse
     */
    public HS110(String ip) {
        super(ip);
    }

    /**
     * @param ip IP Adresse
     * @param port TCP Port Nummer
     */
    public HS110(String ip, int port) {
        super(ip, port);
    }

    /**
     * gint die Energiedaten zurück
     *
     * @return Energiedaten
     * @throws IOException
     */
    public EnergyData getEnergyData() throws IOException {

        EnergyData energyData = new EnergyData();
        String jsonData = sendCommand(COMMAND_ENERGY);
        if(jsonData.length() > 0) {

            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            JsonObject energyDataJson = jo.get("emeter").getAsJsonObject().get("get_realtime").getAsJsonObject();

            energyData.setNowCurrent(energyDataJson.get("current").getAsDouble());
            energyData.setNowVoltage(energyDataJson.get("voltage").getAsDouble());
            energyData.setNowPower(energyDataJson.get("power").getAsDouble());
            energyData.setEnergy(energyDataJson.get("total").getAsDouble());
        }
        return energyData;
    }

}