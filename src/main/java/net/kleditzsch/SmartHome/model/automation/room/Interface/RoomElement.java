package net.kleditzsch.SmartHome.model.automation.room.Interface;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.Element;

import java.util.Optional;

public abstract class RoomElement extends Element {

    /**
     * Element Typen
     */
    public enum Type {

        @SerializedName("BUTTON_ELEMENT")
        BUTTON_ELEMENT,
        @SerializedName("SENSOR_ELEMENT")
        SENSOR_ELEMENT,
        @SerializedName("VIRTUAL_SENSOR_ELEMENT")
        VIRTUAL_SENSOR_ELEMENT,
        @SerializedName("DIVIDER_ELEMENT")
        DIVIDER_ELEMENT,
        @SerializedName("SHUTTER_ELEMENT")
        SHUTTER_ELEMENT
    }

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
     * Ichon Datei
     */
    private String iconFile = "";

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

        Preconditions.checkNotNull(orderId);
        this.orderId = orderId;
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

        Preconditions.checkNotNull(disabled);
        this.disabled = disabled;
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
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    public abstract Type getType();
}
