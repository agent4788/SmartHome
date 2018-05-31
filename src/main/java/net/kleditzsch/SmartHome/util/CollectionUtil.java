package net.kleditzsch.SmartHome.util;

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
}
