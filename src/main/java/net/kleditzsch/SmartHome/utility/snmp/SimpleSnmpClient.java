package net.kleditzsch.SmartHome.utility.snmp;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.*;

/**
 * Vereinfacht das Handling mit der SNMP4J API
 */
public class SimpleSnmpClient {

    /**
     * SNMP Objekt
     */
    private Snmp snmp = null;

    /**
     * IP Adresse des SNMP Agenten
     */
    private String address = "udp:0.0.0.0/161";

    /**
     * Ziel Objekt
     */
    private Target target = null;

    /**
     * @param address IP Adresse des SNMP Agenten
     */
    public SimpleSnmpClient(String address) throws IOException {
        this.address = address;
        start();
    }

    /**
     * Initalisiert den Clienten
     */
    private void start() throws IOException {

        target = getTarget();

        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    /**
     * Generiert das Zielobjekt
     *
     * @return Zielobjekt
     */
    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    /**
     * gibt den Wert der OID als String zurück
     *
     * @param oid OID
     * @return Wert als Zeichenkette
     */
    public Variable get(OID oid) throws IOException {

        List<OID> oids = new ArrayList();
        oids.add(oid);
        ResponseEvent event = get(oids);
        return event.getResponse().get(0).getVariable();
    }

    /**
     * fragt eine Liste von OIDs ab
     *
     * @param oids Liste der OIDs
     * @return Antwort Liste
     */
    public Map<String, Variable> getList(List<OID> oids) throws IOException {

        ResponseEvent event = get(oids);

        Map<String, Variable> vars = new HashMap<>();
        if(event.getResponse() != null) {

            for(int i = 0; i < event.getResponse().size(); i++) {

                vars.put(event.getResponse().get(i).getOid().toDottedString(), event.getResponse().get(i).getVariable());
            }
        }
        return vars;
    }

    /**
     * fragt eine Liste von OIDs ab
     *
     * @param oids Array aus OIDs
     * @return Antwortobjekt
     */
    private ResponseEvent get(List<OID> oids) throws IOException {

        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, target, null);
        if(event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    /**
     * listet alle Variablen einens Teilbaumes
     *
     * @param oid OID
     * @return Liste der Variablen
     */
    public Map<String, Variable> getSubtree(OID oid) {

        List<TreeEvent> eventList = subtree(oid);

        Map<String, Variable> vars = new HashMap<>();
        eventList.stream().forEach(event -> {

            VariableBinding[] bindings = event.getVariableBindings();
            for(VariableBinding binding : bindings) {

                vars.put(binding.getOid().toDottedString(), binding.getVariable());
            }
        });
        return vars;
    }

    /**
     * fragt eine Liste mit allen teilbäumen aller OIDs ab
     *
     * @param oids OIDs
     * @return Liste aller Teilbäume
     */
    public Map<String, Variable> getSubtree(List<OID> oids) {

        Map<String, Variable> vars = new HashMap<>();
        for(OID oid : oids) {

            Map<String, Variable> map = getSubtree(oid);
            vars.putAll(map);
        }
        return vars;
    }

    /**
     * fragt einen Teilbaum ab
     *
     * @param oid OID
     * @return Variablen des Teilbaumes
     */
    private List<TreeEvent> subtree(OID oid) {

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        treeUtils.setMaxRepetitions(50);
        return treeUtils.getSubtree(target, oid);
    }

    /**
     * durchläuft alle untergeordneten Teilbäume der OID
     *
     * @param oid OID
     * @return Liste aller Untergeordneten Variablen
     */
    public Map<String, Variable> doWalk(OID oid) {

        List<TreeEvent> eventList = walk(oid);

        Map<String, Variable> vars = new HashMap<>();
        eventList.stream().forEach(event -> {

            VariableBinding[] bindings = event.getVariableBindings();
            for(VariableBinding binding : bindings) {

                vars.put(binding.getOid().toDottedString(), binding.getVariable());
            }
        });
        return vars;
    }

    /**
     * durchläuft alle untergeordneten Teilbäume der OID
     *
     * @param oid OID
     * @return Liste aller Untergeordneten Variablen
     */
    private List<TreeEvent> walk(OID oid) {

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        return treeUtils.walk(target, new OID[]{oid});
    }
}
