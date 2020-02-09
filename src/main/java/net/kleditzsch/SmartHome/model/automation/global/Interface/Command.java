package net.kleditzsch.SmartHome.model.automation.global.Interface;

public interface Command {

    enum Type {

        COMMAND_SWITCH,
        COMMAND_MOVE
    }

    /**
     * gibt den Typ des Befels zurück
     *
     * @return Typ des Befehles
     */
    Type getType();
}
