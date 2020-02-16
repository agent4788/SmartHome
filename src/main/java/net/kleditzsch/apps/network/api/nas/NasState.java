package net.kleditzsch.apps.network.api.nas;

import net.kleditzsch.SmartHome.utility.snmp.SimpleSnmpClient;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * liest den Status einer SNMP fähigen NAS aus
 * (bezogen auf eine Synology NAS)
 */
public class NasState {

    /**
     * Allgemeine Daten
     */
    private static final String OID_SYSTEM_STATUS = "1.3.6.1.4.1.6574.1.1.0";        //1 -> Normal; 2 -> Fehler
    private static final String OID_SYSTEM_TEMPERATURE = "1.3.6.1.4.1.6574.1.2.0";   //in °C
    private static final String OID_SYSTEM_POWER_STATE = "1.3.6.1.4.1.6574.1.3.0";   //1 -> Normal; 2 -> Fehler
    private static final String OID_SYSTEM_FAN_STATE = "1.3.6.1.4.1.6574.1.4.1.0";   //1 -> Normal; 2 -> Fehler
    private static final String OID_CPU_FAN_STATE = "1.3.6.1.4.1.6574.1.4.2.0";      //1 -> Normal; 2 -> Fehler
    private static final String OID_MODEL_NAME = "1.3.6.1.4.1.6574.1.5.1.0";
    private static final String OID_SERIAL_NUMBER = "1.3.6.1.4.1.6574.1.5.2.0";
    private static final String OID_VERSION = "1.3.6.1.4.1.6574.1.5.3.0";
    private static final String OID_UPGRADE_AVAILABLE = "1.3.6.1.4.1.6574.1.5.4.0";  //1 -> verfügbar
                                                                                     //2 -> nicht verfügbar
                                                                                     //3 -> Verbinden
                                                                                     //4 -> getrennt
                                                                                     //5 -> andere

    private static final String OID_DISC_1_ID = "1.3.6.1.4.1.6574.2.1.1.2.0";
    private static final String OID_DISC_1_MODEL = "1.3.6.1.4.1.6574.2.1.1.3.0";
    private static final String OID_DISC_1_TYPE = "1.3.6.1.4.1.6574.2.1.1.4.0";
    private static final String OID_DISC_1_STATE = "1.3.6.1.4.1.6574.2.1.1.5.0";    //1 -> Normal
                                                                                    //2 -> Initalisiert
                                                                                    //3 -> Nicht Initalisiert
                                                                                    //4 -> Partitionsfehler
                                                                                    //5 -> Defekt
    private static final String OID_DISC_1_TEMPERATURE = "1.3.6.1.4.1.6574.2.1.1.6.0";
    private static final String OID_DISC_2_ID = "1.3.6.1.4.1.6574.2.1.1.2.1";
    private static final String OID_DISC_2_MODEL = "1.3.6.1.4.1.6574.2.1.1.3.1";
    private static final String OID_DISC_2_TYPE = "1.3.6.1.4.1.6574.2.1.1.4.1";
    private static final String OID_DISC_2_STATE = "1.3.6.1.4.1.6574.2.1.1.5.1";
    private static final String OID_DISC_2_TEMPERATURE = "1.3.6.1.4.1.6574.2.1.1.6.1";

    private static final String OID_RAID_1_NAME = "1.3.6.1.4.1.6574.3.1.1.2.0";
    private static final String OID_RAID_1_STATE = "1.3.6.1.4.1.6574.3.1.1.3.0";
    private static final String OID_RAID_1_FREE = "1.3.6.1.4.1.6574.3.1.1.4.0";
    private static final String OID_RAID_1_SIZE = "1.3.6.1.4.1.6574.3.1.1.5.0";
    private static final String OID_RAID_2_NAME = "1.3.6.1.4.1.6574.3.1.1.2.1";
    private static final String OID_RAID_2_STATE = "1.3.6.1.4.1.6574.3.1.1.3.1";
    private static final String OID_RAID_2_FREE = "1.3.6.1.4.1.6574.3.1.1.4.1";
    private static final String OID_RAID_2_SIZE = "1.3.6.1.4.1.6574.3.1.1.5.1";

