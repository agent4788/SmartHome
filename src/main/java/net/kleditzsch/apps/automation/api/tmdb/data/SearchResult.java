package net.kleditzsch.apps.automation.api.tmdb.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Suchergebnis
 */
public class SearchResult {

    /**
     * aktuelle Seite
     */
    private int page = 1;

    /**
     * Anzahl der Seiten
     */
    private int pages = 1;

    /**
     * Anzahl der Funde
     */
    private int resultCount = 0;

    /**
     * Liste der Filme der aktuellen Seite
     */
    private List<MovieInfo> results = new ArrayList<>();

    /**
     * gibt die aktuelle Seite zurück
     *
     * @return Seitenzahl
     */
    public int getPage() {
        return page;
    }

    /**
     * setzt die aktuelle Seite
     *
     * @param page Seitenzahl
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * gibt die Anzahl der Seiten zurück
     *
     * @return Anzahl der Seiten
     */
    public int getPages() {
        return pages;
    }

    /**
     * setzt die Anzahl der Seiten
     *
     * @param pages Anzahl der Seiten
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * gibt die Anzahl der gefundenen Filme zurück
     *
     * @return Anzahl der gefundenen Filme
     */
    public int getResultCount() {
        return resultCount;
    }

    /**
     * setzt die Anzahl der gefundenen Filme
     *
     * @param resultCount Anzahl der gefundenen Filme
     */
    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * gibt die Liste mit den Ergebnissen zurück
     *
     * @return Liste mit den Ergebnissen
     */
    public List<MovieInfo> getResults() {
        return results;
    }
}
