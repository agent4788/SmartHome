package net.kleditzsch.SmartHome.controller.global.backup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.DatabaseManager;
import net.kleditzsch.SmartHome.global.database.exception.DatabaseException;
import net.kleditzsch.SmartHome.model.global.backup.BackupFile;
import net.kleditzsch.SmartHome.model.global.editor.BackupEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieSeriesEditor;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * stellt die Daten einer Sicherung wieder her
 */
public class BackupRestore {

    private static Logger logger = LoggerUtil.getLogger(BackupRestore.class);

    /**
     * Stellt die Backup Daten wieder her
     *
     * @param module Modul
     * @param backupFile Backup Datei
     * @return Liste mit Fehlermeldungen
     */
    public static List<String> restoreModule(BackupEditor.Module module, BackupFile backupFile) throws IOException, DatabaseException {

        switch (module) {

            case Global:

                return restoreGlobalData(backupFile);
            case Automation:

                return restoreAutomationData(backupFile);
            case Contact:

                //TODO implimentieren
            case Contract:

                //TODO implimentieren
            case Movie:

                return restoreMovieData(backupFile);
            case Network:

                return restoreNetworkData(backupFile);
            case Recipe:

                //TODO implimentieren
            case ShoppingList:

                //TODO implimentieren
        }
        List<String> messages = new ArrayList<>();
        messages.add("Fehlerhaftes Modul!");
        return messages;
    }

    /**
     * Globale Daten wiederherstellen
     *
     * @param backupFile Backup Datei
     * @return Liste mit Fehlermeldungen
     */
    private static List<String> restoreGlobalData(BackupFile backupFile) throws IOException, DatabaseException {

        DatabaseManager dbm = connectDatabase();
        MongoDatabase db = dbm.getDatabase();

        List<String> messages = new ArrayList<>();
        if(Files.exists(backupFile.getPath()) && backupFile.getFileName().contains("global")) {

            try (ZipInputStream zip = new ZipInputStream(new FileInputStream(backupFile.getPath().toFile()))) {

                Pattern pattern = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+\\.json$");
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {

                    //Collections auslesen
                    String fileName = entry.getName();
                    if(!entry.isDirectory() && pattern.matcher(fileName).find()) {

                        String collectionName = fileName.substring(0, fileName.length() - 5);

                        //alte Collection löschen
                        db.getCollection(collectionName).drop();

                        //Meldungs Collection als Chapped Collection anlegen
                        if(collectionName.equalsIgnoreCase(MessageEditor.COLLECTION)) {

                            dbm.getDatabase().createCollection(MessageEditor.COLLECTION, new CreateCollectionOptions().capped(true).maxDocuments(1000).sizeInBytes(5 * 1024 * 1024));
                        }

                        //Collection wiederherstellen
                        logger.info("Collection \"" + collectionName + "\" wird wiederhergestellt");
                        restoreCollection(db, collectionName, zip);
                        logger.info("Collection \"" + collectionName + "\" wurde wiederhergestellt");
                    } else {

                        messages.add("ungültige Modul Datei \"" + entry.getName() + "\"");
                    }

                    //nächstes Element
                    entry = zip.getNextEntry();
                }
            }
        } else {

            messages.add("ungültige Modul Datei");
        }
        dbm.disconnectDatabase();
        return messages;
    }

    /**
     * Automatisierungs Daten wiederherstellen
     *
     * @param backupFile Backup Datei
     * @return Liste mit Fehlermeldungen
     */
    private static List<String> restoreAutomationData(BackupFile backupFile) throws IOException, DatabaseException {

        DatabaseManager dbm = connectDatabase();
        MongoDatabase db = dbm.getDatabase();

        List<String> messages = new ArrayList<>();
        if(Files.exists(backupFile.getPath()) && backupFile.getFileName().contains("_automation")) {

            try (ZipInputStream zip = new ZipInputStream(new FileInputStream(backupFile.getPath().toFile()))) {

                Pattern pattern = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+\\.json$");
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {

                    //Collections auslesen
                    String fileName = entry.getName();
                    if(!entry.isDirectory() && pattern.matcher(fileName).find()) {

                        String collectionName = fileName.substring(0, fileName.length() - 5);

                        //alte Collection löschen
                        db.getCollection(collectionName).drop();

                        //Collection wiederherstellen
                        logger.info("Collection \"" + collectionName + "\" wird wiederhergestellt");
                        restoreCollection(db, collectionName, zip);
                        logger.info("Collection \"" + collectionName + "\" wurde wiederhergestellt");
                    } else {

                        messages.add("ungültige Modul Datei \"" + entry.getName() + "\"");
                    }

                    //nächstes Element
                    entry = zip.getNextEntry();
                }
            }
        } else {

            messages.add("ungültige Modul Datei");
        }
        dbm.disconnectDatabase();
        return messages;
    }

