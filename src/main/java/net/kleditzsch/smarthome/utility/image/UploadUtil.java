package net.kleditzsch.smarthome.utility.image;

import net.kleditzsch.smarthome.model.base.ID;

import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

/**
 * Hilfsklasse für Bilder
 */
public abstract class UploadUtil {

    /**
     * Liste mit den erlaubten Content typen
     */
    public static List<String> allowedImageContentTypes;

    /**
     * Map mit den Dateiendungen
     */
    public static Map<String, String> fileTypes;
    static {

        Map<String, String> tmp = new HashMap<>();
        tmp.put("image/jpeg", ".jpg");
        tmp.put("image/png", ".png");
        tmp.put("image/gif", ".gif");

        fileTypes = Collections.unmodifiableMap(tmp);
        allowedImageContentTypes = Collections.unmodifiableList(new ArrayList<>(tmp.keySet()));
    }

    /**
     * prüft und verschiebt eine hochgeladene Bild Datei
     *
     * @param part Datei
     * @param targetDirectory Zielordner
     * @return Zieldatei
     */
    public static Path handleUploadedImage(Part part, Path targetDirectory) throws IOException {

        return handleUploadedImage(part, targetDirectory, ID.create().get());
    }

    /**
     * prüft und verschiebt eine hochgeladene Bild Datei
     *
     * @param part Datei
     * @param targetDirectory Zielordner
     * @param filename Dateiname
     * @return Zieldatei
     */
    public static Path handleUploadedImage(Part part, Path targetDirectory, String filename) throws IOException {

        //Zielordner erstellen (falls nicht vorhanden)
        if(!Files.exists(targetDirectory)) {

            Files.createDirectories(targetDirectory);
        }

        //Dateiendung bestimmen
        String contentType = part.getContentType();
        if(fileTypes.containsKey(contentType)) {

            filename += fileTypes.get(contentType);
        } else {

            throw new IOException("Ungültiger Content Type: " + contentType);
        }

        Path targetFile = targetDirectory.resolve(filename);
        try (OutputStream outputStream = new FileOutputStream(targetFile.toFile())) {

            part.getInputStream().transferTo(outputStream);
        }

        return targetFile;
    }

    /**
     * lädt eine Bild Datei von einem HTTP Server herunter
     *
     * @param url Bild URL
     * @param targetDirectory Zielordner
     * @return Zieldatei
     */
    public static Path handleImageUrl(String url, Path targetDirectory) throws IOException, InterruptedException {

        return handleImageUrl(url, targetDirectory, ID.create().get());
    }

    /**
     * lädt eine Bild Datei von einem HTTP Server herunter
     *
     * @param url Bild URL
     * @param targetDirectory Zielordner
     * @param filename Dateiname
     * @return Zieldatei
     */
    public static Path handleImageUrl(String url, Path targetDirectory, String filename) throws IOException, InterruptedException {

        //Zielordner erstellen (falls nicht vorhanden)
        if(!Files.exists(targetDirectory)) {

            Files.createDirectories(targetDirectory);
        }
        Path tmpDir = Paths.get("upload/tmp");
        if(!Files.exists(tmpDir)) {

            Files.createDirectories(tmpDir);
        }

        //Datei aus dem Intenet in den Temp Ordner laden
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        Path tmpFile = tmpDir.resolve("tmpImageFile");
        HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(tmpFile);
        HttpResponse<Path> response = client.send(request, asFile);

        if(response.statusCode() == 200) {

            //Dateiendung bestimmen
            String contentType = response.headers().allValues("Content-Type").get(0);
            if(contentType != null) {

                if(fileTypes.containsKey(contentType)) {

                    filename += fileTypes.get(contentType);
                } else {

                    throw new IOException("Ungültiger Content Type: " + contentType);
                }

            } else {

                throw new IOException("Ungültiger Content Type");
            }

            //Datei in Zielordner verschieben
            Path targetFile = targetDirectory.resolve(filename);
            Files.move(tmpFile, targetFile);

            return targetFile;
        } else {

            throw new IOException("Download fehlgeschlagen, Status Code: " + response.statusCode());
        }
    }

    /**
     * prüft und verschiebt eine hochgeladene Bild Datei
     *
     * @param part Datei
     * @param targetDirectory Zielordner
     * @param allowedContentTypes Erlaubte Content Typen
     * @return Zieldatei
     */
    public static Path handleUploadedFile(Part part, Path targetDirectory, List<String> allowedContentTypes) throws IOException {

        //Zielordner erstellen (falls nicht vorhanden)
        if(!Files.exists(targetDirectory)) {

            Files.createDirectories(targetDirectory);
        }

        //Contenttype prüfen
        String contentType = part.getContentType();
        if(!allowedContentTypes.contains(contentType)) {

            throw new IOException("Ungültiger Content Type: " + contentType);
        }

        Path targetFile = targetDirectory.resolve(part.getSubmittedFileName());
        try (OutputStream outputStream = new FileOutputStream(targetFile.toFile())) {

            part.getInputStream().transferTo(outputStream);
        }

        return targetFile;
    }
}
