package net.kleditzsch.applications.contract.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.applications.contract.model.contract.Contract;
import net.kleditzsch.applications.contract.model.contract.ContractGroup;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Verträgeverwaltung
 */
public abstract class ContractEditor {

    public static final String COLLECTION = "contract.contract";

    /**
     * gibt eine Liste mit allen Kontaktgruppen zurück
     *
     * @return Liste mit Kontaktgruppen
     */
    public static List<ContractGroup> listContractGroups() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"));

        List<ContractGroup> contactGroups = new ArrayList<>(10);
        for(Document document : iterator) {

            contactGroups.add(documentToContractGroup(document));
        }
        return contactGroups;
    }

    /**
     * gibt eine Liste mit den Vertragsgruppen zwischen start und length zurück
     *
     * @param start Start Index
     * @param length Länge
     * @return Liste mit den Vertragsgruppen
     */
    public static List<ContractGroup> listContractGroups(long start, long length) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"))
                .skip(Long.valueOf(start).intValue())
                .limit(Long.valueOf(length).intValue());

        List<ContractGroup> contractGroups = new ArrayList<>(10);
        for(Document document : iterator) {

            contractGroups.add(documentToContractGroup(document));
        }
        return contractGroups;
    }

    /**
     * gibt die Vertragsgruppe der ID zurück
     *
     * @param id Gruppen ID
     * @return Vertragsgruppe
     */
    public static Optional<ContractGroup> getContractGroupById(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToContractGroup(iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Anzahl der Vertragsgruppen zurück
     *
     * @return Anzahl der Vertragsgruppen
     */
    public static long countContractGroups() {

        MongoCollection messageCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return messageCollection.countDocuments();
    }

    /**
     * speichert eine neue Vertragsgruppe in der Datenbank
     *
     * @param contractGroup Vertragsgruppe
     * @return ID der neuen Gruppe
     */
    public static ID addContractGroup(ContractGroup contractGroup) {

        List<Document> contracts = new ArrayList<>();
        contractGroup.getContracts().forEach(contract -> {

            contracts.add(new Document()
                    .append("_id", contract.getId().get())
                    .append("name", contract.getName())
                    .append("description", contract.getDescription().orElse(""))
                    .append("customerNumber", contract.getCustomerNumber())
                    .append("contractNumber", contract.getContractNumber())
                    .append("companie", contract.getCompanie())
                    .append("companieAddress", contract.getCompanieAddress())
                    .append("companieMailAddress", contract.getCompanieMailAddress())
                    .append("companiePhoneNumber", contract.getCompaniePhoneNumber())
                    .append("companieWebpage", contract.getCompanieWebpage())
                    .append("customerLoginPage", contract.getCustomerLoginPage())
                    .append("startDate", contract.getStartDate())
                    .append("endDate", contract.getEndDate())
                    .append("minTerm", contract.getMinTerm())
                    .append("minTermUnit", contract.getMinTermUnit().toString())
                    .append("renewalTerm", contract.getRenewalTerm())
                    .append("renewaltermUnit", contract.getRenewaltermUnit().toString())
                    .append("noticePeriod", contract.getNoticePeriod())
                    .append("noticePeriodUnit", contract.getNoticePeriodUnit().toString())
                    .append("state", contract.getState().toString())
                    .append("noticeMail", contract.isNoticeMail())
                    .append("price", contract.getPrice())
                    .append("billingPeriod", contract.getBillingPeriod())
                    .append("billingPeriodUnit", contract.getBillingPeriodUnit().toString())
                    .append("billingDay", contract.getBillingDay())
                    .append("billingMonth", contract.getBillingMonth())
            );
        });

        //Neue ID vergeben
        contractGroup.setId(ID.create());
        Document document = new Document()
                .append("_id", contractGroup.getId().get())
                .append("name", contractGroup.getName())
                .append("description", contractGroup.getDescription().orElse(""))
                .append("contracts", contracts);

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        collection.insertOne(document);

        return contractGroup.getId();
    }

    /**
     * aktualisiert eine Vertragsgruppe in der Datenbank
     *
     * @param contractGroup Vertragsgruppe
     * @return Erfolgsmeldung
     */
    public static boolean updateContractGroup(ContractGroup contractGroup) {

        List<Document> contracts = new ArrayList<>();
        contractGroup.getContracts().forEach(contract -> {

            contracts.add(new Document()
                    .append("_id", contract.getId().get())
                    .append("name", contract.getName())
                    .append("description", contract.getDescription().orElse(""))
                    .append("customerNumber", contract.getCustomerNumber())
                    .append("contractNumber", contract.getContractNumber())
                    .append("companie", contract.getCompanie())
                    .append("companieAddress", contract.getCompanieAddress())
                    .append("companieMailAddress", contract.getCompanieMailAddress())
                    .append("companiePhoneNumber", contract.getCompaniePhoneNumber())
                    .append("companieWebpage", contract.getCompanieWebpage())
                    .append("customerLoginPage", contract.getCustomerLoginPage())
                    .append("startDate", contract.getStartDate())
                    .append("endDate", contract.getEndDate())
                    .append("minTerm", contract.getMinTerm())
                    .append("minTermUnit", contract.getMinTermUnit().toString())
                    .append("renewalTerm", contract.getRenewalTerm())
                    .append("renewaltermUnit", contract.getRenewaltermUnit().toString())
                    .append("noticePeriod", contract.getNoticePeriod())
                    .append("noticePeriodUnit", contract.getNoticePeriodUnit().toString())
                    .append("state", contract.getState().toString())
                    .append("noticeMail", contract.isNoticeMail())
                    .append("price", contract.getPrice())
                    .append("billingPeriod", contract.getBillingPeriod())
                    .append("billingPeriodUnit", contract.getBillingPeriodUnit().toString())
                    .append("billingDay", contract.getBillingDay())
                    .append("billingMonth", contract.getBillingMonth())
            );
        });

        MongoCollection deviceCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = deviceCollection.updateOne(
                eq("_id", contractGroup.getId().get()),
                combine(
                        set("name", contractGroup.getName()),
                        set("description", contractGroup.getDescription().orElse("")),
                        set("contracts", contracts)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Vertragsgruppe
     *
     * @param id Gruppen ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteContractGroup(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(collection.deleteOne(eq("_id", id.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    private static ContractGroup documentToContractGroup(Document document) {

        ContractGroup element = new ContractGroup();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));

        List<Document> contracts = (List<Document>) document.get("contracts");
        contracts.forEach(contractDocument -> {

            Contract contract = new Contract();
            contract.setId(ID.of(contractDocument.getString("_id")));
            contract.setName(contractDocument.getString("name"));
            contract.setDescription(contractDocument.getString("description"));
            contract.setCustomerNumber(contractDocument.getString("customerNumber"));
            contract.setContractNumber(contractDocument.getString("contractNumber"));
            contract.setCompanie(contractDocument.getString("companie"));
            contract.setCompanieAddress(contractDocument.getString("companieAddress"));
            contract.setCompanieMailAddress(contractDocument.getString("companieMailAddress"));
            contract.setCompaniePhoneNumber(contractDocument.getString("companiePhoneNumber"));
            contract.setCompanieWebpage(contractDocument.getString("companieWebpage"));
            contract.setCustomerLoginPage(contractDocument.getString("customerLoginPage"));
            contract.setStartDate(DatabaseDateTimeUtil.dateToLocalDate(contractDocument.getDate("startDate")));
            contract.setEndDate(DatabaseDateTimeUtil.dateToLocalDate(contractDocument.getDate("endDate")));
            contract.setMinTerm(contractDocument.getInteger("minTerm"));
            contract.setMinTermUnit(Contract.TimeUnit.valueOf(contractDocument.getString("minTermUnit")));
            contract.setRenewalTerm(contractDocument.getInteger("renewalTerm"));
            contract.setRenewaltermUnit(Contract.TimeUnit.valueOf(contractDocument.getString("renewaltermUnit")));
            contract.setNoticePeriod(contractDocument.getInteger("noticePeriod"));
            contract.setNoticePeriodUnit(Contract.TimeUnit.valueOf(contractDocument.getString("noticePeriodUnit")));
            contract.setState(Contract.State.valueOf(contractDocument.getString("state")));
            contract.setNoticeMail(contractDocument.getBoolean("noticeMail"));
            contract.setPrice(contractDocument.getDouble("price"));
            contract.setBillingPeriod(contractDocument.getInteger("billingPeriod"));
            contract.setBillingPeriodUnit(Contract.TimeUnit.valueOf(contractDocument.getString("billingPeriodUnit")));
            contract.setBillingDay(contractDocument.getInteger("billingDay"));
            contract.setBillingMonth(contractDocument.getInteger("billingMonth"));

            element.getContracts().add(contract);
        });
        return element;
    }
}
