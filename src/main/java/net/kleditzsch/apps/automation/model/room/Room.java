package net.kleditzsch.apps.automation.model.room;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.model.base.Element;
import net.kleditzsch.apps.automation.model.room.Interface.RoomElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Room extends Element {

    /**
     * Anzeigetext
     */
    private String displayText = "";

    /**
     * Sortierungs ID
     */
    private int orderId = 0;

    /**
     * deaktiviert
     */
    private boolean disabled = false;

    /**
     * gibt an ob der Raum ein Dashboard ist
     */
    private boolean dashboard = false;

    /**
     * gibt an ob der Raum das Standard Dashboard ist
     */
    private boolean defaultDashboard = false;

    /**
     * Ichon Datei
     */
    private String iconFile = "";

    /**
     * Liste mit den ELementen des Raumes
     */
    private List<RoomElement> roomElements = new ArrayList<>();

    /**
     * gibt den Anzeigetext zurück
     *
     * @return Anzeigetext
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * setzt den Anzeigetext
     *
     * @param text Anzeigetext
     */
    public void setDisplayText(String text) {

        Preconditions.checkNotNull(displayText);
        this.displayText = text;
        setChangedData();
    }

    /**
     * gibt die Sortierungs ID zurück
     *
     * @return Sortierungs ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * setzt die Sortierungs ID
     *
     * @param orderId Sortierungs ID
     */
    public void setOrderId(int orderId) {

        this.orderId = orderId;
        setChangedData();
    }

    /**
     * gibt an ob das ELement deaktiviert ist
     *
     * @return true wenn deaktiviert
     */
    public boolean isDisabled() {
        return this.disabled;
    }

    /**
     * aktiviert/deaktiviert das Element
     *
     * @param disabled aktiviert/deaktiviert
     */
    public void setDisabled(boolean disabled) {

        this.disabled = disabled;
        setChangedData();
    }

    /**
     * gibt den Pfad zur Icon Datei zurück
     *
     * @return Pfad zur Icon Datei
     */
    public String getIconFile() {
        return iconFile;
    }

    /**
     * setzt Pfad zur Icon Datei
     *
     * @param iconFile Pfad zur Icon Datei
     */
    public void setIconFile(String iconFile) {

        Preconditions.checkNotNull(iconFile);
        this.iconFile = iconFile;
        setChangedData();
    }

    /**
     * gibt an ob der Raum ein Dashboard ist
     *
     * @return Raum ist ein Dashboard
     */
    public boolean isDashboard() {
        return dashboard;
    }

    /**
     * markiert den Raum als Dashboard
     *
     * @param dashboard Raum ein Dashboard
     */
    public void setDashboard(boolean dashboard) {

        this.dashboard = dashboard;
        setChangedData();
    }

    /**
     * gibt an ob der Raum das Standard Dashboard ist
     *
     * @return Standard Dashboard
     */
    public boolean isDefaultDashboard() {
        return defaultDashboard;
    }

    /**
     * markiert den Raum als Standard Dashboard
     *
     * @param defaultDashboard Standard Dashboard
     */
    public void setDefaultDashboard(boolean defaultDashboard) {

        this.defaultDashboard = defaultDashboard;
        setChangedData();
    }

    /**
     * gibt eine Liste mit allen Elementen des Raumes zurück
     *
     * @return Litse mit allen Raumelementen
     */
    public List<RoomElement> getRoomElements() {

        setChangedData();
        return roomElements;
    }

    /**
     * gibt eine sortierte Liste mit allen Elementen des Raumes sortiert nach Sortierungs ID zurück
     *
     * @return Litse mit allen Raumelementen
     */
    public List<RoomElement> getRoomElemenstSorted() {

        setChangedData();
        return roomElements.stream()
                .sorted(Comparator.comparingInt(RoomElement::getOrderId))
                .collect(Collectors.toList());
    }
}