    private static final String OID_CPU_UTILISATION_USER = "1.3.6.1.4.1.2021.11.9.0";
    private static final String OID_CPU_UTILISATION_SYSTEM = "1.3.6.1.4.1.2021.11.10.0";
    private static final String OID_CPU_UTILISATION_IDLE = "1.3.6.1.4.1.2021.11.11.0";

    private static final String OID_MEM_REAL_SIZE = "1.3.6.1.4.1.2021.4.5.0";
    private static final String OID_MEM_REAL_FREE = "1.3.6.1.4.1.2021.4.6.0";
    private static final String OID_MEM_SWAP_SIZE = "1.3.6.1.4.1.2021.4.3.0";
    private static final String OID_MEM_SWAP_FREE = "1.3.6.1.4.1.2021.4.4.0";

    private static final String OID_NET_INTERFACE_1_NAME = "1.3.6.1.2.1.31.1.1.1.1.1";
    private static final String OID_NET_INTERFACE_1_IN = "1.3.6.1.2.1.31.1.1.1.6.1";
    private static final String OID_NET_INTERFACE_1_OUT = "1.3.6.1.2.1.31.1.1.1.10.1";

    private static final String OID_NET_INTERFACE_2_NAME = "1.3.6.1.2.1.31.1.1.1.1.3";
    private static final String OID_NET_INTERFACE_2_IN = "1.3.6.1.2.1.31.1.1.1.6.3";
    private static final String OID_NET_INTERFACE_2_OUT = "1.3.6.1.2.1.31.1.1.1.10.3";

    /**
     * Festplatten Daten
     */
    public class Disc {

        private String id = "";
        private String model = "";
        private String type ="";
        private int state = 1;
        private int temperature = 0;

        public Disc(String id, String model, String type, int state, int temperature) {
            this.id = id;
            this.model = model;
            this.type = type;
            this.state = state;
            this.temperature = temperature;
        }

        public String getId() {
            return id;
        }

        public String getModel() {
            return model;
        }

        public String getType() {
            return type;
        }

        public int getState() {
            return state;
        }

        public int getTemperature() {
            return temperature;
        }
    }

    /**
     * Status der RAID Volums
     */
    public class Raid {

        private String name = "";
        private int state = 1;
        private long free = 0;
        private long size = 0;

        public Raid(String name, int state, long free, long size) {
            this.name = name;
            this.state = state;
            this.free = free;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public int getState() {
            return state;
        }

        public long getFree() {
            return free;
        }

        public long getSize() {
            return size;
        }

        public long getUsed() {
            return size - free;
        }

        public double getUtilisation() {
            if(size > 0) {

                return getUsed() * 100L / size;
            }
            return 100.0;
        }

        public int getIntUtilisation() {
            if(size > 0) {

                return Long.valueOf(getUsed() * 100L / size).intValue();
            }
            return 100;
        }
    }

    /**
     * CPU Auslastung
     */
    public class CpuUtilisation {

        private int user = 0;
        private int system = 0;
        private int idle = 0;

        public CpuUtilisation(int user, int system, int idle) {
            this.user = user;
            this.system = system;
            this.idle = idle;
        }

        public int getUser() {
            return user;
        }

        public int getSystem() {
            return system;
        }

        public int getIdle() {
            return idle;
        }

        public int getUtilisation() {
            return user + system;
        }
    }

    /**
     * Speicherinfo
     */
    public class Memory {

        private long size = 0;
        private long free = 0;

        public Memory(long size, long free) {
            this.size = size;
            this.free = free;
        }

        public long getSize() {
            return size;
        }

        public long getFree() {
            return free;
        }

        public long getUsed() {
            return size - free;
        }

        public double getUtilisation() {
            if(size > 0) {

                return getUsed() * 100L / size;
            }
            return 100.0;
        }

        public int getIntUtilisation() {
            if(size > 0) {

                return Long.valueOf(getUsed() * 100L / size).intValue();
            }
            return 100;
        }
    }

    /**
     * Info Netzwerkschnittstelle
     */
    public class NetworkInterface {

        private String name = "";
        private long in = 0;
        private long out = 0;

        public NetworkInterface(String name, long in, long out) {
            this.name = name;
            this.in = in;
            this.out = out;
        }

        public String getName() {
            return name;
        }

        public long getIn() {
            return in;
        }

