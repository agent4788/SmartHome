package net.kleditzsch.SmartHome.util.api.printer;

import net.kleditzsch.SmartHome.util.snmp.SimpleSnmpClient;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * liest den Status eines SNMP fähigen Druckers aus
 * (bezogen auf einen Brother MFC Farblaserdrucker)
 */
public class PrinterState {

    /**
     * Allgemeine Daten
     */
    private static final String OID_RAW_DATA = "1.3.6.1.2.1.43.18.1.1";
    private static final String OID_CONSOLE_DATA = "1.3.6.1.2.1.43.16";
    private static final String OID_CONTACT = "1.3.6.1.2.1.1.4.0";
    private static final String OID_LOCATION = "1.3.6.1.2.1.1.6.0";

    /**
     * Drucker Basisdaten
     */
    private static final String OID_SERIAL_NUMBER = "1.3.6.1.2.1.43.5.1.1.17.1";
    private static final String OID_SYSTEM_DESCRIPTION = "1.3.6.1.2.1.1.1.0";
    private static final String OID_DEVICE_DESCRIPTION = "1.3.6.1.2.1.25.3.2.1.3.1";
    private static final String OID_DEVICE_STATE = "1.3.6.1.2.1.25.3.2.1.5.1";
    private static final String OID_DEVICE_ERRORS = "1.3.6.1.2.1.25.3.2.1.6.1";
    private static final String OID_UPTIME = "1.3.6.1.2.1.1.3.0";
    private static final String OID_MEMORY_SIZE = "1.3.6.1.2.1.25.2.2.0";
    private static final String OID_PAGE_COUNT = "1.3.6.1.2.1.43.10.2.1.4.1.1";
    private static final String OID_HARDWARE_ADDRESS = "1.3.6.1.2.1.2.2.1.6.1";

    /**
     * Papierfach
     */
    private static final String OID_TRAY_1_NAME = "1.3.6.1.2.1.43.8.2.1.13.1.1";
    private static final String OID_TRAY_1_CAPACITY = "1.3.6.1.2.1.43.8.2.1.9.1.1";
    private static final String OID_TRAY_1_LEVEL = "1.3.6.1.2.1.43.8.2.1.10.1.1";
    private static final String OID_TRAY_2_NAME = "1.3.6.1.2.1.43.8.2.1.13.1.2";
    private static final String OID_TRAY_2_CAPACITY = "1.3.6.1.2.1.43.8.2.1.9.1.2";
    private static final String OID_TRAY_2_LEVEL = "1.3.6.1.2.1.43.8.2.1.10.1.2";
    private static final String OID_TRAY_3_NAME = "1.3.6.1.2.1.43.8.2.1.13.1.3";
    private static final String OID_TRAY_3_CAPACITY = "1.3.6.1.2.1.43.8.2.1.9.1.3";
    private static final String OID_TRAY_3_LEVEL = "1.3.6.1.2.1.43.8.2.1.10.1.3";
    private static final String OID_TRAY_4_NAME = "1.3.6.1.2.1.43.8.2.1.13.1.4";
    private static final String OID_TRAY_4_CAPACITY = "1.3.6.1.2.1.43.8.2.1.9.1.4";
    private static final String OID_TRAY_4_LEVEL = "1.3.6.1.2.1.43.8.2.1.10.1.4";

