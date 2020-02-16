package net.kleditzsch.SmartHome.utility.datetime.Holidays;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import net.kleditzsch.SmartHome.utility.datetime.EasterDate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;

/**
 * Deutsche feiertage
 */
public class GermanHolidays {

    /**
     * Feiertage
     */
    public enum Holidays {

        //Neujahr
        @SerializedName("NEW_YEARS_DAY")
        NEW_YEARS_DAY,
        //Heilige Drei Könige
        @SerializedName("EPIPHANY")
        EPIPHANY,
        //Gruendonnerstag
        @SerializedName("MAUNDY_THURSDAY")
        MAUNDY_THURSDAY,
        //Karfreitag
        @SerializedName("GOOD_FRIDAY")
        GOOD_FRIDAY,
        //Ostersonntag
        @SerializedName("EASTER_DAY")
        EASTER_DAY,
        //Ostermontag
        @SerializedName("EASTER_MONDAY")
        EASTER_MONDAY,
        //Tag der Arbeit
        @SerializedName("DAY_OF_WORK")
        DAY_OF_WORK,
        //Christi Himmelfahrt
        @SerializedName("ASCENSION_DAY")
        ASCENSION_DAY,
        //Pfingstsonntag
        @SerializedName("WHIT_SUN")
        WHIT_SUN,
        //Pfingstmontag
        @SerializedName("WHIT_MONDAY")
        WHIT_MONDAY,
        //Fronleichnam
        @SerializedName("CORPUS_CHRISTI")
        CORPUS_CHRISTI,
        //Mariae Himmelfahrt
        @SerializedName("ASSUMPTION")
        ASSUMPTION,
        //Tag der Deutschen Einheit
        @SerializedName("GERMAN_UNIFICATION_DAY")
        GERMAN_UNIFICATION_DAY,
        //Reformationstag
        @SerializedName("REFOMATION_DAY")
        REFOMATION_DAY,
        //Allerheiligen
        @SerializedName("ALL_SAINTS_DAY")
        ALL_SAINTS_DAY,
        //Buss- und Bettag
        @SerializedName("DAY_OF_REPENTANCE")
        DAY_OF_REPENTANCE,
        //Heiligabend
        @SerializedName("CHRISTMAS_DAY")
        CHRISTMAS_DAY,
        //1. Weihnachtstag
        @SerializedName("XMAS_DAY")
        XMAS_DAY,
        //2. Weihnachtstag
        @SerializedName("BOXING_DAY")
        BOXING_DAY,
        //Silvester
        @SerializedName("NEW_YEARS_EVE")
        NEW_YEARS_EVE
    }

    /**
     * Jahr
     */
    private int year = 2000;

    public GermanHolidays() {

        year = Year.now().getValue();
    }

    /**
     * @param year Jahr
     */
    public GermanHolidays(int year) {
        this.year = year;
    }

    /**
     * gibt das Jahr zurück
     *
     * @return Jahr
     */
    public int getYear() {
        return year;
    }

    /**
     * setzt das Jahr
     *
     * @param year Jaht
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * gibt das Datum von "Neujahr" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate newYearsDay() {

        return LocalDate.of(this.year, 1, 1);
    }

    /**
     * gibt das Datum von "Heilige Drei Koenige" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate epiphany() {

        return LocalDate.of(this.year, 1, 6);
    }

    /**
     * gibt das Datum von "Gründonnerstag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate maundyThursday() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.minusDays(3);
    }

    /**
     * gibt das Datum von "Karfreitag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate goodFriday() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.minusDays(2);
    }

    /**
     * gibt das Datum von "Ostersonntag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate easterDay() {

        return EasterDate.of(this.year);
    }

    /**
     * gibt das Datum von "Ostersonntag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate easterMonday() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.plusDays(1);
    }

    /**
     * gibt das Datum von "Tag der Arbeit" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate dayOfWork() {

        return LocalDate.of(this.year, 5, 1);
    }

    /**
     * gibt das Datum von "Christi Himmelfahrt" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate ascensionDay() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.plusDays(39);
    }

    /**
     * gibt das Datum von "Pfingstsonntag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate whitsun() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.plusDays(49);
    }

    /**
     * gibt das Datum von "Pfingstmontag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate whitMonday() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.plusDays(50);
    }

    /**
     * gibt das Datum von "Fronleichnam" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate corpusChristi() {

        LocalDate easterSunday = EasterDate.of(this.year);
        return easterSunday.plusDays(60);
    }

    /**
     * gibt das Datum von "Mariä Himmelfahrt" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate assumption() {

        return LocalDate.of(this.year, 8, 15);
    }

    /**
     * gibt das Datum von "Tag der Deutschen Einheit" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate germanUnificationDay() {

        return LocalDate.of(this.year, 10, 3);
    }

    /**
     * gibt das Datum von "Reformationstag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate reformationDay() {

        return LocalDate.of(this.year, 10, 31);
    }

    /**
     * gibt das Datum von "Allerheiligen" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate allSaintsDay() {

        return LocalDate.of(this.year, 11, 1);
    }

    /**
     * gibt das Datum von "Buss- und Bettag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate dayOfRepentance() {

        LocalDate date = LocalDate.of(this.year, 11, 23).with(TemporalAdjusters.previous(DayOfWeek.WEDNESDAY));
        return date;
    }

    /**
     * gibt das Datum von "Heiligabend" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate christmasDay() {

        return LocalDate.of(this.year, 12, 24);
    }

    /**
     * gibt das Datum von "1. Weihnachtstag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate xmasDay() {

        return LocalDate.of(this.year, 12, 25);
    }

    /**
     * gibt das Datum von "2. Weihnachtstag" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate boxingDay() {

        return LocalDate.of(this.year, 12, 26);
    }

    /**
     * gibt das Datum von "Silvester" zurueck
     *
     * @return Datumsobjekt
     */
    public LocalDate newYearsEve() {

        return LocalDate.of(this.year, 12, 31);
    }

    /**
     * gibt das Datum zu einem bestimmten Feiertag zurück
     *
     * @param holidays Feiertag
     * @return Datum
     */
    public LocalDate getHolidayDate(Holidays holidays) {

        Preconditions.checkNotNull(holidays);
        switch (holidays) {

            case NEW_YEARS_DAY:

                return newYearsDay();
            case EPIPHANY:

                return epiphany();
            case MAUNDY_THURSDAY:

                return maundyThursday();
            case GOOD_FRIDAY:

                return goodFriday();
            case EASTER_DAY:

                return easterDay();
            case EASTER_MONDAY:

                return easterMonday();
            case DAY_OF_WORK:

                return dayOfWork();
            case ASCENSION_DAY:

                return ascensionDay();
            case WHIT_SUN:

                return whitsun();
            case WHIT_MONDAY:

                return whitMonday();
            case CORPUS_CHRISTI:

                return corpusChristi();
            case ASSUMPTION:

                return assumption();
            case GERMAN_UNIFICATION_DAY:

                return germanUnificationDay();
            case REFOMATION_DAY:

                return reformationDay();
            case ALL_SAINTS_DAY:

                return allSaintsDay();
            case DAY_OF_REPENTANCE:

                return dayOfRepentance();
            case CHRISTMAS_DAY:

                return christmasDay();
            case XMAS_DAY:

                return xmasDay();
            case BOXING_DAY:

                return boxingDay();
            case NEW_YEARS_EVE:

                return newYearsEve();
            default:

                throw new IllegalArgumentException("Ungültiger Feiertag");
        }
    }
}
