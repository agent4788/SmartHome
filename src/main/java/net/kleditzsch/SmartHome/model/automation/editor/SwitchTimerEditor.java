package net.kleditzsch.SmartHome.model.automation.editor;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.global.database.AbstractDatabaseEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.datetime.CornjobCalculator;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Timer Datenverwaltung
 */
public class SwitchTimerEditor extends AbstractDatabaseEditor<SwitchTimer> {

    public static final String COLLECTION = "automation.switchTimer";

    @Override
    public void load() {

        MongoCollection switchTimerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = switchTimerCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        data.clear();
        iterator.forEach((Block<Document>) document -> {

            SwitchTimer element = new  SwitchTimer();
            element.setId(ID.of(document.getString("_id")));
            element.setName(document.getString("name"));
            element.setDescription(document.getString("description"));
            element.setDisabled(document.getBoolean("disabled"));
            element.getMonth().addAll((Collection<? extends Integer>) document.get("month"));
            element.getWeekday().addAll((Collection<? extends Integer>) document.get("weekday"));
            element.getDay().addAll((Collection<? extends Integer>) document.get("day"));
            element.getHour().addAll((Collection<? extends Integer>) document.get("hour"));
            element.getMinute().addAll((Collection<? extends Integer>) document.get("minute"));
            element.setNextExecutionTime(DatabaseDateTimeUtil.dateToLocaleDateTime(document.getDate("nextExecutionTime")));

            List<Document> commands = (List<Document>) document.get("commands");
            for (Document command : commands) {

                element.getCommands().add(
                        new SwitchCommand(
                                ID.of(command.getString("switchableId")),
                                SwitchCommands.valueOf(command.getString("command"))
                        )
                );
            }

            element.resetChangedData();

            data.add(element);
        });

        lock.unlock();
    }

    /**
     * berechnet die nächste Schaltzeit
     *
     * @param switchTimer Schalt Timer
     * @return nächste Schaltzeit
     */
    public LocalDateTime calculateNextExecutionTime(SwitchTimer switchTimer) {

        return CornjobCalculator.calculateNextRunTime(
                switchTimer.getMinute(),
                switchTimer.getHour(),
                switchTimer.getDay(),
                switchTimer.getWeekday(),
                switchTimer.getMonth()
        );
    }

    /**
     * löscht ein Element aus dem Datenbestand
     *
     * @param switchTimer ELement
     * @return erfolgsmeldung
     */
    public boolean delete(SwitchTimer switchTimer) {

        MongoCollection switchTimerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(switchTimer) && getData().remove(switchTimer)) {

            if(switchTimerCollection.deleteOne(eq("_id", switchTimer.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void dump() {

        MongoCollection switchTimerCollection = Application.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        for(SwitchTimer switchTimer : data) {

            if(switchTimer.isChangedData()) {

                List<Document> commands = new ArrayList<>(switchTimer.getCommands().size());
                for(SwitchCommand switchCommand : switchTimer.getCommands()) {

                    Document command = new Document();
                    command.append("switchableId", switchCommand.getSwitchableId().get());
                    command.append("command", switchCommand.getCommand().toString());
                    commands.add(command);
                }

                switchTimerCollection.updateOne(
                        eq("_id", switchTimer.getId().get()),
                        combine(
                                setOnInsert("_id", switchTimer.getId().get()),
                                set("name", switchTimer.getName()),
                                set("description", switchTimer.getDescription().orElseGet(() -> "")),
                                set("disabled", switchTimer.isDisabled()),
                                set("month", switchTimer.getMonth()),
                                set("weekday", switchTimer.getWeekday()),
                                set("day", switchTimer.getDay()),
                                set("hour", switchTimer.getHour()),
                                set("minute", switchTimer.getMinute()),
                                set("commands", commands),
                                set("nextExecutionTime", switchTimer.getNextExecutionTime())
                        ),
                        new UpdateOptions().upsert(true)
                );

                switchTimer.resetChangedData();
            }
        }

        lock.unlock();
    }
}
