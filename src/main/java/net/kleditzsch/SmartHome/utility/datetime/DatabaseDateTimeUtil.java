package net.kleditzsch.SmartHome.utility.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Hilfsfunktionen zum Serialisieren und Parsen von Zeiten für die Datenbank
 */
public abstract class DatabaseDateTimeUtil {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDateTime Objekt
     *
     * @param dateTimeStr Datums String
     * @return Datumsobjekt
     */
    public static LocalDateTime parseDateTimeFromDatabase(String dateTimeStr) {

        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDateTime Objekt
     *
     * @param ldt Datums und Zeit Objekt
     * @return Datum als Zeichenkette
     */
    public static String getDatabaseDateTimeStr(LocalDateTime ldt) {

        return ldt.format(dateTimeFormatter);
    }

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDate Objekt
     *
     * @param dateStr Datums String
     * @return Datums und Zeit Objekt
     */
    public static LocalDate parseDateFromDatabase(String dateStr) {

        return LocalDate.parse(dateStr, dateFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDate Objekt
     *
     * @param dt Datums Objekt
     * @return Datum als Zeichenkette
     */
    public static String getDatabaseDateStr(LocalDate dt) {

        return dt.format(dateFormatter);
    }

    /**
     * erstellt aus einem Datums und Zeit String ein LocalDateTime Objekt
     *
     * @param timeStr Zeit String
     * @return Datumsobjekt
     */
    public static LocalTime parseTimeFromDatabase(String timeStr) {

        return LocalTime.parse(timeStr, timeFormatter);
    }

    /**
     * erstellt einen Datums und Zeit String aus einem LocalDateTime Objekt
     *
     * @param lt Zeit Objekt
     * @return Datum als Zeichenkette
     */
    public static String getDatabaseTimeStr(LocalTime lt) {

        return lt.format(timeFormatter);
    }

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
