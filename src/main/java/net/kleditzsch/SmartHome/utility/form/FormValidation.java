package net.kleditzsch.SmartHome.utility.form;

import com.google.common.net.InetAddresses;
import net.kleditzsch.SmartHome.model.base.ID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Formularfelder Valisieren
 */
public class FormValidation {

    /**
     * HTTP Request
     */
    private HttpServletRequest request;

    /**
     * Validierungsstatus
     */
    private boolean success = true;

    /**
     * Fehlermeldungen
     */
    private Map<String, String> errorMessages = new HashMap<>();

    /**
     * @param request Anfrageobjekt
     */
    private FormValidation(HttpServletRequest request) {

        this.request = request;
    }

    /**
     * erstellt einen neuen Validator
     *
     * @param request Anfrageobjekt
     */
    public static FormValidation create(HttpServletRequest request) {

        return new FormValidation(request);
    }

    /**
     * pürft ob ein Feld vorhanden ist
     *
     * @param name Feldname
     * @return Wahrheitswer
     */
    public boolean fieldExists(String name) {

        return request.getParameter(name) != null;
    }

    /**
     * pürft ob ein Feld vorhanden ist
     *
     * @param name Feldname
     * @return Wahrheitswert
     */
    public boolean fieldNotEmpty(String name) {

        return request.getParameter(name) != null && !request.getParameter(name).isBlank();
    }

    /**
     * pürft das Feld leer ist
     *
     * @param name Feldname
     * @return Wahrheitswert
     */
    public boolean fieldEmpty(String name) {

        return request.getParameter(name) == null || request.getParameter(name).isBlank();
    }

    /**
     * gibt den Wert des Feldes zurück (wenn vorhanden)
     *
     * @param name Feldname
     * @return Wert
     */
    public Optional<String> getValue(String name) {

        if(request.getParameter(name) != null) {

            return Optional.of(request.getParameter(name).trim());
        }
        return Optional.empty();
    }

