package net.kleditzsch.SmartHome.model.automation.room.Interface;

import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.global.base.Element;

public abstract class RoomElement extends Element {

    /**
     * Element Typen
     */
    public enum Type {

        @SerializedName("BUTTON_ELEMENT")
        BUTTON_ELEMENT,
        @SerializedName("SENSOR_ELEMENT")
        SENSOR_ELEMENT
    }

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
        this.iconFile = iconFile;
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    public abstract Type getType();
}
