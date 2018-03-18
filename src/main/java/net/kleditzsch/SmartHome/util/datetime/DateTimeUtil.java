package net.kleditzsch.SmartHome.util.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Hilfsfunktionen für Datum und Zeit
 */
public abstract class DateTimeUtil {

    /**
     * prüft ob ein Datumsobjekt in der Zukunft liegt
     *
     * @param future zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isFuture(LocalDateTime future) {

        return future.isAfter(LocalDateTime.now());
    }

    /**
     * prüft ob ein Datumsobjekt in der Zukunft liegt
     *
     * @param future zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isFuture(LocalDate future) {

        return future.isAfter(LocalDate.now());
    }

    /**
     * prüft ob ein Datumsobjekt in der Zukunft liegt
     *
     * @param future zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isFuture(LocalTime future) {

        return future.isAfter(LocalTime.now());
    }

    /**
     * prüft ob ein Datumsobjekt in der Vergangenheit liegt
     *
     * @param past zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isPast(LocalDateTime past) {

        return past.isBefore(LocalDateTime.now());
    }

    /**
     * prüft ob ein Datumsobjekt in der Vergangenheit liegt
     *
     * @param past zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isPast(LocalDate past) {

        return past.isBefore(LocalDate.now());
    }

    /**
     * prüft ob ein Datumsobjekt in der Vergangenheit liegt
     *
     * @param past zu prüfendes Objekt
     * @return Ergebnis
     */
    public static boolean isPast(LocalTime past) {

        return past.isBefore(LocalTime.now());
    }
}
