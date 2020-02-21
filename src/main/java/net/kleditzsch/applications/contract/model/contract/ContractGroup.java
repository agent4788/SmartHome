package net.kleditzsch.applications.contract.model.contract;

import net.kleditzsch.smarthome.model.base.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Verträge Gruppe
 */
public class ContractGroup extends Element {

    /**
     * Verträge
     */
    private List<Contract> contracts = new ArrayList<>();

    /**
     * gibt die Liste der Verträge zurück
     *
     * @return Liste der Verträge
     */
    public List<Contract> getContracts() {
        return contracts;
    }
}