    /**
     * Filme Daten wiederherstellen
     *
     * @param backupFile Backup Datei
     * @return Liste mit Fehlermeldungen
     */
    private static List<String> restoreMovieData(BackupFile backupFile) throws IOException, DatabaseException {

        DatabaseManager dbm = connectDatabase();
        MongoDatabase db = dbm.getDatabase();

        //Text Index wiederherstellen
        db.createCollection(MovieEditor.COLLECTION);
        MongoCollection movies = db.getCollection(MovieEditor.COLLECTION);
        movies.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));
        db.createCollection(MovieBoxEditor.COLLECTION);
        MongoCollection movieBoxes = db.getCollection(MovieBoxEditor.COLLECTION);
        movieBoxes.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));
        db.createCollection(MovieSeriesEditor.COLLECTION);
        MongoCollection movieSeries = db.getCollection(MovieSeriesEditor.COLLECTION);
        movieSeries.createIndex(Indexes.text("search"), new IndexOptions().defaultLanguage("german"));

        List<String> messages = new ArrayList<>();
        if(Files.exists(backupFile.getPath()) && backupFile.getFileName().contains("movie")) {

            try (ZipInputStream zip = new ZipInputStream(new FileInputStream(backupFile.getPath().toFile()))) {

                Path uploadCover = Paths.get("upload/cover");
                Path uploadFsk = Paths.get("upload/fskLogo");

                //Alle Dateien aus dem Cover Ordner löschen (außer .gitignore)
                DirectoryStream<Path> cover = Files.newDirectoryStream(uploadCover);
                cover.forEach(file -> {

                    if(!file.getFileName().toString().equals(".gitignore") && Files.exists(file)) {

                        try {

                            Files.delete(file);
                        } catch (IOException e) {

                            messages.add("Die Cover Datei \"" + file.getFileName() + "\" konnte nicht gelöscht werden");
                        }
                    }
                });

                //Alle Dateien aus dem FskLogo Ordner löschen (außer .gitignore)
                DirectoryStream<Path> fsk = Files.newDirectoryStream(uploadFsk);
                fsk.forEach(file -> {

                    if(!file.getFileName().toString().equals(".gitignore") && Files.exists(file)) {

                        try {

                            Files.delete(file);
                        } catch (IOException e) {

                            messages.add("Die Cover Datei \"" + file.getFileName() + "\" konnte nicht gelöscht werden");
                        }
                    }
                });

                Pattern pattern = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+\\.json$");
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {

                    //Collections auslesen
                    String fileName = entry.getName();
                    if(!entry.isDirectory() && pattern.matcher(fileName).find()) {

                        String collectionName = fileName.substring(0, fileName.length() - 5);

                        //alte Collection löschen
                        db.getCollection(collectionName).drop();

                        //Collection wiederherstellen
                        logger.info("Collection \"" + collectionName + "\" wird wiederhergestellt");
                        restoreCollection(db, collectionName, zip);
                        logger.info("Collection \"" + collectionName + "\" wurde wiederhergestellt");
                    } else if(entry.getName().startsWith("/upload/cover/")) {

                        //Cover Dateien wiederherstellen
                        dumpFile(uploadCover.resolve(entry.getName().replace("/upload/cover/", "")), zip);
                        logger.info("Cover: " + entry.getName() + "\" wurde wiederhergestellt");
                    } else if(entry.getName().startsWith("/upload/fskLogo/")) {

                        //FSK Logo Dateien wiederherstellen
                        dumpFile(uploadFsk.resolve(entry.getName().replace("/upload/fskLogo/", "")), zip);
                        logger.info("FSK Logo: " + entry.getName() + "\" wurde wiederhergestellt");

                    } else {

                        messages.add("ungültige Modul Datei \"" + entry.getName() + "\"");
                    }

                    //nächstes Element
                    entry = zip.getNextEntry();
                }
            }
        } else {

            messages.add("ungültige Modul Datei");
        }
        dbm.disconnectDatabase();
        return messages;
    }

    /**
     * Globale Daten wiederherstellen
     *
     * @param backupFile Backup Datei
     * @return Liste mit Fehlermeldungen
     */
    private static List<String> restoreNetworkData(BackupFile backupFile) throws IOException, DatabaseException {

        DatabaseManager dbm = connectDatabase();
        MongoDatabase db = dbm.getDatabase();

        List<String> messages = new ArrayList<>();
        if(Files.exists(backupFile.getPath()) && backupFile.getFileName().contains("network")) {

            try (ZipInputStream zip = new ZipInputStream(new FileInputStream(backupFile.getPath().toFile()))) {

                Pattern pattern = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+\\.json$");
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {

                    //Collections auslesen
                    String fileName = entry.getName();
                    if(!entry.isDirectory() && pattern.matcher(fileName).find()) {

                        String collectionName = fileName.substring(0, fileName.length() - 5);

                        //alte Collection löschen
                        db.getCollection(collectionName).drop();

                        //Collection wiederherstellen
                        logger.info("Collection \"" + collectionName + "\" wird wiederhergestellt");
                        restoreCollection(db, collectionName, zip);
                        logger.info("Collection \"" + collectionName + "\" wurde wiederhergestellt");
                    } else {

                        messages.add("ungültige Modul Datei \"" + entry.getName() + "\"");
                    }

                    //nächstes Element
                    entry = zip.getNextEntry();
                }
            }
        } else {

            messages.add("ungültige Modul Datei");
        }
        dbm.disconnectDatabase();
        return messages;
    }

    /**
     * stellt die Datenbankverbindung her
     *
     * @return Datenbankmanager
     */
    private static DatabaseManager connectDatabase() throws DatabaseException {

        DatabaseManager dbm = new DatabaseManager();
        dbm.readDatabaseConfigFile();
        dbm.connectDatabase();
        return dbm;
    }

    /**
     * Daten der Json Datei in der Datenbank wiederherstellen
     *
     * @param db Datenbankverbindung
     * @param collectionName Collection Name
     * @param zip Zip Datenstrom
     */
    private static void restoreCollection(MongoDatabase db, String collectionName, ZipInputStream zip) throws IOException {

        MongoCollection collection = db.getCollection(collectionName);

        JsonReader reader = new JsonReader(new InputStreamReader(zip, StandardCharsets.UTF_8));
        reader.beginArray();
        while (true) {

            JsonToken token = reader.peek();
            switch (token) {

                case BEGIN_OBJECT:

                    reader.beginObject();
                    Document doc = readJsonObjectToDocument(reader);
                    collection.insertOne(doc);
                    break;
                case END_ARRAY:

                    reader.endArray();
                    break;
                case END_DOCUMENT:

                    return;
            }
        }
    }

    /**
     * Daten aus der Json Datei in Dokumente umwandeln
     *
     * @param reader Json Reader
     * @return BSON Dokument
     */
    private static Document readJsonObjectToDocument(JsonReader reader) throws IOException {

        Document doc = new Document();

        String prefixId = "id://";
        String prefixLong = "long://";
        String prefixString = "string://";
        String prefixDate = "date://";
        String prefixNull = "null://";
        int prefixIdLength = prefixId.length();
        int prefixLongLength = prefixLong.length();
        int prefixStringLength = prefixString.length();
        int prefixDateLength = prefixDate.length();

        String nextName = null;
        while (true) {

            JsonToken token = reader.peek();
            switch (token) {

                case BEGIN_ARRAY:

                    reader.beginArray();
                    JsonToken nextToken =reader.peek();
                    if(nextToken == JsonToken.BEGIN_OBJECT) {

                        //Array aus Objekten
                        List<Document> docs = new ArrayList<>();
                        do {

                            reader.beginObject();
                            Document subDoc = readJsonObjectToDocument(reader);
                            docs.add(subDoc);
                            nextToken = reader.peek();
                        } while (nextToken == JsonToken.BEGIN_OBJECT);
                        doc.append(nextName, docs);
                    } else if(nextToken == JsonToken.STRING) {

                        //Array aus Strings
                        String str = reader.nextString();
                        if(str.startsWith(prefixId)) {

                            //BSON Object ID
                            List<ObjectId> list = new ArrayList<>();
                            list.add(new ObjectId(str.substring(prefixIdLength)));
                            while (reader.peek() == JsonToken.STRING) {

                                list.add(new ObjectId(str.substring(prefixIdLength)));
                            }
                            doc.append(nextName, list);
                        } else if(str.startsWith(prefixLong)) {

                            //Long
                            try {

                                List<Long> list = new ArrayList<>();
                                list.add(Long.parseLong(str.substring(prefixLongLength)));
                                while (reader.peek() == JsonToken.STRING) {

                                    list.add(Long.parseLong(str.substring(prefixLongLength)));
                                }
                                doc.append(nextName, list);
                            } catch (NumberFormatException e) {}
                        } else if(str.startsWith(prefixString)) {

                            //String
                            List<String> list = new ArrayList<>();
                            list.add(str.substring(prefixStringLength));
                            while (reader.peek() == JsonToken.STRING) {

                                list.add(str.substring(prefixStringLength));
                            }
                            doc.append(nextName, list);
                        } else if(str.startsWith(prefixDate)) {

                            //Datum
                            List<LocalDateTime> list = new ArrayList<>();
                            list.add(DatabaseDateTimeUtil.parseDateTimeFromDatabase(str.substring(prefixDateLength)));
                            while (reader.peek() == JsonToken.STRING) {

                                list.add(DatabaseDateTimeUtil.parseDateTimeFromDatabase(str.substring(prefixDateLength)));
                            }
                            doc.append(nextName, list);
                        }
                    } else if(nextToken == JsonToken.NUMBER) {

                        //Array aus Zahlen
                        String number = reader.nextString();
                        try {

                            if(number.contains(".")) {

                                //Double
                                List<Double> list = new ArrayList<>();
                                list.add(Double.parseDouble(number));
                                while (reader.peek() == JsonToken.NUMBER) {

                                    list.add(Double.parseDouble(number));
                                }
                                doc.append(nextName, list);
                            } else {

                                //Integer
                                List<Integer> list = new ArrayList<>();
                                list.add(Integer.parseInt(number));
                                while (reader.peek() == JsonToken.NUMBER) {

                                    list.add(Integer.parseInt(number));
                                }
                                doc.append(nextName, list);
                            }
                        } catch (NumberFormatException e) {}
                    } else if(nextToken == JsonToken.BOOLEAN) {

                        //Array aus Wahrheitswerten
                        List<Boolean> list = new ArrayList<>();
                        list.add(reader.nextBoolean());
                        while (reader.peek() == JsonToken.BOOLEAN) {

                            list.add(reader.nextBoolean());
                        }
                        doc.append(nextName, list);
                    } else if(nextToken == JsonToken.END_ARRAY) {

                        //leer
                        doc.append(nextName, new ArrayList<>());
                    }
                    break;
                case END_ARRAY:

                    reader.endArray();
                    break;
                case BEGIN_OBJECT:

                    reader.beginObject();
                    Document subDoc = readJsonObjectToDocument(reader);
                    doc.append(nextName, subDoc);
                    break;
                case END_OBJECT:

                    reader.endObject();
                    return doc;
                case NAME:

                    nextName = reader.nextName();
                    break;
                case STRING:

                    String str = reader.nextString();
                    if(str.startsWith(prefixId)) {

                        //BSON Object ID
                        doc.append(nextName, new ObjectId(str.substring(prefixIdLength)));
                    } else if(str.startsWith(prefixLong)) {

                        //Long
                        try {

                            doc.append(nextName, Long.parseLong(str.substring(prefixLongLength)));
                        } catch (NumberFormatException e) {}
                    } else if(str.startsWith(prefixString)) {

                        //String
                        doc.append(nextName, str.substring(prefixStringLength));
                    } else if(str.startsWith(prefixDate)) {

                        //Datum
                        doc.append(nextName, DatabaseDateTimeUtil.parseDateTimeFromDatabase(str.substring(prefixDateLength)));
                    } else if(str.startsWith(prefixNull)) {

                        //Null
                        doc.append(nextName, null);
                    }
                    break;
                case BOOLEAN:

                    boolean bool = reader.nextBoolean();
                    doc.append(nextName, bool);
                    break;
                case NUMBER:

                    String number = reader.nextString();
                    try {

                        if(number.contains(".")) {

                            //Double
                            doc.append(nextName, Double.parseDouble(number));
                        } else {

                            //Integer
                            doc.append(nextName, Integer.parseInt(number));
                        }
                    } catch (NumberFormatException e) {}
                    break;
                case NULL:

                    reader.nextNull();
                    doc.append(nextName, null);
                    break;
                case END_DOCUMENT:

                    return doc;
            }
        }
    }

    /**
     * entpackt eine Datei aus dem Zip Datenstrom
     *
     * @param target Ziel Datei
     * @param zip Zip Datenstrom
     */
    private static void dumpFile(Path target, ZipInputStream zip) throws IOException {

        if(!Files.exists(target)) {

            try (FileOutputStream fos = new FileOutputStream(target.toFile(), false)) {

                zip.transferTo(fos);
            }
        }
    }
}
