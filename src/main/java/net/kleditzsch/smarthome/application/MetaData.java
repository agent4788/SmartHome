package net.kleditzsch.smarthome.application;

/**
 * Datenobjekt mit Metainformationen zu den Apps
 */
public class MetaData {

    /**
     * Anzeige Name der Anwendung
     */
    private String displayName;

    /**
     * Pfad im Webinterface
     */
    private String webContextPath;

    /**
     * Pfad der Index Seite
     */
    private String webStartUrl;

    /**
     * Name der Icon Datei
     */
    private String iconName;

    /**
     * Sortierung
     */
    private int orderId;

    /**
     * @param displayName Anzeige Name
     * @param webContextPath Pfad im Webinterface
     * @param webStartUrl Pfad der Index Seite
     * @param iconName Name der Icon Datei
     * @param orderId Sortierungs ID der Anwendung
     */
    public MetaData(String displayName, String webContextPath, String webStartUrl, String iconName, int orderId) {
        this.displayName = displayName;
        this.webContextPath = webContextPath;
        this.webStartUrl = webStartUrl;
        this.iconName = iconName;
        this.orderId = orderId;
    }

    /**
     * gibt den Anzeige Namen der Anwendung zurück
     *
     * @return Anzeige Name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * gibt den Context Pfad der Anwendung zurück
     *
     * @return Contect Pfad
     */
    public String getWebContextPath() {
        return webContextPath;
    }

    /**
     * gibt den Pfad der Index Seite zurück
     *
     * @return Pfad der Index Seite
     */
    public String getWebStartUrl() {
        return webStartUrl;
    }

    /**
     * gibt den Namen der Icon Datei zurück
     *
     * @return Name der Icon Datei
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * gibt die Sortierungs ID der Anwendung zurück
     *
     * @return Sortierungs ID der Anwendung
     */
    public int getOrderId() {
        return orderId;
    }
}
