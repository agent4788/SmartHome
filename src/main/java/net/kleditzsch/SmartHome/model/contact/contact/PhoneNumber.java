package net.kleditzsch.SmartHome.model.contact.contact;

import net.kleditzsch.SmartHome.global.base.ID;

/**
 * Telefonnummer
 */
public class PhoneNumber {

    private ID id = null;
    private String phoneNumber = "";
    private String device = "";

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
