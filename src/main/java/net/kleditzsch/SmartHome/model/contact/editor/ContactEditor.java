package net.kleditzsch.SmartHome.model.contact.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.contact.contact.*;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Kontakte Verwaltung
 */
public abstract class ContactEditor {

    public static final String COLLECTION = "contact.contact";

    /**
     * gibt eine Liste mit allen Kontaktgruppen zurück
     *
     * @return Liste mit Kontaktgruppen
     */
    public static List<ContactGroup> listContactGroups() {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"));

        List<ContactGroup> contactGroups = new ArrayList<>(10);
        for(Document document : iterator) {

            contactGroups.add(documentToContactGroup(document));
        }
        return contactGroups;
    }

    /**
     * gibt eine Liste mit den Kontaktgruppen zwischen start und length zurück
     *
     * @param start Start Index
     * @param length Länge
     * @return Liste mit den Kontaktgruppen
     */
    public static List<ContactGroup> listContactGroups(long start, long length) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"))
                .skip(Long.valueOf(start).intValue())
                .limit(Long.valueOf(length).intValue());

        List<ContactGroup> contactGroups = new ArrayList<>(10);
        for(Document document : iterator) {

            contactGroups.add(documentToContactGroup(document));
        }
        return contactGroups;
    }

    /**
     * gibt die Kontaktgruppe der ID zurück
     *
     * @param id Gruppen ID
     * @return Kontaktgruppe
     */
    public static Optional<ContactGroup> getContactGroupById(ID id) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToContactGroup(iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Anzahl der Kontaktgruppen zurück
     *
     * @return Anzahl der Kontaktgruppen
     */
    public static long countContactGroups() {

        MongoCollection messageCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        return messageCollection.countDocuments();
    }

    /**
     * speichert eine neue Kontaktgruppe in der Datenbank
     *
     * @param contactGroup Kontaktgruppe
     * @return ID der neuen Gruppe
     */
    public static ID addContactGroup(ContactGroup contactGroup) {

        List<Document> contacts = new ArrayList<>(contactGroup.getContacts().size());
        contactGroup.getContacts().forEach(contact -> {

            List<Document> mailAddress = new ArrayList<>(contact.getMailAddress().size());
            contact.getMailAddress().forEach(mail -> {

                mailAddress.add(new Document()
                        .append("_id", mail.getId().get())
                        .append("label", mail.getLabel())
                        .append("mailAddress", mail.getMailAddress())
                );
            });
            List<Document> phoneNumber = new ArrayList<>(contact.getPhoneNumber().size());
            contact.getPhoneNumber().forEach(phone -> {

                phoneNumber.add(new Document()
                        .append("_id", phone.getId().get())
                        .append("device", phone.getDevice())
                        .append("phoneNumber", phone.getPhoneNumber())
                );
            });
            List<Document> customFields = new ArrayList<>(contact.getCustomFields().size());
            contact.getCustomFields().forEach(field -> {

                customFields.add(new Document()
                        .append("_id", field.getId().get())
                        .append("label", field.getLabel())
                        .append("value", field.getValue())
                );
            });

            Document document = new Document()
                    .append("_id", contact.getId().get())
                    .append("name", contact.getName())
                    .append("description", contact.getDescription().orElse(""))
                    .append("companie", contact.getCompanie())
                    .append("address", contact.getAddress())
                    .append("webpage", contact.getWebpage())
                    .append("birthDay", contact.getBirthDay())
                    .append("mailAddress", mailAddress)
                    .append("phoneNumber", phoneNumber)
                    .append("customFields", customFields);
            contacts.add(document);
        });

        //Neue ID vergeben
        contactGroup.setId(ID.create());
        Document document = new Document()
                .append("_id", contactGroup.getId().get())
                .append("name", contactGroup.getName())
                .append("description", contactGroup.getDescription().orElse(""))
                .append("contacts", contacts);

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        collection.insertOne(document);

        return contactGroup.getId();
    }

    /**
     * aktualisiert eine Kontaktgruppe in der Datenbank
     *
     * @param contactGroup Kontaktgruppe
     * @return Erfolgsmeldung
     */
    public static boolean updateContactGroup(ContactGroup contactGroup) {

        List<Document> contacts = new ArrayList<>(contactGroup.getContacts().size());
        contactGroup.getContacts().forEach(contact -> {

            List<Document> mailAddress = new ArrayList<>(contact.getMailAddress().size());
            contact.getMailAddress().forEach(mail -> {

                mailAddress.add(new Document()
                        .append("_id", mail.getId().get())
                        .append("label", mail.getLabel())
                        .append("mailAddress", mail.getMailAddress())
                );
            });
            List<Document> phoneNumber = new ArrayList<>(contact.getPhoneNumber().size());
            contact.getPhoneNumber().forEach(phone -> {

                phoneNumber.add(new Document()
                        .append("_id", phone.getId().get())
                        .append("device", phone.getDevice())
                        .append("phoneNumber", phone.getPhoneNumber())
                );
            });
            List<Document> customFields = new ArrayList<>(contact.getCustomFields().size());
            contact.getCustomFields().forEach(field -> {

                customFields.add(new Document()
                        .append("_id", field.getId().get())
                        .append("label", field.getLabel())
                        .append("value", field.getValue())
                );
            });

            Document document = new Document()
                    .append("_id", contact.getId().get())
                    .append("name", contact.getName())
                    .append("description", contact.getDescription().orElse(""))
                    .append("companie", contact.getCompanie())
                    .append("address", contact.getAddress())
                    .append("webpage", contact.getWebpage())
                    .append("birthDay", contact.getBirthDay())
                    .append("mailAddress", mailAddress)
                    .append("phoneNumber", phoneNumber)
                    .append("customFields", customFields);
            contacts.add(document);
        });

        MongoCollection deviceCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = deviceCollection.updateOne(
                eq("_id", contactGroup.getId().get()),
                combine(
                        set("name", contactGroup.getName()),
                        set("description", contactGroup.getDescription().orElse("")),
                        set("contacts", contacts)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Kontaktgruppe
     *
     * @param id Gruppen ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteContactGroup(ID id) {

        MongoCollection collection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(collection.deleteOne(eq("_id", id.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    private static ContactGroup documentToContactGroup(Document document) {

        ContactGroup element = new ContactGroup();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));

        List<Document> contacts = (List<Document>) document.get("contacts");
        contacts.forEach(contactDocument -> {

            Contact contact = new Contact();
            contact.setId(ID.of(contactDocument.getString("_id")));
            contact.setName(contactDocument.getString("name"));
            contact.setDescription(contactDocument.getString("description"));
            contact.setCompanie(contactDocument.getString("companie"));
            contact.setAddress(contactDocument.getString("address"));
            contact.setWebpage(contactDocument.getString("webpage"));
            contact.setBirthDay(DatabaseDateTimeUtil.dateToLocalDate(contactDocument.getDate("birthDay")));

            List<Document> mailAddressDocuments = (List<Document>) contactDocument.get("mailAddress");
            mailAddressDocuments.forEach(mailAddressDocument -> {

                MailAddress mailAddress = new MailAddress();
                mailAddress.setId(ID.of(mailAddressDocument.getString("_id")));
                mailAddress.setLabel(mailAddressDocument.getString("label"));
                mailAddress.setMailAddress(mailAddressDocument.getString("mailAddress"));
                contact.getMailAddress().add(mailAddress);
            });

            List<Document> phoneNumberDocuments = (List<Document>) contactDocument.get("phoneNumber");
            phoneNumberDocuments.forEach(phoneNumberDocument -> {

                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId(ID.of(phoneNumberDocument.getString("_id")));
                phoneNumber.setDevice(phoneNumberDocument.getString("device"));
                phoneNumber.setPhoneNumber(phoneNumberDocument.getString("phoneNumber"));
                contact.getPhoneNumber().add(phoneNumber);
            });

            List<Document> customFieldDocuments = (List<Document>) contactDocument.get("customFields");
            customFieldDocuments.forEach(customFieldDocument -> {

                CustomField customField = new CustomField();
                customField.setId(ID.of(customFieldDocument.getString("_id")));
                customField.setLabel(customFieldDocument.getString("label"));
                customField.setValue(customFieldDocument.getString("value"));
                contact.getCustomFields().add(customField);
            });

            element.getContacts().add(contact);
        });
        return element;
    }
}
