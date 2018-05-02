package net.kleditzsch.SmartHome.model.automation.switchtimer;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Timer Element
 */
public class SwitchTimer extends Element {

    /**
     * Schaltzeiten
     */
    private SortedSet<Integer> month = new TreeSet<>();
    private SortedSet<Integer> weekday = new TreeSet<>();
    private SortedSet<Integer> day = new TreeSet<>();
    private SortedSet<Integer> hour = new TreeSet<>();
    private SortedSet<Integer> minute = new TreeSet<>();

    /**
     * liste mit den Befehlen
     */
    private List<SwitchCommand> commands = new ArrayList<>();

    /**
     * nächste Ausführungszeit
     */
    private LocalDateTime nextExecutionTime;

    /**
     * gibt die Liste mit den Ausführungsmonaten zurück
     *
     * @return Liste mit den Ausführungsmonaten
     */
    public SortedSet<Integer> getMonth() {
        return month;
    }

    /**
     * gibt die Liste mit den Ausführungswochentagen zurück
     *
     * @return Liste mit den Ausführungswochentagen
     */
    public SortedSet<Integer> getWeekday() {
        return weekday;
    }

    /**
     * gibt die Liste mit den Ausführungstagen zurück
     *
     * @return Liste mit den Ausführungstagen
     */
    public SortedSet<Integer> getDay() {
        return day;
    }

    /**
     * gibt die Liste mit den Ausführungsstunden zurück
     *
     * @return Liste mit den Ausführungsstunden
     */
    public SortedSet<Integer> getHour() {
        return hour;
    }

    /**
     * gibt die Liste mit den Ausführungsminuten zurück
     *
     * @return Liste mit den Ausführungsminuten
     */
    public SortedSet<Integer> getMinute() {
        return minute;
    }

    /**
     * gibt die Liste mit den Schaltbefehlen zurück
     *
     * @return Liste mit den Schaltbefehlen
     */
    public List<SwitchCommand> getCommands() {
        return commands;
    }

    /**
     * gibt die nächste Ausführungszeit zurück
     *
     * @return nächste Ausführungszeit
     */
    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    /**
     * setzt die nächste nächste Ausführungszeit
     *
     * @param nextExecutionTime nächste Ausführungszeit
     */
    public void setNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }
}