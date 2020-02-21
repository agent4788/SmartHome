package net.kleditzsch.applications.shoppinglist.model.list;

import net.kleditzsch.smarthome.model.base.Element;

import java.util.Optional;

/**
 * Listeneintrag
 */
public class Item extends Element implements Comparable<Item> {

    /**
     * Menge
     */
    private String amount = "";

    /**
     * gibt die Menge zurück
     *
     * @return Menge
     */
    public Optional<String> getAmount() {

        if(!amount.isBlank()) {

            return Optional.of(amount);
        }
        return Optional.empty();
    }

    /**
     * setzt die Menge
     *
     * @param amount Menge
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Item o) {

        return getName().compareTo(getName());
    }
}
