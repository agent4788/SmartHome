package net.kleditzsch.smarthome.utility.datetime;

import java.time.*;
import java.util.Date;

/**
 * Hilfsfunktionen zum übersetzen der alten Date API zur DateTime API
 */
public abstract class DateTimeConverter {

    /**
     * konvertiert ein Date Objekt zu einen LocalDateTime Objekt
     *
     * @param date Datumsobjekt
     * @return LocalDateTime Objekt
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {

        return date.toInstant().atOffset(ZoneOffset.ofHours(0)).toLocalDateTime();
    }

    /**
     * konvertiert ein Date Objekt zu einen LocalDateTime Objekt
     *
     * @param date Datumsobjekt
     * @param zone Zeitzone
     * @return LocalDateTime Objekt
     */
    public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zone) {

        return date.toInstant().atZone(zone).toLocalDateTime();
    }

    /**
     * konvertiert ein Date Objekt zu einen LocalTime Objekt
     *
     * @param date Datumsobjekt
     * @return LocalTime Objekt
     */
    public static LocalTime dateToLocallTime(Date date) {

        return date.toInstant().atOffset(ZoneOffset.ofHours(0)).toLocalTime();
    }

    /**
     * konvertiert ein Date Objekt zu einen LocalTime Objekt
     *
     * @param date Datumsobjekt
     * @param zone Zeitzone
     * @return LocalTime Objekt
     */
    public static LocalTime dateToLocalTime(Date date, ZoneId zone) {

        return date.toInstant().atZone(zone).toLocalTime();
    }

    /**
     * konvertiert ein Date Objekt zu einen LocalDate Objekt
     *
     * @param date Datumsobjekt
     * @return LocalDate Objekt
     */
    public static LocalDate dateToLocalDate(Date date) {

        return date.toInstant().atOffset(ZoneOffset.ofHours(0)).toLocalDate();
    }

    /**
     * konvertiert ein Date Objekt zu einen LocalDate Objekt
     *
     * @param date Datumsobjekt
     * @param zone Zeitzone
     * @return LocalDate Objekt
     */
    public static LocalDate dateToLocalDate(Date date, ZoneId zone) {

        return date.toInstant().atZone(zone).toLocalDate();
    }
}
