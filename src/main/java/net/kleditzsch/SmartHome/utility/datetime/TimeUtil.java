package net.kleditzsch.SmartHome.utility.datetime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Hilfsfunktionen zur Datums und Zeitverarbeitung
 */
public abstract class TimeUtil {

    /**
     * gibt ein Lokalisiertes DateTime Objekt zum Zeitstempel zurueck (Zeitzone Europe/Berlin)
     *
     * @param seconds Zeitstempel
     * @return
     */
    public static LocalDateTime getLocalDateTimeOfEpochSeconds(long seconds) {

        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds),
                TimeZone.getDefault().toZoneId());
    }

    /**
     * gibt ein Instant zum Zeitstempel zurueck
     *
     * @param seconds Zeitstempel
     * @return
     */
    public static Instant getInstantOfEpoch(long seconds) {

        return Instant.ofEpochSecond(seconds, 0);
    }

    /**
     * formatiert eine Zeitdifferenz
     *
     * @param diff Zeitdifferenz
     * @return lesbare Zeitdifferenz
     */
    public static String formatDuration(Duration diff) {

        return TimeUtil.formatDuration(diff, true);
    }

    /**
     * formatiert eine Zeitdifferenz
     *
     * @param diff Zeitdifferenz
     * @param smartShort Smartes kürzen der Angaben
     * @return lesbare Zeitdifferenz
     */
    public static String formatDuration(Duration diff, boolean smartShort) {

        long years = 0, month = 0;
        long days = Math.abs(diff.toDays());
        if(days > 365) {

            years = (long) Math.floor(days / 365);
            diff = (!diff.isNegative() ? diff.minusDays(years * 365) : diff.plusDays(years * 365));
            days = Math.abs(diff.toDays());
        }
        if(days > 30) {

            month = (long) Math.floor(days / 30);
            diff = (!diff.isNegative() ? diff.minusDays(month * 30) : diff.plusDays(month * 30));
            days = Math.abs(diff.toDays());
        }
        long hours = Math.abs(diff.toHoursPart());
        long minutes = Math.abs(diff.toMinutesPart());

        String output = "";
        if(diff.isNegative()) {

            //vergangenheit
            output += "vor";
        } else {

            //Zukunft
            output += "in";
        }

        if(years != 0) {

            if(smartShort && month > 6) {
                years += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + years + (years == 1 ? " Jahr" : " Jahre");
        }

        if(month != 0 && (!smartShort || years < 5)) {

            if(smartShort && days > 15) {
                month += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + month + (month == 1 ? " Monat" : " Monate");
        }

        if(days != 0 && (!smartShort || (years < 1 && month < 6))) {

            if(smartShort && hours > 12) {
                days += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + days + (days == 1 ? " Tag" : " Tagen");
        }

        if(hours != 0 && (!smartShort || (years < 1 && month < 1 && days < 15))) {

            if(smartShort && minutes > 30) {
                hours += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + hours + (hours == 1 ? " Stunde" : " Stunden");
        }

        if(minutes != 0 && (!smartShort || (years < 1 && month < 1 && days < 1 && hours < 12))) {

            output += " " + minutes + (minutes == 1 ? " Minute" : " Minuten");
        }

        if(days == 0 && hours == 0 && minutes == 0 && diff.isNegative()) {

            output = "gerade eben";
        } else if(days == 0 && hours == 0 && minutes == 0 && !diff.isNegative()) {

            output = "jetzt";
        }

        return output.trim();
    }

    /**
     * formatiert eine Zeit Angabe in Sekunden
     *
     * @param seconds Zeitdifferenz in Seknunden
     * @return lesbare Zeitdifferenz
     */
    public static String formatSeconds(long seconds) {

        return TimeUtil.formatSeconds(seconds, false);
    }

    /**
     * formatiert eine Zeit Angabe in Sekunden
     *
     * @param seconds Zeitdifferenz in Seknunden
     * @param smartShort Smartes kürzen der Angaben
     * @return lesbare Zeitdifferenz
     */
    public static String formatSeconds(long seconds, boolean smartShort) {

        Duration diff = Duration.between(Instant.now(), Instant.ofEpochSecond(Instant.now().getEpochSecond() + seconds));

        long years = 0, month = 0;
        long days = Math.abs(diff.toDays());
        if(days > 365) {

            years = (long) Math.floor(days / 365);
            diff = (!diff.isNegative() ? diff.minusDays(years * 365) : diff.plusDays(years * 365));
            days = Math.abs(diff.toDays());
        }
        if(days > 30) {

            month = (long) Math.floor(days / 30);
            diff = (!diff.isNegative() ? diff.minusDays(month * 30) : diff.plusDays(month * 30));
            days = Math.abs(diff.toDays());
        }
        long hours = Math.abs(diff.toHoursPart());
        long minutes = Math.abs(diff.toMinutesPart());

        String output = "";
        if(years != 0) {

            if(smartShort && month > 6) {
                years += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + years + (years == 1 ? " Jahr" : " Jahre");
        }

        if(month != 0 && (!smartShort || years < 5)) {

            if(smartShort && days > 15) {
                month += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + month + (month == 1 ? " Monat" : " Monate");
        }

        if(days != 0 && (!smartShort || (years < 1 && month < 6))) {

            if(smartShort && hours > 12) {
                days += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + days + (days == 1 ? " Tag" : " Tagen");
        }

        if(hours != 0 && (!smartShort || (years < 1 && month < 1 && days < 15))) {

            if(smartShort && minutes > 30) {
                hours += (!diff.isNegative() ? 1 : -1);
            }
            output += " " + hours + (hours == 1 ? " Stunde" : " Stunden");
        }

        if(minutes != 0 && (!smartShort || (years < 1 && month < 1 && days < 1 && hours < 12))) {

            output += " " + minutes + (minutes == 1 ? " Minute" : " Minuten");
        }

        if(years == 0 && month == 0 && days == 0 && hours == 0 && minutes == 0 && diff.isNegative()) {

            output = "gerade eben";
        } else if(years == 0 && month == 0 && days == 0 && hours == 0 && minutes == 0 && !diff.isNegative()) {

            output = "jetzt";
        }

        return output.trim();
    }
}