    /**
     * Toner Kapazitäten
     */
    private static final String OID_BLACK_TONER_CARTRIDGE_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.1";
    private static final String OID_BLACK_TONER_CARTRIDGE_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.1";
    private static final String OID_BLACK_TONER_CARTRIDGE_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.1";
    private static final String OID_CYAN_TONER_CARTRIDGE_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.2";
    private static final String OID_CYAN_TONER_CARTRIDGE_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.2";
    private static final String OID_CYAN_TONER_CARTRIDGE_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.2";
    private static final String OID_MAGENTA_TONER_CARTRIDGE_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.3";
    private static final String OID_MAGENTA_TONER_CARTRIDGE_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.3";
    private static final String OID_MAGENTA_TONER_CARTRIDGE_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.3";
    private static final String OID_YELLOW_TONER_CARTRIDGE_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.4";
    private static final String OID_YELLOW_TONER_CARTRIDGE_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.4";
    private static final String OID_YELLOW_TONER_CARTRIDGE_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.4";
    private static final String OID_WASTE_TONER_BOX_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.5";
    private static final String OID_WASTE_TONER_BOX_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.5";
    private static final String OID_WASTE_TONER_BOX_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.5";
    private static final String OID_BELT_UNIT_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.6";
    private static final String OID_BELT_UNIT_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.6";
    private static final String OID_BELT_UNIT_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.6";
    private static final String OID_BLACK_DRUM_UNIT_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.7";
    private static final String OID_BLACK_DRUM_UNIT_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.7";
    private static final String OID_BLACK_DRUM_UNIT_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.7";
    private static final String OID_CYAN_DRUM_UNIT_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.8";
    private static final String OID_CYAN_DRUM_UNIT_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.8";
    private static final String OID_CYAN_DRUM_UNIT_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.8";
    private static final String OID_MAGENTA_DRUM_UNIT_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.9";
    private static final String OID_MAGENTA_DRUM_UNIT_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.9";
    private static final String OID_MAGENTA_DRUM_UNIT_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.9";
    private static final String OID_YELLOW_DRUM_UNIT_NAME = "1.3.6.1.2.1.43.11.1.1.6.1.10";
    private static final String OID_YELLOW_DRUM_UNIT_CAPACITY = "1.3.6.1.2.1.43.11.1.1.8.1.10";
    private static final String OID_YELLOW_DRUM_UNIT_LEVEL = "1.3.6.1.2.1.43.11.1.1.9.1.10";

    /**
     * Füllstand einer Komponente
     */
    public class Level {

        /**
         * Name der Komponente
         */
        private String name = "";

        /**
         * gesamte Kapazität
         */
        private int capacity = 0;

        /**
         * verbleibende Kapazität
         */
        private int level = 0;

        /**
         * @param name Name
         * @param capacity Gesamte Kapazität
         * @param level verbleibende Kapazität
         */
        public Level(String name, int capacity, int level) {
            this.name = name;
            this.capacity = capacity;
            this.level = level;
        }

        /**
         * gibt den Namen der Komponente zurück
         *
         * @return Name
         */
        public String getName() {
            return name;
        }

        /**
         * gibt die gesamte Kapazität zurück
         *
         * @return gesamte Kapazität
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * gibt die verbleibende Kapazität zurück
         *
         * @return verbleibende Kapazität
         */
        public int getLevel() {
            return level;
        }

        /**
         * gibt die prozentuale verbleibende Kapazität zurück
         *
         * @return prozentuale verbleibende Kapazität
         */
        public double getPercentageLevel() {

            if(capacity < 0 || level < 0) {

                return (level / capacity * 100);
            }
            return (level * 100 / capacity);
        }
    }

    /**
     * IP Adresse des Druckers
     */
    private String address = "udp:0.0.0.0/161";

    /**
     * Liste aller OIDs
     */
    private List<String> oids = null;

    /**
     * Liste aller verfügbaren SNMP Variablen
     */
    private Map<String, Variable> snmpData = null;

