package net.kleditzsch.applications.contact;

import net.kleditzsch.smarthome.app.Application;
import net.kleditzsch.applications.contact.view.user.ContactIndexServlet;
import net.kleditzsch.applications.contact.view.user.overview.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Kontakteverwaltung
 */
public class ContactApplication implements Application {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {


    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(ContactIndexServlet.class, "/contact/");
        contextHandler.addServlet(ContactIndexServlet.class, "/contact/index");
        contextHandler.addServlet(ContactGroupViewServlet.class, "/contact/groupview");
        contextHandler.addServlet(ContactGroupFormServlet.class, "/contact/groupform");
        contextHandler.addServlet(ContactGroupDeleteServlet.class, "/contact/groupdelete");
        contextHandler.addServlet(ContactContactViewServlet.class, "/contact/contactview");
        contextHandler.addServlet(ContactContactFormServlet.class, "/contact/contactform");
        contextHandler.addServlet(ContactContactDeleteServlet.class, "/contact/contactdelete");
        contextHandler.addServlet(ContactPhoneNumberFormServlet.class, "/contact/phonenumberform");
        contextHandler.addServlet(ContactPhoneNumberDeleteServlet.class, "/contact/phonenumberdelete");
        contextHandler.addServlet(ContactMailAddressFormServlet.class, "/contact/mailaddressform");
        contextHandler.addServlet(ContactMailAddressDeleteServlet.class, "/contact/mailaddressdelete");
        contextHandler.addServlet(ContactCustomFieldFormServlet.class, "/contact/customfieldform");
        contextHandler.addServlet(ContactCustomFieldDeleteServlet.class, "/contact/customfielddelete");
    }

    /**
     * startet die Anwendung
     */
    public void start() {


    }

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump() {


    }

    /**
     * Beendet die Anwendung
     */
    public void stop() {


    }
}
