package net.kleditzsch.SmartHome.controller.backup;

import java.nio.file.Files;
import java.nio.file.Path;

public class BackupConfiguration {

    /**
     * Zielordner
     */
    private Path destinationDirectory;

    /**
     * Konfiguration welche Daten gesichert werden sollen
     */
    private boolean backupGlobalData = false;
    private boolean backupAutomationData = false;
    private boolean backupContactData = false;
    private boolean backupContractData = false;
    private boolean backupMovieData = false;
    private boolean backupNetworkData = false;
    private boolean backupRecipeData = false;
    private boolean backupShoppingListData = false;

    /**
     * gibt an ob es ein automatisch erstelltes Backup ist
     */
    private boolean isAutoBackup = false;

    /**
     * @param destinationDirectory Zielordner
     */
    private  BackupConfiguration(Path destinationDirectory) {

        this.destinationDirectory = destinationDirectory;
    };

    /**
     * erstellt ein neues Kofigurationsobjekt
     *
     * @param destDir Zielordner
     */
    public static BackupConfiguration create(Path destDir) {

        if(Files.isDirectory(destDir) && Files.isWritable(destDir)) {

            return new BackupConfiguration(destDir);
        } else {

            throw new IllegalStateException("Der Ordner \"" + destDir.toAbsolutePath() + "\" ist nicht vorhanden oder Schreibgeschützt");
        }
    }

    /**
     * gibt den Zielpfad des Backups zurück
     *
     * @return Zielpfad des Backups
     */
    public Path getDestinationDirectory() {
        return destinationDirectory;
    }

    /**
     * gibt an ob das BackupCreator der Globalen Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupGlobalDataEnabled() {
        return backupGlobalData;
    }

    /**
     * aktiviert das BackupCreator der Globalen Daten
     *
     * @param backupGlobalData aktiviert
     */
    public void setBackupGlobalDataEnabled(boolean backupGlobalData) {
        this.backupGlobalData = backupGlobalData;
    }

    /**
     * gibt an ob das BackupCreator der Anutmaiton Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupAutomationDataEnabled() {
        return backupAutomationData;
    }

    /**
     * aktiviert das BackupCreator der Anutmaiton Daten
     *
     * @param backupAutomationData aktiviert
     */
    public void setBackupAutomationDataEnabled(boolean backupAutomationData) {
        this.backupAutomationData = backupAutomationData;
    }

    /**
     * gibt an ob das BackupCreator der Kontakte Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupContactDataEnabled() {
        return backupContactData;
    }

    /**
     * aktiviert das BackupCreator der Kontakte Daten
     *
     * @param backupContactData aktiviert
     */
    public void setBackupContactDataEnabled(boolean backupContactData) {
        this.backupContactData = backupContactData;
    }

    /**
     * gibt an ob das BackupCreator der Verträge Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupContractDataEnabled() {
        return backupContractData;
    }

    /**
     * aktiviert das BackupCreator der Verträge Daten
     *
     * @param backupContractData aktiviert
     */
    public void setBackupContractDataEnabled(boolean backupContractData) {
        this.backupContractData = backupContractData;
    }

    /**
     * gibt an ob das BackupCreator der Film Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupMovieDataEnabled() {
        return backupMovieData;
    }

    /**
     * aktiviert das BackupCreator der Film Daten
     *
     * @param backupMovieData aktiviert
     */
    public void setBackupMovieDataEnabled(boolean backupMovieData) {
        this.backupMovieData = backupMovieData;
    }

    /**
     * gibt an ob das BackupCreator der Netzwerk Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupNetworkDataEnabled() {
        return backupNetworkData;
    }

    /**
     * aktiviert das BackupCreator der Netzwerk Daten
     *
     * @param backupNetworkData aktiviert
     */
    public void setBackupNetworkDataEnabled(boolean backupNetworkData) {
        this.backupNetworkData = backupNetworkData;
    }

    /**
     * gibt an ob das BackupCreator der Rezepte Daten aktiviert ist
     *
     * @return aktiviert
     */
    public boolean isBackupRecipeDataEnabled() {
        return backupRecipeData;
    }

    /**
     * aktiviert das BackupCreator der Rezepte Daten
     *
     * @param backupRecipeData aktiviert
     */
    public void setBackupRecipeDataEnabled(boolean backupRecipeData) {
        this.backupRecipeData = backupRecipeData;
    }

    public boolean isBackupShoppingListDataEnabled() {
        /**
         * gibt an ob das BackupCreator der Eingaufsliste Daten aktiviert ist
         *
         * @return aktiviert
         */
        return backupShoppingListData;
    }

    /**
     * aktiviert das BackupCreator der Eingaufsliste Daten
     *
     * @param backupShoppingListData aktiviert
     */
    public void setBackupShoppingListDataEnabled(boolean backupShoppingListData) {
        this.backupShoppingListData = backupShoppingListData;
    }

    /**
     * gibt an ob das Backup Automatisch erstellt wird
     *
     * @return true = automatisch
     */
    public boolean isAutoBackup() {
        return isAutoBackup;
    }

    /**
     * setzt das Backup als Auto Backup
     *
     * @param autoBackup Auto Backup aktiviert
     */
    public void setAutoBackup(boolean autoBackup) {
        isAutoBackup = autoBackup;
    }
}