    /**
     * pürft ob ein Feld vorhanden ist
     *
     * @param name Feldname
     * @return Wahrheitswer
     */
    public boolean uploadExist(String name) {

        try {

            return request.getPart(name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * pürft ob ein Feld vorhanden ist
     *
     * @param name Feldname
     * @return Wahrheitswer
     */
    public boolean uploadNotEmpty(String name) {

        try {

            return request.getPart(name) != null && request.getPart(name).getSize() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * validiert das Feld als ID
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return ID
     */
    public ID getId(String name, String displayName) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return ID.of(value.get());
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültige %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Wahrheitswert
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Wahrheitswert
     */
    public boolean getBoolean(String name, String displayName) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && (value.get().equals("1") || value.get().equals("0") || value.get().equals("on") || value.get().equals("off"))) {

            return value.get().equals("1") || value.get().equals("on");
        }
        setInvalid(name, String.format("Ungültige %s", displayName));
        return false;
    }

    /**
     * validiert das Feld als Wahrheitswert
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @return Wahrheitswert
     */
    public boolean optBoolean(String name, String displayName, boolean defaultValue) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            if((value.get().equals("1") || value.get().equals("0") || value.get().equals("on") || value.get().equals("off"))) {

                return value.get().equals("1") || value.get().equals("on");
            }
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültige %s", displayName));
        return false;
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Ganzzahl
     */
    public int getInteger(String name, String displayName) {

        return getInteger(name, displayName, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Ganzzahl
     */
    public int getInteger(String name, String displayName, int min, int max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                int intValue = Integer.parseInt(value.get());
                if(intValue >= min && intValue <= max) {

                    return intValue;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @return Ganzzahl
     */
    public int optInteger(String name, String displayName, int defaultValue) {

        return optInteger(name, displayName, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Ganzzahl
     */
    public int optInteger(String name, String displayName, int defaultValue, int min, int max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                int intValue = Integer.parseInt(value.get());
                if(intValue >= min && intValue <= max) {

                    return intValue;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Ganzzahl
     */
    public long getLong(String name, String displayName) {

        return getLong(name, displayName, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Ganzzahl
     */
    public long getLong(String name, String displayName, long min, long max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                long longValue = Long.parseLong(value.get());
                if(longValue >= min && longValue <= max) {

                    return longValue;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @return Ganzzahl
     */
    public long optLong(String name, String displayName, long defaultValue) {

        return optLong(name, displayName, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * validiert das Feld als Ganzzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Ganzzahl
     */
    public long optLong(String name, String displayName, long defaultValue, long min, long max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                long longValue = Long.parseLong(value.get());
                if(longValue >= min && longValue <= max) {

                    return longValue;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Gleitpunktzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Gleitpunktzahl
     */
    public double getDouble(String name, String displayName) {

        return getDouble(name, displayName, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * validiert das Feld als Gleitpunktzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Gleitpunktzahl
     */
    public double getDouble(String name, String displayName, double min, double max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                double doubleValue = Double.parseDouble(value.get());
                if(doubleValue >= min && doubleValue <= max) {

                    return doubleValue;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Gleitpunktzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @return Gleitpunktzahl
     */
    public double optDouble(String name, String displayName, double defaultValue) {

        return optDouble(name, displayName, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * validiert das Feld als Gleitpunktzahl
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param defaultValue Standard Wert
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Gleitpunktzahl
     */
    public double optDouble(String name, String displayName, double defaultValue, double min, double max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                double doubleValue = Double.parseDouble(value.get());
                if(doubleValue >= min && doubleValue <= max) {

                    return doubleValue;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return 0;
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Zeichenkette
     */
    public String getString(String name, String displayName) {

        return getString(name, displayName, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Zeichenkette
     */
    public String getString(String name, String displayName, int minLength, int maxLength) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && value.get().length() >= minLength && value.get().length() <= maxLength) {

            return value.get();
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Zeichenkette
     */
    public String optString(String name, String displayName, String defaultValue) {

        return optString(name, displayName, defaultValue, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Zeichenkette
     */
    public String optString(String name, String displayName, String defaultValue, int minLength, int maxLength) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            if(value.get().length() >= minLength && value.get().length() <= maxLength) {

                return value.get();
            }
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @return Zeichenkette
     */
    public String getString(String name, String displayName, Pattern pattern) {

        return getString(name, displayName, pattern, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Zeichenkette
     */
    public String getString(String name, String displayName, Pattern pattern, int minLength, int maxLength) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && value.get().length() >= minLength && value.get().length() <= maxLength && pattern.matcher(value.get()).find()) {

            return value.get();
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return "";
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @return Zeichenkette
     */
    public String optString(String name, String displayName, String defaultValue, Pattern pattern) {

        return optString(name, displayName, defaultValue, pattern, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Zeichenkette
     */
    public String optString(String name, String displayName, String defaultValue, Pattern pattern, int minLength, int maxLength) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            if(value.get().length() >= minLength && value.get().length() <= maxLength && pattern.matcher(value.get()).find()) {

                return value.get();
            }
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return "";
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param whitelist Wihitelist
     * @return Zeichenkette
     */
    public String getString(String name, String displayName, List<String> whitelist) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            for (String whiteElement : whitelist) {
                if(whiteElement.equalsIgnoreCase(value.get())) {

                    return value.get();
                }
            }
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return "";
    }

    /**
     * validiert das Feld als Zeichenkette
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param whitelist Wihitelist
     * @return Zeichenkette
     */
    public String optString(String name, String displayName, String defaultValue, List<String> whitelist) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            for (String whiteElement : whitelist) {
                if(whiteElement.equalsIgnoreCase(value.get())) {

                    return value.get();
                }
            }
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return "";
    }

    /**
     * validiert das Feld als Datum
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Datum
     */
    public LocalDate getLocalDate(String name, String displayName, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return LocalDate.parse(value.get(), format);
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Datum
     */
    public LocalDate optLocalDate(String name, String displayName, LocalDate defaultValue, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                return LocalDate.parse(value.get(), format);
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Datum
     */
    public LocalDate getLocalDate(String name, String displayName, DateTimeFormatter format, LocalDate min, LocalDate max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                LocalDate date = LocalDate.parse(value.get(), format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

                    return date;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Datum
     */
    public LocalDate optLocalDate(String name, String displayName, LocalDate defaultValue, DateTimeFormatter format, LocalDate min, LocalDate max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                LocalDate date = LocalDate.parse(value.get(), format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

                    return date;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Zeit
     */
    public LocalTime getLocalTime(String name, String displayName, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return LocalTime.parse(value.get(), format);
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Zeit
     */
    public LocalTime optLocalTime(String name, String displayName, LocalTime defaultValue, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                return LocalTime.parse(value.get(), format);
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Zeit
     */
    public LocalTime getLocalTime(String name, String displayName, DateTimeFormatter format, LocalTime min, LocalTime max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                LocalTime date = LocalTime.parse(value.get(), format);
                if(min.isBefore(date) && max.isAfter(date)) {

                    return date;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Zeit
     */
    public LocalTime optLocalTime(String name, String displayName, LocalTime defaultValue, DateTimeFormatter format, LocalTime min, LocalTime max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                LocalTime date = LocalTime.parse(value.get(), format);
                if(min.isBefore(date) && max.isAfter(date)) {

                    return date;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum und Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Datum und Zeit
     */
    public LocalDateTime getLocalDateTime(String name, String displayName, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return LocalDateTime.parse(value.get(), format);
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum und Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @return Datum und Zeit
     */
    public LocalDateTime optLocalDateTime(String name, String displayName, LocalDateTime defaultValue, DateTimeFormatter format) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                return LocalDateTime.parse(value.get(), format);
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum und Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Datum und Zeit
     */
    public LocalDateTime getLocalDateTime(String name, String displayName, DateTimeFormatter format, LocalDateTime min, LocalDateTime max) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                LocalDateTime date = LocalDateTime.parse(value.get(), format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

                    return date;
                }
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Datum und Zeit
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param format Format
     * @param min Minimalwert
     * @param max Maximalwert
     * @return Datum und Zeit
     */
    public LocalDateTime optLocalDateTime(String name, String displayName, LocalDateTime defaultValue, DateTimeFormatter format, LocalDateTime min, LocalDateTime max) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                LocalDateTime date = LocalDateTime.parse(value.get(), format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

                    return date;
                }
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als IP Adresse
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return IP Adresse
     */
    public InetAddress getIpAddress(String name, String displayName) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return InetAddresses.forString(value.get());
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als IP Adresse
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return IP Adresse
     */
    public InetAddress optIpAddress(String name, String displayName, InetAddress defaultValue) {

        Optional<String> value = getValue(name);
        if(value.isPresent() && fieldNotEmpty(name)) {

            try {

                return InetAddresses.forString(value.get());
            } catch (Exception e) {}
        } else {

            return defaultValue;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Liste von Strings
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Liste von Strings
     */
    public List<String> getStringList(String name, String displayName) {

        return getStringList(name, displayName, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Liste von Strings
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Liste von Strings
     */
    public List<String> getStringList(String name, String displayName, int minLength, int maxLength) {

        String[] values = request.getParameterValues(name);
        if(values.length > 0) {

            List<String> valueList = new ArrayList<>(values.length);
            for (String value : values) {

                if(value != null) {

                    if(value.length() >= minLength && value.length() <= maxLength) {

                        valueList.add(value.trim());
                    } else {

                        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
                        return null;
                    }
                }
            }
            return valueList;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Liste von Strings
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @return Liste von Strings
     */
    public List<String> getStringList(String name, String displayName, Pattern pattern) {

        return getStringList(name, displayName, pattern, 0, Integer.MAX_VALUE);
    }

    /**
     * validiert das Feld als Liste von Strings
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param pattern Suchmuster
     * @param minLength Minimallänge
     * @param maxLength Maximallänge
     * @return Liste von Strings
     */
    public List<String> getStringList(String name, String displayName, Pattern pattern, int minLength, int maxLength) {

        String[] values = request.getParameterValues(name);
        if(values != null) {

            List<String> valueList = new ArrayList<>(values.length);
            for (String value : values) {

                if(value != null) {

                    if(value.length() >= minLength && value.length() <= maxLength && pattern.matcher(value).find()) {

                        valueList.add(value.trim());
                    } else {

                        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
                        return null;
                    }
                }
            }
            return valueList;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Liste aus IDs
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @return Liste aus IDs
     */
    public List<ID> getIDList(String name, String displayName) {

        String[] values = request.getParameterValues(name);
        if(values != null) {

            List<ID> valueList = new ArrayList<>(values.length);
            for (String value : values) {

                try {

                    valueList.add(ID.of(value.trim()));
                } catch (Exception e) {

                    setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
                    return null;
                }
            }
            return valueList;
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * validiert das Feld als Dateiupload
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param maxFileSize Maximale Dateigröße
     * @return Upload referenz
     */
    public Part getUploadedFile(String name, String displayName, long maxFileSize) {

        return getUploadedFile(name, displayName, maxFileSize, Collections.emptyList());
    }

    /**
     * validiert das Feld als Dateiupload
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param maxFileSize Maximale Dateigröße
     * @param allowedContentTypes erlaubte Dateitypen
     * @return Upload referenz
     */
    public Part getUploadedFile(String name, String displayName, long maxFileSize, List<String> allowedContentTypes) {

        try {

            Part part = request.getPart(name);
            if(part != null) {

                if(part.getSize() <= maxFileSize) {

                    if(allowedContentTypes.size() > 0) {

                        //Content Type prüfen
                        if(allowedContentTypes.contains(part.getContentType())) {

                            return part;
                        } else {

                            setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
                            return null;
                        }
                    } else {

                        //ohne Content Type prüfung
                        return part;
                    }
                }
            }
            setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
            return null;
        } catch (Exception e) {

            setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
            return null;
        }
    }

    /**
     * gibt den Wert eines Enum zurück
     *
     * @param name Feldname
     * @param displayName Anzeigename
     * @param clazz Enum Class
     * @return Enum Wert
     */
    public <E extends Enum<E>> E getEnum(String name, String displayName, Class<E> clazz) {

        Optional<String> value = getValue(name);
        if(value.isPresent()) {

            try {

                return Enum.valueOf(clazz, value.get());
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültiger Wert für %s", displayName));
        return null;
    }

    /**
     * setzt ein Feld als Invalid
     *
     * @param name Feldname
     * @param message Meldung
     */
    public void setInvalid(String name, String message) {

        success = false;
        errorMessages.put(name, message);
    }

    /**
     * gibt an ob alle geprüften Felder erfolgreich validiert wurden
     *
     * @return Erfolgsmeldung
     */
    public boolean isSuccessful() {

        return success;
    }

    /**
     * gibt eine Map mit den Fehlerhaften Feldern und Fehlertexten aus
     *
     * @return Map mit Fehlermeldungen
     */
    public Map<String, String> getErrorMessages() {

        return Collections.unmodifiableMap(errorMessages);
    }

    public void printErrorMessages() {

        System.out.println("Formularfehler:");
        getErrorMessages().forEach((k, v) -> System.out.println(k + " -> " + v));
    }
}
