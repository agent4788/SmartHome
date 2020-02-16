package net.kleditzsch.SmartHome.utility.formatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class DateTimeFormatUtil {

    /**
     * Datum und Zeit Fomatierung
     */
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Datum Fomatierung
     */
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Zeit Fomatierung
     */
    private static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * fomatiert Datum und Zeit
     *
     * @param dateTime Datumsobjekt
     * @return Lesbare Zeichenkette
     */
    public static String format(LocalDateTime dateTime) {

        return dateTimeFormat.format(dateTime);
    }

    /**
     * fomatiert Datum
     *
     * @param date Datumsobjekt
     * @return Lesbare Zeichenkette
     */
    public static String format(LocalDate date) {

        return dateFormat.format(date);
    }

    /**
     * fomatiert Zeit
     *
     * @param time Datumsobjekt
     * @return Lesbare Zeichenkette
     */
    public static String format(LocalTime time) {

        return timeFormat.format(time);
    }

    /**
     * gibt den Datum und Zeit Formatter zurück
     *
     * @return Datum und Zeit Formatter
     */
    public static DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }

    /**
     * setzt den Datum und Zeit Formatter
     *
     * @param dateTimeFormat Datum und Zeit Formatter
     */
    public static void setDateTimeFormat(DateTimeFormatter dateTimeFormat) {
        DateTimeFormatUtil.dateTimeFormat = dateTimeFormat;
    }

    /**
     * setzt den Datum und Zeit Formatter
     *
     * @param dateTimeFormat Datum und Zeit Format
     */
    public static void setDateTimeFormat(String dateTimeFormat) {
        DateTimeFormatUtil.dateTimeFormat = DateTimeFormatter.ofPattern(dateTimeFormat);
    }

    /**
     * gibt den DatumFormatter zurück
     *
     * @return Datum Formatter
     */
    public static DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    /**
     * setzt den Datum Formatter
     *
     * @param dateFormat Datum Formatter
     */
    public static void setDateFormat(DateTimeFormatter dateFormat) {
        DateTimeFormatUtil.dateFormat = dateFormat;
    }

    /**
     * setzt den Datum Formatter
     *
     * @param dateFormat DatumFormat
     */
    public static void setDateFormat(String dateFormat) {
        DateTimeFormatUtil.dateFormat = DateTimeFormatter.ofPattern(dateFormat);
    }

    /**
     * gibt den Zeit Formatter zurück
     *
     * @return Zeit Formatter
     */
    public static DateTimeFormatter getTimeFormat() {
        return timeFormat;
    }

    /**
     * setzt den Zeit Formatter
     *
     * @param timeFormat Zeit Formatter
     */
    public static void setTimeFormat(DateTimeFormatter timeFormat) {
        DateTimeFormatUtil.timeFormat = timeFormat;
    }

    /**
     * setzt den Zeit Formatter
     *
     * @param timeFormat Zeit Format
     */
    public static void setTimeFormat(String timeFormat) {
        DateTimeFormatUtil.timeFormat = DateTimeFormatter.ofPattern(timeFormat);
    }
}
