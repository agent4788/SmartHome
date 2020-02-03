package net.kleditzsch.SmartHome.controller.automation.executorservice.command;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;

public class MoveShutterCommand implements Command {

    /**
     * Rollladen
     */
    private Shutter shutter;

    /**
     * Ziel niveau
     */
    private int targetLevel = 0;

    /**
     * @param shutter Rollladen
     * @param targetLevel Ziel níveau
     */
    public MoveShutterCommand(Shutter shutter, int targetLevel) {

        Preconditions.checkNotNull(shutter);
        Preconditions.checkArgument(targetLevel >= 0 && targetLevel <= 100);
        this.shutter = shutter;
        this.targetLevel = targetLevel;
    }

    /**
     * gibt den Rollladen zurück
     *
     * @return Rollladen
     */
    public Shutter getShutter() {
        return shutter;
    }

    /**
     * gibt das Ziel níveau zurück
     *
     * @return Ziel níveau
     */
    public int getTargetLevel() {
        return targetLevel;
    }
}
