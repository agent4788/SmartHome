package net.kleditzsch.applications.contact.model.contact;

import net.kleditzsch.smarthome.model.base.ID;

/**
 * Benutzerdefiniertes Feld
 */
public class CustomField {

    private ID id = null;
    private String label = "";
    private String value = "";

    public ID getId() {
        return id;
    }

    public void setId(ID id) {

        if(id != null) {

            this.id = id;
        } else {

            throw new IllegalArgumentException("Ungültige ID");
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
