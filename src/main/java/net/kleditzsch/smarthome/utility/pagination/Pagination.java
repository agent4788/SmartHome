package net.kleditzsch.smarthome.utility.pagination;

/**
 * Berechnet die Daten für eine Blätterfunktion
 */
public class Pagination {

    /**
     * Anzahl der Elemente
     */
    private long size = 0;

    /**
     * Anzahl der Elemente Pro Seite
     */
    private long elementsAtPage = 10;

    /**
     * aktueller Index
     */
    private long currentIndex = 0;

    /**
     * Link
     */
    private String baseLink = "";

    /**
     * @param size Anzahl der Elemente
     */
    public Pagination(long size) {
        this.size = size;
    }

    /**
     * @param size Anzahl der Elemente
     * @param elementsAtPage Anzahl der Elemente Pro Seite
     */
    public Pagination(long size, long elementsAtPage) {
        this.size = size;
        this.elementsAtPage = elementsAtPage;
    }

    /**
     * @param size Anzahl der Elemente
     * @param elementsAtPage Anzahl der Elemente Pro Seite
     * @param currentIndex aktueller Index
     */
    public Pagination(long size, long elementsAtPage, long currentIndex) {
        this.size = size;
        this.elementsAtPage = elementsAtPage;
        this.currentIndex = currentIndex;
    }

    /**
     * gibt die Anzahl der Elemente zurück
     *
     * @return Anzahl der Elemente
     */
    public long getSize() {
        return size;
    }

    /**
     * setzt die Anzahl der Elemente
     *
     * @param size Anzahl der Elemente
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * gibt die Anzahl der Elemente Pro Seite zurück
     *
     * @return Anzahl der Elemente Pro Seite
     */
    public long getElementsAtPage() {
        return elementsAtPage;
    }

    /**
     * setzt Anzahl der Elemente Pro Seite
     *
     * @param elementsAtPage Anzahl der Elemente Pro Seite
     */
    public void setElementsAtPage(long elementsAtPage) {
        this.elementsAtPage = elementsAtPage;
    }

    /**
     * gibt den Basislink zurück
     *
     * @return Basislink
     */
    public String getBaseLink() {
        return baseLink;
    }

    /**
     * setzt den Basislink
     *
     * @param baseLink Basislink
     */
    public void setBaseLink(String baseLink) {
        this.baseLink = baseLink;
    }

    /**
     * gibt den Index des aktuellen Elementes zurück
     *
     * @return Index des aktuellen Elementes
     */
    public long getCurrentIndex() {
        return currentIndex;
    }

    /**
     * setzt den Index des aktuellen Elementes
     *
     * @param currentIndex Index des aktuellen Elementes
     */
    public void setCurrentIndex(long currentIndex) {
        this.currentIndex = currentIndex;
    }

    /**
     * prüft ob genügend Elemente für die Blätterfunktion zur verfügung stehen
     *
     * @return Blätterfunktion verfügbar
     */
    public boolean hasPages() {

        return size > elementsAtPage;
    }

    /**
     * gibt den Index des ersten Elements der ersten Seite zurück
     *
     * @return Index des ersten Elements der ersten Seite
     */
    public long getFirstPageIndex() {

        return 0;
    }

    /**
     * gibt das Label der ersten Seite zurück
     *
     * @return Label der ersten Seite
     */
    public long getFirstPageLabel() {

        return 1;
    }

    /**
     * prüft ob es eine 2. vorherige Seite gibt
     *
     * @return 2. vorherige Seite vorhanden
     */
    public boolean hasSecondPreviousPage() {

        return (getCurrentPageIndex() - (2 * elementsAtPage)) >= getFirstPageIndex();
    }

    /**
     * gibt den Index des ersten Elements der 2. vorherigen Seite zurück
     *
     * @return Index des ersten Elements der 2. verherigen Seite
     */
    public long getSecondPreviousPageIndex() {

        return (getCurrentPageIndex() - (2 * elementsAtPage));
    }

    /**
     * gibt das Label der 2. vorherigen Seite zurück
     *
     * @return Label der 2. vorherigen Seite
     */
    public long getSecondPreviousPageLabel() {

        return Math.round(Math.ceil(getSecondPreviousPageIndex() / elementsAtPage)) + 1;
    }