    /**
     * @param address Adresse des Druckers
     */
    public PrinterState(String address) {

        this.address = "udp:" + address + "/161";

        oids = new ArrayList<>();

        oids.add(OID_CONTACT);
        oids.add(OID_LOCATION);

        oids.add(OID_SERIAL_NUMBER);
        oids.add(OID_SYSTEM_DESCRIPTION);
        oids.add(OID_DEVICE_DESCRIPTION);
        oids.add(OID_DEVICE_ERRORS);
        oids.add(OID_DEVICE_STATE);
        oids.add(OID_UPTIME);
        oids.add(OID_MEMORY_SIZE);
        oids.add(OID_PAGE_COUNT);
        oids.add(OID_HARDWARE_ADDRESS);

        oids.add(OID_TRAY_1_NAME);
        oids.add(OID_TRAY_1_CAPACITY);
        oids.add(OID_TRAY_1_LEVEL);
        oids.add(OID_TRAY_2_NAME);
        oids.add(OID_TRAY_2_CAPACITY);
        oids.add(OID_TRAY_2_LEVEL);
        oids.add(OID_TRAY_3_NAME);
        oids.add(OID_TRAY_3_CAPACITY);
        oids.add(OID_TRAY_3_LEVEL);
        oids.add(OID_TRAY_4_NAME);
        oids.add(OID_TRAY_4_CAPACITY);
        oids.add(OID_TRAY_4_LEVEL);

        oids.add(OID_BLACK_TONER_CARTRIDGE_NAME);
        oids.add(OID_BLACK_TONER_CARTRIDGE_CAPACITY);
        oids.add(OID_BLACK_TONER_CARTRIDGE_LEVEL);
        oids.add(OID_CYAN_TONER_CARTRIDGE_NAME);
        oids.add(OID_CYAN_TONER_CARTRIDGE_CAPACITY);
        oids.add(OID_CYAN_TONER_CARTRIDGE_LEVEL);
        oids.add(OID_MAGENTA_TONER_CARTRIDGE_NAME);
        oids.add(OID_MAGENTA_TONER_CARTRIDGE_CAPACITY);
        oids.add(OID_MAGENTA_TONER_CARTRIDGE_LEVEL);
        oids.add(OID_YELLOW_TONER_CARTRIDGE_NAME);
        oids.add(OID_YELLOW_TONER_CARTRIDGE_CAPACITY);
        oids.add(OID_YELLOW_TONER_CARTRIDGE_LEVEL);
        oids.add(OID_WASTE_TONER_BOX_NAME);
        oids.add(OID_WASTE_TONER_BOX_CAPACITY);
        oids.add(OID_WASTE_TONER_BOX_LEVEL);
        oids.add(OID_BELT_UNIT_NAME);
        oids.add(OID_BELT_UNIT_CAPACITY);
        oids.add(OID_BELT_UNIT_LEVEL);
        oids.add(OID_BLACK_DRUM_UNIT_NAME);
        oids.add(OID_BLACK_DRUM_UNIT_CAPACITY);
        oids.add(OID_BLACK_DRUM_UNIT_LEVEL);
        oids.add(OID_CYAN_DRUM_UNIT_NAME);
        oids.add(OID_CYAN_DRUM_UNIT_CAPACITY);
        oids.add(OID_CYAN_DRUM_UNIT_LEVEL);
        oids.add(OID_MAGENTA_DRUM_UNIT_NAME);
        oids.add(OID_MAGENTA_DRUM_UNIT_CAPACITY);
        oids.add(OID_MAGENTA_DRUM_UNIT_LEVEL);
        oids.add(OID_YELLOW_DRUM_UNIT_NAME);
        oids.add(OID_YELLOW_DRUM_UNIT_CAPACITY);
        oids.add(OID_YELLOW_DRUM_UNIT_LEVEL);
    }

    /**
     * fordert alle relevanten Daten vom Drucker an und speichert sie zwischen
     *
     * @throws IOException
     */
    public void requestData() throws IOException {

        SimpleSnmpClient snmp = new SimpleSnmpClient(address);
        List<OID> oidList = oids.stream().map(OID::new).collect(Collectors.toList());
        snmpData = snmp.getList(oidList);
    }

    /**
     * gibt den Kontakt zurück
     *
     * @return Kontakt
     */
    public Optional<String> getContact() {

        return getVar(OID_CONTACT);
    }

    /**
     * gibt den Standort zurück
     *
     * @return Standort
     */
    public Optional<String> getLocation() {

        return getVar(OID_LOCATION);
    }

    /**
     * gibt die Seriennummer zurück
     *
     * @return Seriennummer
     */
    public Optional<String> getSerialNumber() {

        return getVar(OID_SERIAL_NUMBER);
    }

    /**
     * gibt die Systembeschreibung zurück
     *
     * @return
     */
    public Optional<String> getSystemDescription() {

        return getVar(OID_SYSTEM_DESCRIPTION);
    }

