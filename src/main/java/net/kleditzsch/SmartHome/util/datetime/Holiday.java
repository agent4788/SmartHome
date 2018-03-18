package net.kleditzsch.SmartHome.util.datetime;

import net.kleditzsch.SmartHome.util.datetime.Holidays.GermanHolidays;

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
