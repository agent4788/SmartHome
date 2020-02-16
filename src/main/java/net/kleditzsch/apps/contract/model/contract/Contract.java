package net.kleditzsch.apps.contract.model.contract;

import net.kleditzsch.SmartHome.model.base.Element;

import java.time.LocalDate;

/**
 * Vertrag
 */
public class Contract extends Element implements Comparable<Contract> {

    /**
     * Zeiteinheiten
     */
    public enum TimeUnit {

        DAY,
        MONTH,
        YEAR
    }

    public enum State {

        CONSTANTLY, //laufend
        TERMINATED, //gekündigt
        COMPLETED   //beendet
    }

    /**
     * Kundennummer
     */
    private String customerNumber = "";

    /**
     * Vertragsnummer
     */
    private String contractNumber = "";

    /**
     * Firma
     */
    private String companie = "";

    /**
     * Firmen Adresse
     */
    private String companieAddress = "";

    /**
     * Mail Adress
     */
    private String companieMailAddress = "";

    /**
     * Telefonnummer
     */
    private String companiePhoneNumber = "";

    /**
     * Internetseite
     */
    private String companieWebpage = "";

    /**
     * Webseite des Kundencenters
     */
    private String customerLoginPage = "";

    /**
     * Start Datum
     */
    private LocalDate startDate = LocalDate.of(1980, 1, 1);

    /**
     * Vertragsende
     */
    private LocalDate endDate = LocalDate.of(1900, 1, 1);

    /**
     * Mindestlaufzeit
     */
    private int minTerm = 1;

    /**
     * Mindestlaufzeit Einheit
     */
    private TimeUnit minTermUnit = TimeUnit.MONTH;

    /**
     * Vertragsverlängerung
     */
    private int renewalTerm = 1;

    /**
     * Vertragsverlängerung Einheit
     */
    private TimeUnit renewaltermUnit = TimeUnit.MONTH;

    /**
     * Kündigungsfrist
     */
    private int noticePeriod = 1;

    /**
     * Kündigungsfrist Einheit
     */
    private TimeUnit noticePeriodUnit = TimeUnit.MONTH;

    /**
     * Status
     */
    private State state = State.CONSTANTLY;

    /**
     * Mail Benachrichtigung wenn kurz vor Kündigungsfrist
     */
    private boolean noticeMail = false;

    /**
     * Preis
     */
    private double price = 0.0;

    /**
     * Abrechnungszeitraum
     */
    private int billingPeriod = 1;

    /**
     * Abrechnungszeitraum Einheit
     */
    private TimeUnit billingPeriodUnit = TimeUnit.MONTH;

    /**
     * Tag der Abbuchung im Monat
     */
    private int billingDay = 1;

    /**
     * Monat der Abbuchung im Jahr
     */
    private int billingMonth = 1;

    /**
     * gibt die Kundennummer zurück
     *
     * @return Kundennummer
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * setzt die Kundennummer
     *
     * @param customerNumber Kundennummer
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * gibt die Vertragsnummer zurück
     *
     * @return Vertragsnummer
     */
    public String getContractNumber() {
        return contractNumber;
    }

    /**
     * setzt die Vertragsnummer
     *
     * @param contractNumber Vertragsnummer
     */
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    /**
     * gibt den Firmennamen zurück
     *
     * @return Firmennamen
     */
    public String getCompanie() {
        return companie;
    }

    /**
     * setzt den Firmennamen
     *
     * @param companie Firmennamen
     */
    public void setCompanie(String companie) {
        this.companie = companie;
    }

    /**
     * gibt die Firmenadresse zurück
     *
     * @return Firmenadresse
     */
    public String getCompanieAddress() {
        return companieAddress;
    }

    /**
     * setzt die Firmenadresse
     *
     * @param companieAddress Firmenadresse
     */
    public void setCompanieAddress(String companieAddress) {
        this.companieAddress = companieAddress;
    }

    /**
     * gibt die Firmenmailadresse zurück
     *
     * @return Firmenmailadresse
     */
    public String getCompanieMailAddress() {
        return companieMailAddress;
    }

    /**
     * setzt die Firmenmailadresse
     *
     * @param companieMailAddress Firmenmailadresse
     */
    public void setCompanieMailAddress(String companieMailAddress) {
        this.companieMailAddress = companieMailAddress;
    }

    /**
     * gibt die Firmentelefonnummer zurück
     *
     * @return Firmentelefonnummer
     */
    public String getCompaniePhoneNumber() {
        return companiePhoneNumber;
    }

    /**
     * setzt die Firmentelefonnummer
     *
     * @param companiePhoneNumber Firmentelefonnummer
     */
    public void setCompaniePhoneNumber(String companiePhoneNumber) {
        this.companiePhoneNumber = companiePhoneNumber;
    }

    /**
     * gibt die Firmewebseite zurück
     *
     * @return Firmewebseite
     */
    public String getCompanieWebpage() {
        return companieWebpage;
    }

    /**
     * setzt die Firmewebseite
     *
     * @param companieWebpage Firmewebseite
     */
    public void setCompanieWebpage(String companieWebpage) {
        this.companieWebpage = companieWebpage;
    }

    /**
     * gibt die Webseite des Kundenlogin zurück
     *
     * @return Webseite des Kundenlogin
     */
    public String getCustomerLoginPage() {
        return customerLoginPage;
    }

    /**
     * setzt die Webseite des Kundenlogin
     *
     * @param customerLoginPage Webseite des Kundenlogin
     */
    public void setCustomerLoginPage(String customerLoginPage) {
        this.customerLoginPage = customerLoginPage;
    }

