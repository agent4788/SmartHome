package net.kleditzsch.apps.automation.model.editor;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.database.AbstractDatabaseEditor;
import net.kleditzsch.apps.automation.model.global.Interface.Command;
import net.kleditzsch.apps.automation.model.global.MoveCommand;
import net.kleditzsch.apps.automation.model.global.SwitchCommand;
import net.kleditzsch.apps.automation.model.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.model.options.SwitchCommands;
import net.kleditzsch.SmartHome.utility.datetime.CornjobCalculator;
import net.kleditzsch.SmartHome.utility.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.time.LocalDateTime;
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

        MongoCollection switchTimerCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = switchTimerCollection.find();

        ReentrantReadWriteLock.WriteLock lock = getReadWriteLock().writeLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        data.clear();
        for (Document document : iterator) {

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
            element.setNextExecutionTime(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("nextExecutionTime")));

            List<Document> commands = (List<Document>) document.get("commands");
            for (Document commandDoc : commands) {

                Command.Type type = commandDoc.getString("type") != null ? Command.Type.valueOf(commandDoc.getString("type")) : Command.Type.COMMAND_SWITCH;
                switch (type) {

                    case COMMAND_SWITCH:

                        element.getCommands().add(
                                new SwitchCommand(
                                        ID.of(commandDoc.getString("switchableId")),
                                        SwitchCommands.valueOf(commandDoc.getString("command"))
                                )
                        );
                        break;
                    case COMMAND_MOVE:

                        element.getCommands().add(
                                new MoveCommand(
                                        ID.of(commandDoc.getString("shutterId")),
                                        commandDoc.getInteger("targetLevel")
                                )
                        );
                        break;
                }

            }

            element.resetChangedData();

            data.add(element);
        };

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

        MongoCollection switchTimerCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(getData().contains(switchTimer) && getData().remove(switchTimer)) {

            if(switchTimerCollection.deleteOne(eq("_id", switchTimer.getId().get())).getDeletedCount() == 1) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void dump() {

        MongoCollection switchTimerCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);

        ReentrantReadWriteLock.ReadLock lock = getReadWriteLock().readLock();
        lock.lock();

        List<SwitchTimer> data = getData();
        for(SwitchTimer switchTimer : data) {

            if(switchTimer.isChangedData()) {

                List<Document> commands = new ArrayList<>(switchTimer.getCommands().size());
                for(Command command : switchTimer.getCommands()) {

                    Document commandDoc = new Document();
                    if(command instanceof SwitchCommand) {

                        SwitchCommand switchCommand = (SwitchCommand) command;
                        commandDoc.append("switchableId", switchCommand.getSwitchableId().get());
                        commandDoc.append("command", switchCommand.getCommand().toString());
                        commandDoc.append("type", switchCommand.getType().toString());
                    } else if(command instanceof MoveCommand) {

                        MoveCommand moveCommand = (MoveCommand) command;
                        commandDoc.append("shutterId", moveCommand.getShutterId().get());
                        commandDoc.append("targetLevel", moveCommand.getTargetLevel());
                        commandDoc.append("type", moveCommand.getType().toString());
                    }
                    commands.add(commandDoc);
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
