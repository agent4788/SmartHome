package net.kleditzsch.SmartHome.utility.formatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Zahlen Fomatierung
 */
public abstract class NumberFormatUtil {

    /**
     * größe der Dezimalgruppen
     */
    private static int groupSize = 3;

    /**
     * Dezimaltrennzeichen
     */
    private static String decimalSeparator = ",";

    /**
     * Gruppierungstrennzeichen
     */
    private static String groupingSeparator = " ";

    /**
     * gibt die Tausender Gruppierung zurück
     *
     * @return Tausender Gruppierung
     */
    public static int getGroupSize() {
        return groupSize;
    }

    /**
     * setzt die Tausender Gruppierung
     *
     * @param groupSize Tausender Gruppierung
     */
    public static void setGroupSize(int groupSize) {
        NumberFormatUtil.groupSize = groupSize;
    }

    /**
     * gibt das Dezimaltrennzeichen zurück
     *
     * @return Dezimaltrennzeichen
     */
    public static String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * setzt das Dezimaltrennzeichen
     *
     * @param decimalSeparator Dezimaltrennzeichen
     */
    public static void setDecimalSeparator(String decimalSeparator) {
        NumberFormatUtil.decimalSeparator = decimalSeparator;
    }

    /**
     * gibt das Gruppierungstrennzeichen zurück
     *
     * @return Gruppierungstrennzeichen
     */
    public static String getGroupingSeparator() {
        return groupingSeparator;
    }

    /**
     * setzt das Gruppierungstrennzeichen
     *
     * @param groupingSeparator Gruppierungstrennzeichen
     */
    public static void setGroupingSeparator(String groupingSeparator) {
        NumberFormatUtil.groupingSeparator = groupingSeparator;
    }

    /**
     * Zahlen lesbar formatieren
     *
     * @param numberArg Zahl
     * @return lesbare Zeichenkette
     */
    public static String numberFormat (Object numberArg) {

        return NumberFormatUtil.numberFormat(numberArg, 2, groupSize, decimalSeparator, groupingSeparator);
    }

    /**
     * Zahlen lesbar formatieren
     *
     * @param numberArg Zahl
     * @param fractionDigits Anzahl der Deizmalstellen
     * @return lesbare Zeichenkette
     */
    public static String numberFormat (Object numberArg, int fractionDigits) {

        return NumberFormatUtil.numberFormat(numberArg, fractionDigits, groupSize, decimalSeparator, groupingSeparator);
    }

    /**
     * Zahlen lesbar formatieren
     *
     * @param numberArg Zahl
     * @param fractionDigits Anzahl der Deizmalstellen
     * @param groupSize Anzahl der Stellen einer tausender Gruppe
     * @param decimalSeparator Dezimaltrennzeichen
     * @param groupingSeparator Tausender Trennzeichen
     * @return lesbare Zeichenkette
     */
    public static String numberFormat (Object numberArg, int fractionDigits, int groupSize, String decimalSeparator, String groupingSeparator) {

        DecimalFormat numberFormat = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbols = numberFormat.getDecimalFormatSymbols();
        numberFormat.setMaximumFractionDigits(fractionDigits);
        numberFormat.setMinimumFractionDigits(fractionDigits);
        numberFormat.setGroupingUsed(true);
        numberFormat.setGroupingSize(groupSize);
        numberFormat.setParseBigDecimal(true);

        if (decimalSeparator != null && !decimalSeparator.isEmpty())
            decimalFormatSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
        else
            decimalFormatSymbols.setDecimalSeparator('.');

        if (groupingSeparator != null && !groupingSeparator.isEmpty())
            decimalFormatSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
        else
            numberFormat.setGroupingUsed(false);

        numberFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        return numberFormat.format(numberArg);
    }
}
