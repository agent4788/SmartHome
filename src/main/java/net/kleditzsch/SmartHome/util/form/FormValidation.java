package net.kleditzsch.SmartHome.util.form;

import com.google.common.net.InetAddresses;
import net.kleditzsch.SmartHome.global.base.ID;

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

    private HttpServletRequest request;

    private boolean success = true;

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
     * @return Wahrheitswer
     */
    public boolean fieldNotEmpty(String name) {

        return request.getParameter(name) != null && request.getParameter(name).length() > 0;
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return ID.of(value);
            } catch (Exception e) {}
        }
        setInvalid(name, String.format("Ungültige %s ID", displayName));
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

        String value = request.getParameter(name);
        if(value != null && (value.equals("1") || value.equals("0"))) {

            return value.equals("1");
        }
        setInvalid(name, String.format("Ungültige %s ID", displayName));
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                int intValue = Integer.parseInt(value);
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                long longValue = Long.parseLong(value);
                if(longValue >= min && longValue <= max) {

                    return longValue;
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                double doubleValue = Double.parseDouble(value);
                if(doubleValue >= min && doubleValue <= max) {

                    return doubleValue;
                }
            } catch (Exception e) {}
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

        String value = request.getParameter(name);
        if(value != null && value.length() >= minLength && value.length() <= maxLength) {

            return value;
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

        String value = request.getParameter(name);
        if(value != null && value.length() >= minLength && value.length() <= maxLength && pattern.matcher(value).find()) {

            return value;
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return LocalDate.parse(value, format);
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
    public LocalDate getLocalDate(String name, String displayName, DateTimeFormatter format, LocalDate min, LocalDate max) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                LocalDate date = LocalDate.parse(value, format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

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
     * @return Zeit
     */
    public LocalTime getLocalTime(String name, String displayName, DateTimeFormatter format) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return LocalTime.parse(value, format);
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
    public LocalTime getLocalTime(String name, String displayName, DateTimeFormatter format, LocalTime min, LocalTime max) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                LocalTime date = LocalTime.parse(value, format);
                if(min.isBefore(date) && max.isAfter(date)) {

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
     * @return Datum und Zeit
     */
    public LocalDateTime getLocalDateTime(String name, String displayName, DateTimeFormatter format) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return LocalDateTime.parse(value, format);
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
    public LocalDateTime getLocalDateTime(String name, String displayName, DateTimeFormatter format, LocalDateTime min, LocalDateTime max) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                LocalDateTime date = LocalDateTime.parse(value, format);
                if((min.isBefore(date) || min.isEqual(date)) && (max.isAfter(date) || max.isEqual(date))) {

                    return date;
                }
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
    public InetAddress getIpAddress(String name, String displayName) {

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return InetAddresses.forString(value);
            } catch (Exception e) {}
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

                        valueList.add(value);
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

                        valueList.add(value);
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

                    valueList.add(ID.of(value));
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

        String value = request.getParameter(name);
        if(value != null) {

            try {

                return Enum.valueOf(clazz, value);
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
}
