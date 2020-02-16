package net.kleditzsch.apps.automation.model.global;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.global.Interface.Command;

public class MoveCommand implements Command {

    private Type type = Type.COMMAND_MOVE;

    /**
     * ID des Rollladens
     */
    private ID shutterId;

    /**
     * Ziel niveau
     */
    private int targetLevel;

    /**
     * @param shutterId ID des Rollladens
     * @param targetLevel Ziel niveau
     */
    public MoveCommand(ID shutterId, int targetLevel) {
        setShutterId(shutterId);
        setTargetLevel(targetLevel);
    }

    /**
     * gibt die ID des Rollladens zurück
     *
     * @return ID des Rollladens
     */
    public ID getShutterId() {
        return shutterId;
    }

    /**
     * setzt die ID des Rollladens
     *
     * @param shutterId ID des Rollladens
     */
    public void setShutterId(ID shutterId) {

        Preconditions.checkNotNull(shutterId);
        this.shutterId = shutterId;
    }

    /**
     * gibt das Ziel niveau zurück
     *
     * @return Ziel niveau
     */
    public int getTargetLevel() {
        return targetLevel;
    }

    /**
     * setzt das Ziel niveau
     *
     * @param targetLevel Ziel niveau
     */
    public void setTargetLevel(int targetLevel) {

        Preconditions.checkArgument(targetLevel >= 0 && targetLevel <= 100);
        this.targetLevel = targetLevel;
    }

    /**
     * gibt den Typ des Befels zurück
     *
     * @return Typ des Befehles
     */
    public net.kleditzsch.apps.automation.model.global.Interface.Command.Type getType() {

        return type;
    }
}
