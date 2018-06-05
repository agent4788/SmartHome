package net.kleditzsch.SmartHome.util.collection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Hilfsfunktionen für Collections
 */
public abstract class CollectionUtil {

    /**
     * verschiebt ein ELement aus der Liste an eine neue Position
     *
     * @param sourceIndex aktueller Index
     * @param targetIndex Tiel Index
     * @param list Liste
     */
    public static <T> void moveItem(int sourceIndex, int targetIndex, List<T> list) {

        if (sourceIndex <= targetIndex) {
            Collections.rotate(list.subList(sourceIndex, targetIndex + 1), -1);
        } else {
            Collections.rotate(list.subList(targetIndex, sourceIndex + 1), 1);
        }
    }

    /**
     * speichert die Daten einer Collection aus Strings in eine Textdatei
     *
     * @param fileName Dateiname
     * @param collection Collection
     */
    public static void save(String fileName, Collection<String> collection) throws IOException {

        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        collection.forEach(pw::println);
        pw.close();
    }

    /**
     * lädt die Daten aus einer Textdatei in die Collection
     *
     * @param fileName Dateiname
     * @param collection Collection
     */
    public static void load(String fileName, Collection<String> collection) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        lines.stream().map(String::trim).filter(line -> line.length() > 0).forEach(collection::add);
    }

    /**
     * lädt die Daten aus einer Textdatei in die Collection
     *
     * @param fileName Dateiname
     * @param collection Collection
     */
    public static void load(URI fileName, Collection<String> collection) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        lines.stream().map(String::trim).filter(line -> line.length() > 0).forEach(collection::add);
    }
}
