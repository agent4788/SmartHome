package net.kleditzsch.smarthome.utility.datetime;

import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class DateTimeSerializer {

    //Formatter initalisieren
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Serialisiert ein Datum/Zeit Objekt
     *
     * @param e Datum/Zeit
     * @return Serialisierter String
     */
    public static String toString(LocalDateTime e) {

        Preconditions.checkNotNull(e);
        return e.format(dateTimeFormatter);
    }

    /**
     * Serialisiert ein Zeit Objekt
     *
     * @param e Zeit
     * @return Serialisierter String
     */
    public  static String toString(LocalTime e) {

        Preconditions.checkNotNull(e);
        return e.format(timeFormatter);
    }

    /**
     * Serialisiert ein Datums Objekt
     *
     * @param e Datum
     * @return Serialisierter String
     */
    public static String toString(LocalDate e) {

        Preconditions.checkNotNull(e);
        return e.format(dateFormatter);
    }

    /**
     * deserialisiert einen Datum/Zeit String
     *
     * @param e Datum/Zeit
     * @return  Datum/Zeit Objekt
     */
    public static LocalDateTime dateTimeFromString(String e) {

        Preconditions.checkNotNull(e);
        return LocalDateTime.parse(e, dateTimeFormatter);
    }

    /**
     * deserialisiert einen Zeit String
     *
     * @param e Zeit
     * @return  Zeit Objekt
     */
    public static LocalTime timeFromString(String e) {

        Preconditions.checkNotNull(e);
        return LocalTime.parse(e, timeFormatter);
    }

    /**
     * deserialisiert einen Datums String
     *
     * @param e Datum
     * @return  Datums Objekt
     */
    public static LocalDate dateFromString(String e) {

        Preconditions.checkNotNull(e);
        return LocalDate.parse(e, dateFormatter);
    }
}
