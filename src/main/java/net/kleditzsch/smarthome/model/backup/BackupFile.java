package net.kleditzsch.smarthome.model.backup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Backup Datei
 */
public class BackupFile {

    /**
     * Datumsformat
     */
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    /**
     * Backup Datei
     */
    private Path file;

    /**
     * Hash
     */
    private String hash = null;

    /**
     * @param file Backup Datei
     */
    public BackupFile(Path file) {

        Objects.requireNonNull(file);
        this.file = file;
    }

    /**
     * gibt den Dateipfad zurück
     *
     * @return Dateipfad
     */
    public Path getPath() {

        return file;
    }

    /**
     * gibt den Dateinamen der Backup Datei zurück
     *
     * @return Dateiname
     */
    public String getFileName() {

        return getPath().getFileName().toString();
    }

    /**
     * gibt die Erstellungszeit der Backupdatei zurück
     *
     * @return Datumsobjekt
     */
    public LocalDateTime getCreationDateTime() {

        String filename = file.getFileName().toString();
        return LocalDateTime.parse(filename.substring(0, 19), formatter);
    }

    /**
     * gibt die Dateigröße der Backup Datei zurück
     *
     * @return Dateigröße in Bytes
     */
    public long getFileSize() throws IOException {

        return Files.size(getPath());
    }

    /**
     * gibt einen eindeutigen Hash für die Datei zurück
     *
     * @return Hash
     */
    public String getHash() throws NoSuchAlgorithmException {

        if(hash == null) {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(getPath().toAbsolutePath().toString().getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        }
        return hash;
    }
}
