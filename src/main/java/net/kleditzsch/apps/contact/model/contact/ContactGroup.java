package net.kleditzsch.apps.contact.model.contact;

import net.kleditzsch.SmartHome.model.base.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Kontakt Gruppe
 */
public class ContactGroup extends Element {

    /**
     * Kontakte
     */
    private List<Contact> contacts = new ArrayList<>();

    /**
     * gibt die Liste mit den Kontakten zurück
     *
     * @return Liste mit den Kontakten
     */
    public List<Contact> getContacts() {
        return contacts;
    }
}
