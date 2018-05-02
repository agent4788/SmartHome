package net.kleditzsch.SmartHome.util.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

/**
 * Hilfsfunktionen zur bearbeitung von Dateien
 */
public abstract class FileUtil {

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1024)
     *
     * @param filesize Dateigroeße
     * @return
     */
    public static String formatFilezizeBinary(long filesize) {

        return FileUtil.formatFilezizeBinary(filesize, true);
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1024)
     *
     * @param filesize Dateigroeße
     * @param useShortNames Namen in Kurzversion benutzen
     * @return
     */
    public static String formatFilezizeBinary(long filesize, boolean useShortNames) {

        if(useShortNames) {

            String[] norm = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};

            int count = norm.length;
            int factor = 1024;
            int x = 0;
            double size = filesize;

            while(size >= factor && x < count) {

                size /= factor;
                x++;
            }

            DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
            return format.format(size) + " " + norm[x];
        }

        String[] norm = {"Byte", "Kibibyte", "Mebibyte", "Gibibyte", "Tebibyte", "Pebibyte", "Exbibyte", "Zebibyte", "Yobibyte"};

        int count = norm.length;
        int factor = 1024;
        int x = 0;
        double size = filesize;

        while(size >= factor && x < count) {

            size /= factor;
            x++;
        }

        DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
        return format.format(size) + " " + norm[x];
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1000)
     *
     * @param filesize Dateigroeße
     * @return
     */
    public static String formatFilesize(int filesize) {

        return FileUtil.formatFilesize(filesize, true);
    }

    /**
     * Formatiert eine Dateigroeße in Bytes in einen Anzeigewert (Basis 1000)
     *
     * @param filesize Dateigroeße
     * @param useShortNames Namen in Kurzversion benutzen
     * @return
     */
    public static String formatFilesize(int filesize, boolean useShortNames) {

        if(useShortNames) {

            String[] norm = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

            int count = norm.length;
            int factor = 1000;
            int x = 0;
            double size = filesize;

            while(size >= factor && x < count) {

                size /= factor;
                x++;
            }

            DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
            return format.format(size) + " " + norm[x];
        }

        String[] norm = {"Byte", "Kilobyte", "Megabyte", "Gigabyte", "Terrabyte", "Petabyte", "Exabyte", "Zettabyte", "Yottabyte"};

        int count = norm.length;
        int factor = 1000;
        int x = 0;
        double size = filesize;

        while(size >= factor && x < count) {

            size /= factor;
            x++;
        }

        DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );
        return format.format(size) + " " + norm[x];
    }

    /**
     * liest eine Datei als String ein
     *
     * @param path Pfad
     * @return Dateiinhalt
     */
    public static String readFile(Path path) throws IOException  {

        return readFile(path, Charset.defaultCharset());
    }

    /**
     * liest eine Datei als String ein
     *
     * @param path Pfad
     * @param charset Zeichensatz
     * @return Dateiinhalt
     */
    public static String readFile(Path path, Charset charset) throws IOException  {

        if(Files.exists(path)) {

            byte[] encoded = Files.readAllBytes(path);
            return new String(encoded, charset);
        }
        return "";
    }
}
