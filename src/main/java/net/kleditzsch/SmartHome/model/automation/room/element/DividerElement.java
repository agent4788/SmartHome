package net.kleditzsch.SmartHome.model.automation.room.element;

import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;

import java.util.Optional;

public class DividerElement extends RoomElement {

    private Type type = Type.DIVIDER_ELEMENT;

    /**
     * Optionales Icon
     */
    private String icon = "";

    public DividerElement() {

        this.setIconFile("horizontal-line.png");
    }

    /**
     * gibt das Icon zurück
     *
     * @return Icon
     */
    public Optional<String> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * setzt das Icon
     *
     * @param icon Icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
