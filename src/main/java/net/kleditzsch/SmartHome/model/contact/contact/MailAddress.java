package net.kleditzsch.SmartHome.model.contact.contact;

import net.kleditzsch.SmartHome.global.base.ID;

/**
 * Mail Adresse
 */
public class MailAddress {

    private ID id = null;
    private String mailAddress = "";
    private String label = "";

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

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
