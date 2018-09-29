package net.kleditzsch.SmartHome.model.automation.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Verwaltung Schaltserver
 */
public class SwitchServerEditor extends AbstractDatabaseEditor<SwitchServer> {

    private static final String COLLECTION = "automation.switchServer";

    /**
     * Schaltserver aus der Datenbank laden
     */
    @Override
    public void load() {

        MongoCollection switchServerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = switchServerCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SwitchServer> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            SwitchServer element = new SwitchServer(
                    ID.of(document.getString("_id")),
                    document.getString("name"),
                    document.getString("ipAddress"),
                    document.getInteger("port"),
                    document.getBoolean("disabled"));
            element.setDescription(document.getString("description"));
            element.setTimeout(document.getInteger("timeout"));
            element.resetChangedData();

            data.add(element);
        });

        lock.unlock();
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param switchServer ELement
     * @return erfolgsmeldung
     */
    public boolean delete(SwitchServer switchServer) {

        MongoCollection switchServerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(switchServer) && getData().remove(switchServer)) {

            if(switchServerCollection.deleteOne(eq("_id", switchServer.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    /**
     * Schaltserver in die Datenbank speichern
     */
    @Override
    public void dump() {

        MongoCollection switchServerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SwitchServer> data = getData();
        for(SwitchServer switchServer : data) {

            if(switchServer.isChangedData()) {

                switchServerCollection.updateOne(
                        eq("_id", switchServer.getId().get()),
                        combine(
                                setOnInsert("_id", switchServer.getId().get()),
                                set("name", switchServer.getName()),
                                set("description", switchServer.getDescription().orElseGet(() -> "")),
                                set("ipAddress", switchServer.getIpAddress()),
                                set("port", switchServer.getPort()),
                                set("timeout", switchServer.getTimeout()),
                                set("disabled", switchServer.isDisabled())
                        ),
                        new UpdateOptions().upsert(true)
                );

                switchServer.resetChangedData();
            }
        }

        lock.unlock();
    }
}