    /**
     * gibt die Gerätebeschreibung zurück (Handelsname)
     *
     * @return Gerätebeschreibung
     */
    public Optional<String> getDeviceDescription() {

        return getVar(OID_DEVICE_DESCRIPTION);
    }

    /**
     * gibt eventuelle Fehler zurück
     *
     * @return eventuelle Fehler
     */
    public Optional<String> getDeviceErrors() {

        return getVar(OID_DEVICE_ERRORS);
    }

    /**
     * gibt den Gerätestatus zurück
     *
     * @return Gerätestatus
     */
    public Optional<String> getDeviceState() {

        return getVar(OID_DEVICE_STATE);
    }

    /**
     * gibt die Laufzeit zurück
     *
     * @return Laufzeit
     */
    public Optional<String> getUptime() {

        return getVar(OID_UPTIME);
    }

    /**
     * gibt die größe des Speichers zurück
     *
     * @return größe des Speichers
     */
    public Optional<String> getMemorySize() {

        return getVar(OID_MEMORY_SIZE);
    }

    /**
     * gibt die Anzahl der Geruckten Seiten zurück
     *
     * @return Anzahl der Geruckten Seiten
     */
    public Optional<String> getPageCount() {

        return getVar(OID_PAGE_COUNT);
    }

    /**
     * gibt die Hardwareadresse zurück
     *
     * @return Hardwareadresse
     */
    public Optional<String> getHardwareAddress() {

        return getVar(OID_HARDWARE_ADDRESS);
    }

    /**
     * fragt eine SNMP Variable anhand ihrer OID ab und gibt sie sofern vorhanden zurück
     *
     * @param oid OID
     * @return Wert
     */
    private Optional<String> getVar(String oid) {

        if(snmpData.keySet().contains(oid)) {

            return Optional.of(snmpData.get(oid).toString());
        }
        return Optional.empty();
    }

    /**
     * gibt den Füllstand des ersten Papierfaches zurück
     *
     * @return Füllstand des ersten Papierfaches
     */
    public Optional<Level> getTray1Level() {

        return getLevel(OID_TRAY_1_NAME, OID_TRAY_1_CAPACITY, OID_TRAY_1_LEVEL);
    }

    /**
     * gibt den Füllstand des zweiten Papierfaches zurück
     *
     * @return Füllstand des zweiten Papierfaches
     */
    public Optional<Level> getTray2Level() {

        return getLevel(OID_TRAY_2_NAME, OID_TRAY_2_CAPACITY, OID_TRAY_2_LEVEL);
    }

    /**
     * gibt den Füllstand des dritten Papierfaches zurück
     *
     * @return Füllstand des dritten Papierfaches
     */
    public Optional<Level> getTray3Level() {

        return getLevel(OID_TRAY_3_NAME, OID_TRAY_3_CAPACITY, OID_TRAY_3_LEVEL);
    }

    /**
     * gibt den Füllstand des vierten Papierfaches zurück
     *
     * @return Füllstand des vierten Papierfaches
     */
    public Optional<Level> getTray4Level() {

        return getLevel(OID_TRAY_4_NAME, OID_TRAY_4_CAPACITY, OID_TRAY_4_LEVEL);
    }

    /**
     * gibt den Füllstand des schwarzen Toners zurück
     *
     * @return Füllstand des schwarzen Toners
     */
    public Optional<Level> getBlackTonerCartridgeLevel() {

        return getLevel(OID_BLACK_TONER_CARTRIDGE_NAME, OID_BLACK_TONER_CARTRIDGE_CAPACITY, OID_BLACK_TONER_CARTRIDGE_LEVEL);
    }

    /**
     * gibt den Füllstand des blauen Toners zurück
     *
     * @return Füllstand des blauen Toners
     */
    public Optional<Level> getCyanTonerCartridgeLevel() {

        return getLevel(OID_CYAN_TONER_CARTRIDGE_NAME, OID_CYAN_TONER_CARTRIDGE_CAPACITY, OID_CYAN_TONER_CARTRIDGE_LEVEL);
    }

