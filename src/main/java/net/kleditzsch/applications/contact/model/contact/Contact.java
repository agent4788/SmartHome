package net.kleditzsch.applications.contact.model.contact;

import net.kleditzsch.smarthome.model.base.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kontakt
 */
public class Contact extends Element implements Comparable<Contact> {

    /**
     * Firma
     */
    private String companie = "";

    /**
     * Adresse
     */
    private String address = "";

    /**
     * Webseite
     */
    private String webpage = "";

    /**
     * Geburtstag
     */
    private LocalDate birthDay = LocalDate.of(1980, 1, 1);

    /**
     * Mail Adressen
     */
    private List<MailAddress> mailAddress = new ArrayList<>();

    /**
     * Telefonnummern
     */
    private List<PhoneNumber> phoneNumber = new ArrayList<>();

    /**
     * Benutzerdefinierte Felder
     */
    private List<CustomField> customFields = new ArrayList<>();

    /**
     * gibt die Firma zurück
     *
     * @return Firma
     */
    public String getCompanie() {
        return companie;
    }

    /**
     * setzt die Firma
     *
     * @param companie Firma
     */
    public void setCompanie(String companie) {
        this.companie = companie;
    }

    /**
     * gibt die Adresse zurück
     *
     * @return Adresse
     */
    public String getAddress() {
        return address;
    }

    /**
     * setzt die Adresse
     *
     * @param address Adresse
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * gibt die Webseite zurück
     *
     * @return Webseite
     */
    public String getWebpage() {
        return webpage;
    }

    /**
     * setzt die Webseite
     *
     * @param webpage Webseite
     */
    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    /**
     * gibt an ob der Geburtstag gesetzt wurde
     *
     * @return Geburtstag gesetzt
     */
    public boolean isBirtday() {

        return !birthDay.equals(LocalDate.of(1900, 1, 1));
    }

    /**
     * gibt das Geburtsdatum zurück
     *
     * @return Geurtsdatum
     */
    public LocalDate getBirthDay() {
        return birthDay;
    }

    /**
     * setzt das Geburtsdatum
     *
     * @param birthDay Geburtsdatum
     */
    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    /**
     * gibt die Mail Adressen zurück
     *
     * @return Mail Adressen
     */
    public List<MailAddress> getMailAddress() {
        return mailAddress;
    }

    /**
     * gibt die Telefonnummern zurück
     *
     * @return Telefonnummern
     */
    public List<PhoneNumber> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * gibt die Benutzerfelder zurück
     *
     * @return Benutzerfelder
     */
    public List<CustomField> getCustomFields() {
        return customFields;
    }

    @Override
    public int compareTo(Contact o) {
        return getName().compareToIgnoreCase(o.getName());
    }
}
