package net.kleditzsch.applications.shoppinglist.model.list;

import net.kleditzsch.smarthome.model.base.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Einkaufsliste
 */
public class ShoppingList extends Element implements Comparable<ShoppingList> {

    /**
     * Listeneinträge
     */
    private List<Item> items = new ArrayList();

    /**
     * Sortierung
     */
    private int orderId = 0;

    /**
     * gibt die Listen Einträge zurück
     *
     * @return Listen Einträge
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * gibt die Sortierung zurück
     *
     * @return Sortierung
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * setzt die Sortierung
     *
     * @param orderId Sortierung
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public int compareTo(ShoppingList o) {

        return Integer.compare(getOrderId(), o.getOrderId());
    }
}