    /**
     * prüft ob es eine vorherige Seite gibt
     *
     * @return vorherige Seite vorhanden
     */
    public boolean hasPreviousPage() {

        return (getCurrentPageIndex() - elementsAtPage) >= getFirstPageIndex();
    }

    /**
     * gibt den Index des ersten Elements der vorherigen Seite zurück
     *
     * @return Index des ersten Elements der vorherigen Seite
     */
    public long getPreviousPageIndex() {

        return (getCurrentPageIndex() - elementsAtPage);
    }

    /**
     * gibt das Label der vorherigen Seite zurück
     *
     * @return Label der vorherigen Seite
     */
    public long getPreviousPageLabel() {

        return Math.round(Math.ceil(getPreviousPageIndex() / elementsAtPage)) + 1;
    }

    /**
     * prüft ob die aktuelle Seite gleich der ersten Seite ist
     *
     * @return aktuelle Seite gleich der ersten Seite
     */
    public boolean isCurrentPageEqualFirstPage() {

        return getCurrentPageIndex() == 0;
    }

    /**
     * gibt den Index des ersten Elements der aktuellen Seite zurück
     *
     * @return Index des ersten Elements der aktuellen Seite
     */
    public long getCurrentPageIndex() {

        if(currentIndex % elementsAtPage != 0) {

            return currentIndex - (currentIndex % elementsAtPage);
        }
        return currentIndex;
    }

    /**
     * gibt das Label der aktuellen Seite zurück
     *
     * @return Label der aktuellen Seite
     */
    public long getCurrentPageLabel() {

        return Math.round(Math.ceil(getCurrentPageIndex() / elementsAtPage)) + 1;
    }

    /**
     * prüft ob es eine nächste Seite gibt
     *
     * @return nächste Seite vorhanden
     */
    public boolean hasNextPage() {

        return (getCurrentPageIndex() + elementsAtPage) <= getLastPageIndex();
    }

    /**
     * gibt den Index des ersten Elements der nächsten Seite zurück
     *
     * @return Index des ersten Elements der nächsten Seite
     */
    public long getNextPageIndex() {

        return (getCurrentPageIndex() + elementsAtPage);
    }

    /**
     * gibt das Label der nächsten Seite zurück
     *
     * @return Label der nächsten Seite
     */
    public long getNextPageLabel() {

        return Math.round(Math.ceil(getNextPageIndex() / elementsAtPage)) + 1;
    }

    /**
     * prüft ob es eine 2. nächste Seite gibt
     *
     * @return 2. nächste Seite vorhanden
     */
    public boolean hasSecondNextPage() {

        return (getCurrentPageIndex() + (2 * elementsAtPage)) <= getLastPageIndex();
    }

    /**
     * gibt den Index des ersten Elements der 2. nächsten Seite zurück
     *
     * @return Index des ersten Elements der 2. nächsten Seite
     */
    public long getSecondNextPageIndex() {

        return (getCurrentPageIndex() + (2 * elementsAtPage));
    }

    /**
     * gibt das Label der 2. nächsten Seite zurück
     *
     * @return Label der 2. nächsten Seite
     */
    public long getSecondNextPageLabel() {

        return Math.round(Math.ceil(getSecondNextPageIndex() / elementsAtPage)) + 1;
    }

    /**
     * prüft ob die aktuelle Seite gleich der letzten Seite ist
     *
     * @return aktuelle Seite gleich der letzten Seite
     */
    public boolean isCurrentPageEqualLastPage() {

        return getCurrentPageIndex() == getLastPageIndex();
    }

    /**
     * gibt den Index des ersten Elements der letzten Seite zurück
     *
     * @return Index des ersten Elements der letzen Seite
     */
    public long getLastPageIndex() {

        long lastPageIndex = Math.round(Math.ceil(size / elementsAtPage) * elementsAtPage);
        if(lastPageIndex >= size) {

            lastPageIndex -= elementsAtPage;
        }
        return lastPageIndex;
    }

    /**
     * gibt das Label der letzten Seite zurück
     *
     * @return Label der letzten Seite
     */
    public long getLastPageLabel() {

        long lastPageIndex = Math.round(Math.ceil(size / elementsAtPage) * elementsAtPage);
        long lastPageLabel = Math.round(Math.ceil(size / elementsAtPage)) + 1;
        if(lastPageIndex >= size) {

            lastPageLabel -= 1;
        }
        return lastPageLabel;
    }
}
