package net.kleditzsch.applications.automation.model.global.Interface;

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