    /**
     * gibt den Füllstand des roten Toners zurück
     *
     * @return Füllstand des roten Toners
     */
    public Optional<Level> getMagentaTonerCartridgeLevel() {

        return getLevel(OID_MAGENTA_TONER_CARTRIDGE_NAME, OID_MAGENTA_TONER_CARTRIDGE_CAPACITY, OID_MAGENTA_TONER_CARTRIDGE_LEVEL);
    }

    /**
     * gibt den Füllstand des gelben Toners zurück
     *
     * @return Füllstand des gelben Toners
     */
    public Optional<Level> getYellowTonerCartridgeLevel() {

        return getLevel(OID_YELLOW_TONER_CARTRIDGE_NAME, OID_YELLOW_TONER_CARTRIDGE_CAPACITY, OID_YELLOW_TONER_CARTRIDGE_LEVEL);
    }

    /**
     * gibt den Füllstand der Abfallbox zurück
     *
     * @return Füllstand der Abfallbox
     */
    public Optional<Level> getWasteTonerBoxLevel() {

        return getLevel(OID_WASTE_TONER_BOX_NAME, OID_WASTE_TONER_BOX_CAPACITY, OID_WASTE_TONER_BOX_LEVEL);
    }

    /**
     * gibt den Füllstand der Bandeinheit zurück
     *
     * @return Füllstand der Bandeinheit
     */
    public Optional<Level> getBeltUnitLevel() {

        return getLevel(OID_BELT_UNIT_NAME, OID_BELT_UNIT_CAPACITY, OID_BELT_UNIT_LEVEL);
    }

    /**
     * gibt den Füllstand der schwarzen Trommeleinheit zurück
     *
     * @return Füllstand der schwarzen Trommeleinheit
     */
    public Optional<Level> getBlackDrumUnitLevel() {

        return getLevel(OID_BLACK_DRUM_UNIT_NAME, OID_BLACK_DRUM_UNIT_CAPACITY, OID_BLACK_DRUM_UNIT_LEVEL);
    }

    /**
     * gibt den Füllstand der blauen Trommeleinheit zurück
     *
     * @return Füllstand der blauen Trommeleinheit
     */
    public Optional<Level> getCyanDrumUnitLevel() {

        return getLevel(OID_CYAN_DRUM_UNIT_NAME, OID_CYAN_DRUM_UNIT_CAPACITY, OID_CYAN_DRUM_UNIT_LEVEL);
    }

    /**
     * gibt den Füllstand der roten Trommeleinheit zurück
     *
     * @return Füllstand der roten Trommeleinheit
     */
    public Optional<Level> getMagentaDrumUnitLevel() {

        return getLevel(OID_MAGENTA_DRUM_UNIT_NAME, OID_MAGENTA_DRUM_UNIT_CAPACITY, OID_MAGENTA_DRUM_UNIT_LEVEL);
    }

    /**
     * gibt den Füllstand der gelben Trommeleinheit zurück
     *
     * @return Füllstand der gelben Trommeleinheit
     */
    public Optional<Level> getYellowDrumUnitLevel() {

        return getLevel(OID_YELLOW_DRUM_UNIT_NAME, OID_YELLOW_DRUM_UNIT_CAPACITY, OID_YELLOW_DRUM_UNIT_LEVEL);
    }

    /**
     * gibt den Füllstand einer Komponente zurück
     *
     * @param oidName OID des Namens
     * @param oidCapacity OID der Kapazität
     * @param oidLevel OID des Füllstandes
     * @return Füllstand einer Komponente
     */
    private Optional<Level> getLevel(String oidName, String oidCapacity, String oidLevel) {

        Optional<String> optionalName = getVar(oidName);
        Optional<String> optionalCapacity = getVar(oidCapacity);
        Optional<String> optionalLevel = getVar(oidLevel);

        if(optionalName.isPresent() && optionalCapacity.isPresent() && optionalLevel.isPresent()) {

            String name = optionalName.get();
            int capacity = Integer.parseInt(optionalCapacity.get());
            int level = Integer.parseInt(optionalLevel.get());

            return Optional.of(new Level(name, capacity, level));
        }
        return Optional.empty();
    }
}