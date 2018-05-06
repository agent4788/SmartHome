package net.kleditzsch.SmartHome.util.pagination;

import java.util.List;

/**
 * Listenbezogene Blätterfunktion
 *
 * @param <Element>
 */
public class ListPagination<Element> extends Pagination {

    /**
     * Daten Liste
     */
    private List<Element> data;

    /**
     * @param data Daten Liste
     */
    public ListPagination(List<Element> data) {

        super(data.size());
        this.data = data;
    }

    /**
     * @param data Daten Liste
     * @param elementsAtPage Anzahl der Elemente Pro Seite
     */
    public ListPagination(List<Element> data, long elementsAtPage) {
        super(data.size(), elementsAtPage);
        this.data = data;
    }

    /**
     * @param data Daten Liste
     * @param elementsAtPage Anzahl der Elemente Pro Seite
     * @param currentIndex aktueller Index
     */
    public ListPagination(List<Element> data, long elementsAtPage, long currentIndex) {
        super(data.size(), elementsAtPage, currentIndex);
        this.data = data;
    }

    /**
     * gibt die Elemente der ersten Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getFirstPageEmelents() {

        return data.subList((int) getFirstPageIndex(), Math.min((int) (getFirstPageIndex() + getElementsAtPage()), data.size()));
    }

    /**
     * gibt die Elemente der 2. vorherigen Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getSecondPreviousPageEmelents() {

        return data.subList((int) getSecondPreviousPageIndex(), Math.min((int) (getSecondPreviousPageIndex() + getElementsAtPage()), data.size()));
    }


    /**
     * gibt die Elemente der vorheriugen Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getPreviousPageEmelents() {

        return data.subList((int) getPreviousPageIndex(), Math.min((int) (getPreviousPageIndex() + getElementsAtPage()), data.size()));
    }


    /**
     * gibt die Elemente der aktuellen Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getCurrentPageElements() {

        return data.subList((int) getCurrentPageIndex(), Math.min((int) (getCurrentPageIndex() + getElementsAtPage()), data.size()));
    }

    /**
     * gibt die Elemente der nächsten Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getNextPageElements() {

        return data.subList((int) getNextPageIndex(), Math.min((int) (getNextPageIndex() + getElementsAtPage()), data.size()));
    }

    /**
     * gibt die Elemente der 2. nächsten Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getSecondNextPageElements() {

        return data.subList((int) getSecondNextPageIndex(), Math.min((int) (getSecondNextPageIndex() + getElementsAtPage()), data.size()));
    }

    /**
     * gibt die Elemente der letzten Seite zurück
     *
     * @return Subliste
     */
    public List<Element> getLastPageEmelents() {

        return data.subList((int) getLastPageIndex(), data.size());
    }
}
