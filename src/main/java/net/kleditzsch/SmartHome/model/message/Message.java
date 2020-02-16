package net.kleditzsch.SmartHome.model.message;

import net.kleditzsch.SmartHome.model.base.ID;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * System Meldung
 */
public class Message {

    /**
     * Meldungs Status
     */
    public enum Type {

        info,
        success,
        warning,
        error
    }

    /**
     * Quittierung
     */
    public enum AckType {

        auto,
        manual
    }

    /**
     * ID
     */
    private ID id;

    /**
     * Zeitstempel
     */
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Modul
     */
    private String module = "global";

    /**
     * Meldungstyp
     */
    private Type type = Type.info;

    /**
     * Quittierung anfordern
     */
    private boolean acknowledgeRequired = false;

    /**
     * Quittierungs Typ
     */
    private AckType acknowledgeType = AckType.auto;

    /**
     * Quittiert
     */
    private boolean acknowledgeApproved = false;

    /**
     * Zeitstempel Quittierung
     */
    private LocalDateTime acknowledgeApprovedTime = LocalDateTime.of(2000, 1, 1, 0, 0);

    /**
     * Meldung
     */
    private String message = "";

    /**
     * Detail Informationen
     */
    private String description = "";

    /**
     * Zusatz Informationen
     */
    private Map<String, String> extraInformation = new HashMap<>();

    public Message() { }

    /**
     * @param module Modul
     * @param type Meldungstyp
     * @param message Meldung
     */
    public Message(String module, Type type, String message) {

        id = ID.create();
        timestamp = LocalDateTime.now();
        this.module = module;
        this.type = type;
        this.message = message;
    }

    /**
     * @param module Modul
     * @param type Meldungstyp
     * @param message Meldung
     * @param description Detail Informationen
     */
    public Message(String module, Type type, String message, String description) {

        id = ID.create();
        timestamp = LocalDateTime.now();
        this.module = module;
        this.type = type;
        this.message = message;
        this.description = description;
    }

    /**
     * @param module Modul
     * @param type Meldungstyp
     * @param message Meldung
     * @param exception Ausnahme (Stack Trace)
     */
    public Message(String module, Type type, String message, Exception exception) {

        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);
        exception.printStackTrace(writer);

        id = ID.create();
        timestamp = LocalDateTime.now();
        this.module = module;
        this.type = type;
        this.message = message;
        this.description = strWriter.toString();
    }

    /**
     * quittiert die Meldung
     */
    public void approveAcknowledge() {

        acknowledgeApproved = true;
        acknowledgeApprovedTime = LocalDateTime.now();
    }

    /**
     * gibt die ID zurück
     *
     * @return ID
     */
    public ID getId() {
        return id;
    }

    /**
     * setzt die ID
     *
     * @param id ID
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * gibt den Zeitstempel der Meldung zurück
     *
     * @return Zeitstempel
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * setzt den Zeitstempel der Meldung
     *
     * @param timestamp Zeitstempel
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * gibt das Modul zurück
     *
     * @return Modul
     */
    public String getModule() {
        return module;
    }

    /**
     * setzt das Modul
     *
     * @param module Modul
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * gibt den Meldungstyp zurück
     *
     * @return Meldungstyp
     */
    public Type getType() {
        return type;
    }

    /**
     * setzt den Meldungstyp
     *
     * @param type Meldungstyp
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * gibt an ob die Meldung quittiert werden muss
     *
     * @return Quittierung erforderlich
     */
    public boolean isAcknowledgeRequired() {
        return acknowledgeRequired;
    }

    /**
     * setzt die Quittierung der Meldung
     *
     * @param acknowledgeRequired Quittierung der Meldung
     */
    public void setAcknowledgeRequired(boolean acknowledgeRequired) {
        this.acknowledgeRequired = acknowledgeRequired;
    }

    /**
     * gibt den Quittierungstyp zurück
     *
     * @return Quittierungstyp
     */
    public AckType getAcknowledgeType() {
        return acknowledgeType;
    }

    /**
     * setzt den Quittierungstyp
     *
     * @param acknowledgeType Quittierungstyp
     */
    public void setAcknowledgeType(AckType acknowledgeType) {
        this.acknowledgeType = acknowledgeType;
    }

    /**
     * gibt an ob die Meldung Quittiert wurde
     *
     * @return Meldung quittiert
     */
    public boolean isAcknowledgeApproved() {
        return acknowledgeApproved;
    }

    /**
     * setzt die Meldung als quittiert
     *
     * @param acknowledgeApproved Meldung quittiert
     */
    public void setAcknowledgeApproved(boolean acknowledgeApproved) {
        this.acknowledgeApproved = acknowledgeApproved;
    }

    /**
     * gibt den Zeitstempel der Quittierung zurück
     *
     * @return Zeitstempel der Quittierung
     */
    public LocalDateTime getAcknowledgeApprovedTime() {
        return acknowledgeApprovedTime;
    }

    /**
     * setzt den Zeitstempel der Quittierung
     *
     * @param acknowledgeApprovedTime Zeitstempel der Quittierung
     */
    public void setAcknowledgeApprovedTime(LocalDateTime acknowledgeApprovedTime) {
        this.acknowledgeApprovedTime = acknowledgeApprovedTime;
    }

    /**
     * gibt die Meldung zurück
     *
     * @return Meldung
     */
    public String getMessage() {
        return message;
    }

    /**
     * setzt die Meldung
     *
     * @param message Meldung
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * gibt die Detailinformationen zurück
     *
     * @return Detailinformationen
     */
    public String getDescription() {
        return description;
    }

    /**
     * setzt die Detailinformationen
     *
     * @param description Detailinformationen
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gibt die Liste mit den extra Informationen zurück
     *
     * @return extra Informationen
     */
    public Map<String, String> getExtraInformation() {
        return extraInformation;
    }
}
