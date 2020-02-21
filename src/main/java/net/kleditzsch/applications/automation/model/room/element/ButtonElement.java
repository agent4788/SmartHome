package net.kleditzsch.applications.automation.model.room.element;

import net.kleditzsch.applications.automation.model.global.SwitchCommand;
import net.kleditzsch.applications.automation.model.room.Interface.RoomElement;

import java.util.ArrayList;
import java.util.List;

public class ButtonElement extends RoomElement {

    private Type type = Type.BUTTON_ELEMENT;

    /**
     * liste mit den Befehlen
     */
    private List<SwitchCommand> commands = new ArrayList<>();

    /**
     * Button Texte
     */
    private String onButtonText = "an";
    private String offButtonText = "aus";

    /**
     * gibt an ob es ein an/aus button angezeigt werden soll
     */
    private boolean isDoubleButton = true;

    /**
     * Sicherheitsabfrage
     */
    private boolean safetyRequestEnabled = false;
    private String safetyRequestIcon = "question";
    private String safetyRequestHeaderText = "Ausführen";
    private String safetyRequestText = "Möchtest du den Befehl ausführen";
    private String safetyRequestExecuteButtonText = "ja";
    private String safetyRequestCancelButtonText = "nein";

    /**
     * gibt die Liste mit den Schaltbefehlen zurück
     *
     * @return Liste mit den Schaltbefehlen
     */
    public List<SwitchCommand> getCommands() {

        return commands;
    }

    /**
     * gibt an ob die Sicherheitsabfrage aktiviert ist
     *
     * @return Sicherheitsabfrage
     */
    public boolean isSafetyRequestEnabled() {
        return safetyRequestEnabled;
    }

    /**
     * aktiviert/deaktiviert die Sicherheitsabfrage
     *
     * @param safetyRequestEnabled Sicherheitsabfrage aktiviert/deaktiviert
     */
    public void setSafetyRequestEnabled(boolean safetyRequestEnabled) {
        this.safetyRequestEnabled = safetyRequestEnabled;
    }

    /**
     * gibt das Icon der Sicherheitsabfrage zurück
     *
     * @return Sicherheitsabfrage
     */
    public String getSafetyRequestIcon() {
        return safetyRequestIcon;
    }

    /**
     * setzt das Icon der Sicherheitsabfrage
     *
     * @param safetyRequestIcon Icon der Sicherheitsabfrage
     */
    public void setSafetyRequestIcon(String safetyRequestIcon) {
        this.safetyRequestIcon = safetyRequestIcon;
    }

    /**
     * gibt den Kopf Text der Sicherheitsabfrage zuürck
     *
     * @return Kopf Text der Sicherheitsabfrage
     */
    public String getSafetyRequestHeaderText() {
        return safetyRequestHeaderText;
    }

    /**
     * setzt den Kopf Text der Sicherheitsabfrage
     *
     * @param safetyRequestHeaderText Kopf Text der Sicherheitsabfrage
     */
    public void setSafetyRequestHeaderText(String safetyRequestHeaderText) {
        this.safetyRequestHeaderText = safetyRequestHeaderText;
    }

    /**
     * gibt den Text der Sicherheitsabfrage zurück
     *
     * @return Text der Sicherheitsabfrage
     */
    public String getSafetyRequestText() {
        return safetyRequestText;
    }

    /**
     * setzt den Text der Sicherheitsabfrage
     *
     * @param safetyRequestText Text der Sicherheitsabfrage
     */
    public void setSafetyRequestText(String safetyRequestText) {
        this.safetyRequestText = safetyRequestText;
    }

    /**
     * gibt den Text des "ausführen" Buttons der Sicherheitsabfrage zurück
     *
     * @return Text des "ausführen" Buttons der Sicherheitsabfrage
     */
    public String getSafetyRequestExecuteButtonText() {
        return safetyRequestExecuteButtonText;
    }

    /**
     * setzt den Text des "ausführen" Buttons der Sicherheitsabfrage
     *
     * @param safetyRequestExecuteButtonText Text des "ausführen" Buttons der Sicherheitsabfrage
     */
    public void setSafetyRequestExecuteButtonText(String safetyRequestExecuteButtonText) {
        this.safetyRequestExecuteButtonText = safetyRequestExecuteButtonText;
    }

    /**
     * gibt den Text des "nicht ausführen" Buttons der Sicherheitsabfrage zurück
     *
     * @return Text des "nicht ausführen" Buttons der Sicherheitsabfrage
     */
    public String getSafetyRequestCancelButtonText() {
        return safetyRequestCancelButtonText;
    }

    /**
     * setzt den Text des "nicht ausführen" Buttons der Sicherheitsabfrage
     *
     * @param safetyRequestCancelButtonText Text des "nicht ausführen" Buttons der Sicherheitsabfrage
     */
    public void setSafetyRequestCancelButtonText(String safetyRequestCancelButtonText) {
        this.safetyRequestCancelButtonText = safetyRequestCancelButtonText;
    }

    /**
     * gibt den Text für den an Button zurück
     *
     * @return Text für den an Button
     */
    public String getOnButtonText() {
        return onButtonText;
    }

    /**
     * setzt den Text für den an Button
     *
     * @param onButtonText Text für den an Button
     */
    public void setOnButtonText(String onButtonText) {
        this.onButtonText = onButtonText;
    }

    /**
     * gibt den Text für den aus Button zurück
     *
     * @return Text für den aus Button
     */
    public String getOffButtonText() {
        return offButtonText;
    }

    /**
     * setzt den Text für den aus Button
     *
     * @param offButtonText Text für den aus Button
     */
    public void setOffButtonText(String offButtonText) {
        this.offButtonText = offButtonText;
    }

    /**
     * gibt an ob ein an/aus Botton dargestellt werden soll
     *
     * @return an/aus Botton dargestellen
     */
    public boolean isDoubleButton() {
        return isDoubleButton;
    }

    /**
     * setzt die Datrstellung des an/aus Bottons
     *
     * @param doubleButton an/aus Botton Datrstellung
     */
    public void setDoubleButton(boolean doubleButton) {
        isDoubleButton = doubleButton;
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
