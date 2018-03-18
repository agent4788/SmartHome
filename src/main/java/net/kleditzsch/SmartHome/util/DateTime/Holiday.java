package net.kleditzsch.SmartHome.util.DateTime;

import net.kleditzsch.SmartHome.util.DateTime.Holidays.GermanHolidays;

import java.time.Year;

/**
 * Verwaltung der Feiertage
 */
public abstract class Holiday {

    /**
     * Deutsche feiertage
     *
     * @return Deutsche Feiertage
     */
    public static GermanHolidays getGermanHolidays() {

        return new GermanHolidays(Year.now().getValue());
    }
}