        public long getOut() {
            return out;
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
     * @param address Adresse der NAS
     */
    public NasState(String address) {

        this.address = "udp:" + address + "/161";

        oids = new ArrayList<>();

        oids.add(OID_SYSTEM_STATUS);
        oids.add(OID_SYSTEM_TEMPERATURE);
        oids.add(OID_SYSTEM_POWER_STATE);
        oids.add(OID_SYSTEM_FAN_STATE);
        oids.add(OID_CPU_FAN_STATE);
        oids.add(OID_MODEL_NAME);
        oids.add(OID_SERIAL_NUMBER);
        oids.add(OID_VERSION);
        oids.add(OID_UPGRADE_AVAILABLE);

        oids.add(OID_DISC_1_ID);
        oids.add(OID_DISC_1_MODEL);
        oids.add(OID_DISC_1_TYPE);
        oids.add(OID_DISC_1_STATE);
        oids.add(OID_DISC_1_TEMPERATURE);
        oids.add(OID_DISC_2_ID);
        oids.add(OID_DISC_2_MODEL);
        oids.add(OID_DISC_2_TYPE);
        oids.add(OID_DISC_2_STATE);
        oids.add(OID_DISC_2_TEMPERATURE);

        oids.add(OID_RAID_1_NAME);
        oids.add(OID_RAID_1_STATE);
        oids.add(OID_RAID_1_FREE);
        oids.add(OID_RAID_1_SIZE);
        oids.add(OID_RAID_2_NAME);
        oids.add(OID_RAID_2_STATE);
        oids.add(OID_RAID_2_FREE);
        oids.add(OID_RAID_2_SIZE);

        oids.add(OID_CPU_UTILISATION_USER);
        oids.add(OID_CPU_UTILISATION_SYSTEM);
        oids.add(OID_CPU_UTILISATION_IDLE);
        oids.add(OID_MEM_REAL_SIZE);
        oids.add(OID_MEM_REAL_FREE);
        oids.add(OID_MEM_SWAP_SIZE);
        oids.add(OID_MEM_SWAP_FREE);
        oids.add(OID_NET_INTERFACE_1_NAME);
        oids.add(OID_NET_INTERFACE_1_IN);
        oids.add(OID_NET_INTERFACE_1_OUT);
        oids.add(OID_NET_INTERFACE_2_NAME);
        oids.add(OID_NET_INTERFACE_2_IN);
        oids.add(OID_NET_INTERFACE_2_OUT);
    }

    /**
     * fordert alle relevanten Daten der NAS an und speichert sie zwischen
     *
     * @throws IOException
     */
    public void requestData() throws IOException {

        SimpleSnmpClient snmp = new SimpleSnmpClient(address);
        List<OID> oidList = oids.stream().map(OID::new).collect(Collectors.toList());
        snmpData = snmp.getList(oidList);
    }

    /**
     * gibt den System Status zurück
     *
     * @return System Status
     */
    public Optional<Integer> getSystemState() {

        return getIntVar(OID_SYSTEM_STATUS);
    }

    /**
     * gibt die System Temperatur zurück
     *
     * @return System Temperatur
     */
    public Optional<Integer> getSystemTemrature() {

        return getIntVar(OID_SYSTEM_TEMPERATURE);
    }

    /**
     * gibt den System Energieversorgungsstatus zurück
     *
     * @return System Energieversorgungsstatus
     */
    public Optional<Integer> getSystemPowerState() {

        return getIntVar(OID_SYSTEM_POWER_STATE);
    }

    /**
     * gibt den Status des System Lüfters zurück
     *
     * @return Status des System Lüfters
     */
    public Optional<Integer> getSystemFanState() {

        return getIntVar(OID_SYSTEM_FAN_STATE);
    }

    /**
     * gibt den Status des CPU Lüfters zurück
     *
     * @return Status des CPU Lüfters
     */
    public Optional<Integer> getCpuFanState() {

        return getIntVar(OID_CPU_FAN_STATE);
    }

    /**
     * gibt den Model Name der NAS zurück
     *
     * @return Model Name der NAS
     */
    public Optional<String> getModelName() {

        return getVar(OID_MODEL_NAME);
    }

    /**
     * gibt die Seriennummer der NAS zurück
     *
     * @return Seriennummer der NAS
     */
    public Optional<String> getSerialNumber() {

        return getVar(OID_SERIAL_NUMBER);
    }

    /**
     * gibt die Software Version der NAS zurück
     *
     * @return Software Versio der NAS
     */
    public Optional<String> getSoftwareVersion() {

        return getVar(OID_VERSION);
    }

    /**
     * gibt den Update Status der NAS zurück
     *
     * @return Update Status der NAS
     */
    public Optional<Integer> getUpdateState() {

        return getIntVar(OID_UPGRADE_AVAILABLE);
    }

    /**
     * gibt die Festplatteninformation der ersten Festplatte zurück
     *
     * @return Festplatteninformation
     */
    public Optional<Disc> getDisc1State() {

        return getDiscState(OID_DISC_1_ID, OID_DISC_1_MODEL, OID_DISC_1_TYPE, OID_DISC_1_STATE, OID_DISC_1_TEMPERATURE);
    }

    /**
     * gibt die Festplatteninformation der zweiten Festplatte zurück
     *
     * @return Festplatteninformation
     */
    public Optional<Disc> getDisc2State() {

        return getDiscState(OID_DISC_2_ID, OID_DISC_2_MODEL, OID_DISC_2_TYPE, OID_DISC_2_STATE, OID_DISC_2_TEMPERATURE);
    }

    /**
     * gibt die Festplatteninformation einer Festplatte zurück
     *
     * @param oidID OID der ID
     * @param oidModel OID des Modesl
     * @param oidType OID des Typs
     * @param oidState OID des Status
     * @param oidTemperature OID der Temperatur
     * @return Festplatteninformation
     */
    private Optional<Disc> getDiscState(String oidID, String oidModel, String oidType, String oidState, String oidTemperature) {

        Optional<String> idOptional = getVar(oidID);
        Optional<String> modelOptional = getVar(oidModel);
        Optional<String> typeOptional = getVar(oidType);
        Optional<Integer> stateOptional = getIntVar(oidState);
        Optional<Integer> temperatureOptional = getIntVar(oidTemperature);

        if(idOptional.isPresent() && modelOptional.isPresent() && typeOptional.isPresent() && stateOptional.isPresent() && temperatureOptional.isPresent()) {

            return Optional.of(new Disc(idOptional.get(), modelOptional.get(), typeOptional.get(), stateOptional.get(), temperatureOptional.get()));
        }
        return Optional.empty();
    }

    /**
     * gibt den Status des ersten Raid Volums zurück
     *
     * @return Status des Raid Volums
     */
    public Optional<Raid> getRaid1State() {

        return getRaid(OID_RAID_1_NAME, OID_RAID_1_STATE, OID_RAID_1_FREE, OID_RAID_1_SIZE);
    }

    /**
     * gibt den Status des zweiten Raid Volums zurück
     *
     * @return Status des Raid Volums
     */
    public Optional<Raid> getRaid2State() {

        return getRaid(OID_RAID_2_NAME, OID_RAID_2_STATE, OID_RAID_2_FREE, OID_RAID_2_SIZE);
    }

    /**
     * gibt den Status des ersten Raid Volums zurück
     *
     * @param oidName OID Name
     * @param oidState OID Status
     * @param oidFree OID Freier Kapazität
     * @param oidSize OID Gesamt Kapazität
     * @return Status des Raid Volums
     */
    private Optional<Raid> getRaid(String oidName, String oidState, String oidFree, String oidSize) {

        Optional<String> nameOptional = getVar(oidName);
        Optional<Integer> stateOptional = getIntVar(oidState);
        Optional<Long> freeOptional = getLongVar(oidFree);
        Optional<Long> sizeOptional = getLongVar(oidSize);

        if(nameOptional.isPresent() && stateOptional.isPresent() && freeOptional.isPresent() && sizeOptional.isPresent()) {

            return Optional.of(new Raid(nameOptional.get(), stateOptional.get(), freeOptional.get(), sizeOptional.get()));
        }
        return Optional.empty();
    }

    /**
     * gibt die CPU Auslastung zurück
     *
     * @return CPU Auslastung
     */
    public Optional<CpuUtilisation> getCpuUtilisation() {

        Optional<Integer> userOptional = getIntVar(OID_CPU_UTILISATION_USER);
        Optional<Integer> systemOptional = getIntVar(OID_CPU_UTILISATION_SYSTEM);
        Optional<Integer> idleOptional = getIntVar(OID_CPU_UTILISATION_IDLE);

        if(userOptional.isPresent() && systemOptional.isPresent() && idleOptional.isPresent()) {

            return Optional.of(new CpuUtilisation(userOptional.get(), systemOptional.get(), idleOptional.get()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Auslastung des Arbeitsspeichers zurück
     *
     * @return Auslastung des Arbeitsspeichers
     */
    public Optional<Memory> getSystemMemory() {

        return getMemoryInfo(OID_MEM_REAL_SIZE, OID_MEM_REAL_FREE);
    }

    /**
     * gibt die Auslasutung des SWAP Speichers zurück
     *
     * @return Auslasutung des SWAP Speichers
     */
    public Optional<Memory> getSwapMemory() {

        return getMemoryInfo(OID_MEM_SWAP_SIZE, OID_MEM_SWAP_FREE);
    }

    /**
     * gibt die Auslastung des Speichers zurück
     *
     * @param oidSize OID Gesamte Kapazität
     * @param oidFree OID Freier Speicher
     * @return Auslastung des Speichers
     */
    private Optional<Memory> getMemoryInfo(String oidSize, String oidFree) {

        Optional<Long> sizeOptional = getLongVar(oidSize);
        Optional<Long> freeOptional = getLongVar(oidFree);

        if(sizeOptional.isPresent() && freeOptional.isPresent()) {

            return Optional.of(new Memory(sizeOptional.get() * 1024, freeOptional.get() * 1024));
        }
        return Optional.empty();
    }

    /**
     * gibt die Schnittestellen Daten der ersten Netzwerkschnittstelle zurück
     *
     * @return Schnittestellen Daten der Netzwerkschnittstelle
     */
    public Optional<NetworkInterface> getNetworkInterface1() {

        return getNetworkInterfaceInfo(OID_NET_INTERFACE_1_NAME, OID_NET_INTERFACE_1_IN, OID_NET_INTERFACE_1_OUT);
    }

    /**
     * gibt die Schnittestellen Daten der zweiten Netzwerkschnittstelle zurück
     *
     * @return Schnittestellen Daten der Netzwerkschnittstelle
     */
    public Optional<NetworkInterface> getNetworkInterface2() {

        return getNetworkInterfaceInfo(OID_NET_INTERFACE_2_NAME, OID_NET_INTERFACE_2_IN, OID_NET_INTERFACE_2_OUT);
    }

    /**
     * gibt die Schnittestellen Daten der Netzwerkschnittstelle zurück
     *
     * @param oidName OID Name
     * @param oidIn OID In
     * @param oidOut OID Out
     * @return Schnittestellen Daten der Netzwerkschnittstelle
     */
    private Optional<NetworkInterface> getNetworkInterfaceInfo(String oidName, String oidIn, String oidOut) {

        Optional<String> nameOptional = getVar(oidName);
        Optional<Long> inOptional = getLongVar(oidIn);
        Optional<Long> outOptional = getLongVar(oidOut);

        if(nameOptional.isPresent() && inOptional.isPresent() && outOptional.isPresent()) {

            return Optional.of(new NetworkInterface(nameOptional.get(), inOptional.get(), outOptional.get()));
        }
        return Optional.empty();
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
     * fragt eine SNMP Variable anhand ihrer OID ab und gibt sie sofern vorhanden zurück
     *
     * @param oid OID
     * @return Wert
     */
    private Optional<Integer> getIntVar(String oid) {

        if(snmpData.keySet().contains(oid)) {

            return Optional.of(Integer.parseInt(snmpData.get(oid).toString().trim()));
        }
        return Optional.empty();
    }

    /**
     * fragt eine SNMP Variable anhand ihrer OID ab und gibt sie sofern vorhanden zurück
     *
     * @param oid OID
     * @return Wert
     */
    private Optional<Long> getLongVar(String oid) {

        if(snmpData.keySet().contains(oid)) {

            return Optional.of(Long.parseLong(snmpData.get(oid).toString().trim()));
        }
        return Optional.empty();
    }
}
