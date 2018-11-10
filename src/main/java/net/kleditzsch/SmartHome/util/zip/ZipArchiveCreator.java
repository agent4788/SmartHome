package net.kleditzsch.SmartHome.util.zip;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiveCreator implements AutoCloseable {

    /**
     * Pfad zum Archiv
     */
    private Path file;

    /**
     * Zip Stream
     */
    private ZipOutputStream zipOutputStream;

    /**
     * @param file Pfad zum Archiv
     */
    private ZipArchiveCreator(Path file) {

        this.file = file;
    }

    /**
     * erstellt einen neuen Zip Creator
     *
     * @param file Pfad zum Archiv
     * @return Zip Creator
     */
    public static ZipArchiveCreator create(Path file) throws FileNotFoundException {

        Objects.requireNonNull(file);
        if(!Files.exists(file)) {

            ZipArchiveCreator zipArchiveCreator = new ZipArchiveCreator(file);
            zipArchiveCreator.createArchive();
            return zipArchiveCreator;
        }
        throw new IllegalStateException("Die Datei \"" + file.toAbsolutePath().toString() + "\" existiert bereits");
    }

    /**
     * Initalisiert den Stream
     */
    private void createArchive() throws FileNotFoundException {

        zipOutputStream = new ZipOutputStream(new FileOutputStream(file.toAbsolutePath().toString()));
    }

    /**
     * schreibt eine Datei aus dem Dateisystem in die ZipDatei
     *
     * @param file Datei
     */
    public void addFile(Path file) throws IOException {

        Objects.requireNonNull(file);
        if(Files.exists(file) && Files.isRegularFile(file)) {

            try (FileInputStream fis = new FileInputStream(file.toAbsolutePath().toString())) {

                ZipEntry entry = new ZipEntry(file.getFileName().toString());
                zipOutputStream.putNextEntry(entry);
                fis.transferTo(zipOutputStream);
                return;
            }
        }
        throw new IllegalStateException("Die Datei \"" + file.toAbsolutePath().toString() + "\" existiert nicht");
    }

    /**
     * schreibt eine Datei aus dem Dateisystem in die ZipDatei
     *
     * @param file Datei
     * @param fileName Dateiname im Archiv
     */
    public void addFile(Path file, String fileName) throws IOException {

        Objects.requireNonNull(file);
        if(Files.exists(file) && Files.isRegularFile(file)) {

            try (FileInputStream fis = new FileInputStream(file.toAbsolutePath().toString())) {

                ZipEntry entry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(entry);
                fis.transferTo(zipOutputStream);
                return;
            }
        }
        throw new IllegalStateException("Die Datei \"" + file.toAbsolutePath().toString() + "\" existiert nicht");
    }

    /**
     * schreibt eine Datei aus dem Datenstrom in die ZipDatei
     *
     * @param fileName Dateiname im Archiv
     * @param is  Input Stream
     */
    public void addFile(String fileName, InputStream is) throws IOException {

        Objects.requireNonNull(fileName);
        Objects.requireNonNull(is);
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        is.transferTo(zipOutputStream);
    }

    /**
     * schreibt eine Datei mit der Zeichenkette in die ZipDatei
     *
     * @param fileName Dateiname im Archiv
     * @param data Daten
     */
    public void addFile(String fileName, String data) throws IOException {

        Objects.requireNonNull(fileName);
        Objects.requireNonNull(data);
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        byte[] bytes = data.getBytes();
        zipOutputStream.write(bytes, 0, bytes.length);
    }

    /**
     * schreibt eine Datei mit der Zeichenkette in die ZipDatei
     *
     * @param fileName Dateiname im Archiv
     * @param data Daten
     * @param charset  Zeichensatz
     */
    public void addFile(String fileName, String data, Charset charset) throws IOException {

        Objects.requireNonNull(fileName);
        Objects.requireNonNull(data);
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        byte[] bytes = data.getBytes(charset);
        zipOutputStream.write(bytes, 0, bytes.length);
    }

    /**
     * schreibt eine Datei mit den Daten in die ZipDatei
     *
     * @param fileName Dateiname im Archiv
     * @param bytes Daten
     */
    public void addFile(String fileName, byte[] bytes) throws IOException {

        Objects.requireNonNull(fileName);
        Objects.requireNonNull(bytes);
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(bytes, 0, bytes.length);
    }

    /**
     * schreibt eine Datei mit den Daten in die ZipDatei
     *
     * @param fileName Dateiname im Archiv
     */
    public OutputStream addFile(String fileName) throws IOException {

        Objects.requireNonNull(fileName);
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        return zipOutputStream;
    }

    /**
     * erstellt einen Ordner in der ZipDatei ZipDatei
     *
     * @param dirName Dateiname im Archiv
     */
    public void addEmptryDirectory(String dirName) throws IOException {

        Objects.requireNonNull(dirName);
        if(!dirName.endsWith("/")) {
            dirName = dirName + "/";
        }
        ZipEntry entry = new ZipEntry(dirName);
        zipOutputStream.putNextEntry(entry);
    }

    /**
     * fügt dem Archiv alle Elemente eines Ordner hinzu
     *
     * @param directory Ordner
     */
    public void addDirectory(Path directory) throws IOException {

        addDirectory(directory, "/", false, false);
    }

    /**
     * fügt dem Archiv alle Elemente eines Ordner hinzu
     *
     * @param directory Ordner
     * @param archiveDir Pfad im Archiv
     */
    public void addDirectory(Path directory, String archiveDir) throws IOException {

        addDirectory(directory, archiveDir, false, false);
    }

    /**
     * fügt dem Archiv alle Elemente eines Ordner hinzu
     *
     * @param directory Ordner
     * @param archiveDir Pfad im Archiv
     * @param recursive Unterordner mit übernehmen
     */
    public void addDirectory(Path directory, String archiveDir, boolean recursive) throws IOException {

        addDirectory(directory, archiveDir, recursive, false);
    }

    /**
     * fügt dem Archiv alle Elemente eines Ordner hinzu
     *
     * @param directory Ordner
     * @param archiveDir Pfad im Archiv
     * @param recursive Unterordner mit übernehmen
     * @param ignoreHidden Versteckte Dateien ignorieren
     */
    public void addDirectory(Path directory, String archiveDir, boolean recursive, boolean ignoreHidden) throws IOException {

        Objects.requireNonNull(directory);
        Objects.requireNonNull(archiveDir);

        if(!archiveDir.endsWith("/")) {

            archiveDir = archiveDir + "/";
        }

        if(Files.exists(directory)) {

            if(Files.isDirectory(directory)) {

                //Ordner
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {

                    for (Path element : ds) {

                        if(Files.isDirectory(element) && recursive && (!ignoreHidden || !Files.isHidden(element))) {

                            //Ordner
                            addEmptryDirectory(archiveDir + element.getFileName().toString() + "/");
                            addDirectory(element, archiveDir + element.getFileName().toString() + "/", recursive, ignoreHidden);
                        } else if(!Files.isDirectory(element)) {

                            //Datei
                            if(!ignoreHidden || !Files.isHidden(element)) {

                                addFile(element, archiveDir + element.getFileName());
                            }
                        }
                    }
                }
            } else {

                //Datei
                if(!ignoreHidden || !Files.isHidden(directory)) {

                    addFile(directory, archiveDir + directory.getFileName());
                }
            }
        }
    }

    /**
     * schliest den Zip Stream
     */
    @Override
    public void close() throws Exception {

        if(zipOutputStream != null) {

            zipOutputStream.close();
        }
    }
}
