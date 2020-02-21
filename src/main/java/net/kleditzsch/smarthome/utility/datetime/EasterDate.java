package net.kleditzsch.smarthome.utility.datetime;

import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

/**
 * Errechnet das Osterdatum
 */
public abstract class EasterDate {

    /**
     * gibt das Datum des Ostersonntags zurück
     *
     * @param year Jahr
     * @return Datum des Ostersonntags
     */
    public static LocalDate of(int year) {

        return EasterDate.of(Year.of(year));
    }

    /**
     * gibt das Datum des Ostersonntags zurück
     *
     * @param year Jahr
     * @return Datum des Ostersonntags
     */
    public static LocalDate of(Year year) {

        Preconditions.checkNotNull(year);
        int X = year.getValue();
        int K = X / 100;
        int M = 15 + (3 * K + 3) / 4 - (8 * K + 13) / 25;
        int S = 2 - (3 * K + 3) / 4;
        int A = X % 19;
        int D = (19 * A + M) % 30;
        int R = (D + A / 11) / 29;
        int OG = 21 + D - R;
        int SZ = 7 - (X + X / 4 + S) % 7;
        int OE = 7 - (OG - SZ) % 7;
        int OS = OG + OE;

        return LocalDate.of(X, Month.MARCH, 1).plusDays(OS - 1);
    }

}