    /**
     * gibt das Start Datum zurück
     *
     * @return Start Datum
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * setzt das Start Datum
     *
     * @param startDate Start Datum
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * gibt das End Datum zurück
     *
     * @return End Datum
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * setzt das End Datum
     *
     * @param endDate End Datum
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * gibt die Mindestlaufzeit zurück
     *
     * @return Mindestlaufzeit
     */
    public int getMinTerm() {
        return minTerm;
    }

    /**
     * setzt die Mindestlaufzeit
     *
     * @param minTerm Mindestlaufzeit
     */
    public void setMinTerm(int minTerm) {
        this.minTerm = minTerm;
    }

    /**
     * gibt die Einheit der Mindestlaufzeit zurück
     *
     * @return Einheit der Mindestlaufzeit
     */
    public TimeUnit getMinTermUnit() {
        return minTermUnit;
    }

    /**
     * setzt die Einheit der Mindestlaufzeit
     *
     * @param minTermUnit Einheit der Mindestlaufzeit
     */
    public void setMinTermUnit(TimeUnit minTermUnit) {
        this.minTermUnit = minTermUnit;
    }

    /**
     * gibt die Vertragsverlängerung zurück
     *
     * @return Vertragsverlängerung
     */
    public int getRenewalTerm() {
        return renewalTerm;
    }

    /**
     * setzt die Vertragsverlängerung
     *
     * @param renewalTerm Vertragsverlängerung
     */
    public void setRenewalTerm(int renewalTerm) {
        this.renewalTerm = renewalTerm;
    }

    /**
     * gibt die Einheit der Vertragsverlängerung zurück
     *
     * @return Einheit der Vertragsverlängerung
     */
    public TimeUnit getRenewaltermUnit() {
        return renewaltermUnit;
    }

    /**
     * setzt die Einheit der Vertragsverlängerung
     *
     * @param renewaltermUnit Einheit der Vertragsverlängerung
     */
    public void setRenewaltermUnit(TimeUnit renewaltermUnit) {
        this.renewaltermUnit = renewaltermUnit;
    }

    /**
     * gibt die Kündigungsfrist zurück
     *
     * @return Kündigungsfrist
     */
    public int getNoticePeriod() {
        return noticePeriod;
    }

    /**
     * setzt die Kündigungsfrist
     *
     * @param noticePeriod Kündigungsfrist
     */
    public void setNoticePeriod(int noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    /**
     * gibt die Einheit der Kündigungsfrist zurück
     *
     * @return Einheit der Kündigungsfrist
     */
    public TimeUnit getNoticePeriodUnit() {
        return noticePeriodUnit;
    }

    /**
     * setzt die Einheit der Kündigungsfrist
     *
     * @param noticePeriodUnit Einheit der Kündigungsfrist
     */
    public void setNoticePeriodUnit(TimeUnit noticePeriodUnit) {
        this.noticePeriodUnit = noticePeriodUnit;
    }

    /**
     * gibt den Status zurück
     *
     * @return Status
     */
    public State getState() {
        return state;
    }

    /**
     * setzt den Status
     *
     * @param state Status
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * gibt an ob eine Erinnerungsmail zur Kündigung verschickt werden soll
     *
     * @return Erinnerungsmail zur Kündigung
     */
    public boolean isNoticeMail() {
        return noticeMail;
    }

    /**
     * aktiviert die Erinnerungsmail zur Kündigung
     *
     * @param noticeMail Erinnerungsmail zur Kündigung
     */
    public void setNoticeMail(boolean noticeMail) {
        this.noticeMail = noticeMail;
    }

    /**
     * gibt den Preis pro Abrechnungszeitraum zurück
     *
     * @return Preis pro Abrechnungszeitraum
     */
    public double getPrice() {
        return price;
    }

    /**
     * setzt den Preis pro Abrechnungszeitraum
     *
     * @param price Preis pro Abrechnungszeitraum
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * gibt den Abrechnungszeitraum zurück
     *
     * @return Abrechnungszeitraum
     */
    public int getBillingPeriod() {
        return billingPeriod;
    }

    /**
     * setzt den Abrechnungszeitraum
     *
     * @param billingPeriod Abrechnungszeitraum
     */
    public void setBillingPeriod(int billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    /**
     * gibt die Einheit des Abrechnungszeitraums zurück
     *
     * @return Einheit des Abrechnungszeitraums
     */
    public TimeUnit getBillingPeriodUnit() {
        return billingPeriodUnit;
    }

    /**
     * setzt die Einheit des Abrechnungszeitraums
     *
     * @param billingPeriodUnit Einheit des Abrechnungszeitraums
     */
    public void setBillingPeriodUnit(TimeUnit billingPeriodUnit) {
        this.billingPeriodUnit = billingPeriodUnit;
    }

    /**
     * gibt den Abbuchungstag zurück
     *
     * @return Abbuchungstag
     */
    public int getBillingDay() {
        return billingDay;
    }

    /**
     * setzt den Abbuchungstag
     *
     * @param billingDay Abbuchungstag
     */
    public void setBillingDay(int billingDay) {
        this.billingDay = billingDay;
    }

    /**
     * gibt den Abbuchungsmonat zurück
     *
     * @return Abbuchungsmonat
     */
    public int getBillingMonth() {
        return billingMonth;
    }

    /**
     * setzt den Abbuchungsmonat
     *
     * @param billingMonth Abbuchungsmonat
     */
    public void setBillingMonth(int billingMonth) {
        this.billingMonth = billingMonth;
    }

    @Override
    public int compareTo(Contract o) {
        return getName().compareToIgnoreCase(o.getName());
    }
}
