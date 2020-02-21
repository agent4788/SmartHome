package net.kleditzsch.smarthome.utility.datetime.Holidays;

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
