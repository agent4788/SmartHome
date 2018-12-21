package net.kleditzsch.SmartHome.controller.global.backup;

import com.google.gson.stream.JsonWriter;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import net.kleditzsch.SmartHome.util.zip.ZipArchiveCreator;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class BackupCreator {

    /**
     * BackupCreator Konfiguration
     */
    private BackupConfiguration config;

    /**
     * Datenbankobjekt
     */
    private MongoDatabase database;

    /**
     * Standard Dateiname
     */
    private String baseFilename;

    /**
     * @param config Konfiguration
     * @param database Datenbankobjekt
     */
    private BackupCreator(BackupConfiguration config, MongoDatabase database) {

        this.config = config;
        this.database = database;
    }

    /**
     * gibt ein neues BackupCreator Objekt zurück
     *
     * @param config Konfiguration
     * @param database Datenbankobjekt
     */
    public static BackupCreator create(BackupConfiguration config, MongoDatabase database) {

        return  new BackupCreator(config, database);
    }

    /**
     * erstellt die Backups
     *
     * @return Erfolgsmeldung
     */
    public List<Path> executeBackup() {

        Logger logger = LoggerUtil.getLogger(this);
        try {

            baseFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + "__" + ID.create().toString().substring(0, 5) + "_";
            if(config.isAutoBackup()) {

                baseFilename += "auto_";
            }
            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<Future<Path>> futureList = new ArrayList<>(10);
            if(config.isBackupGlobalDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupGlobalData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Global\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupAutomationDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupAutomationData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Automation\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupContractDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupContractData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Verträge\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupContactDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupContactData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Kontakte\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupMovieDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupMovielData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Filme\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupNetworkDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupNetworkData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Netzwerk\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupRecipeDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupRecipeData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Rezepte\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }
            if(config.isBackupShoppingListDataEnabled()) {

                Future<Path> future = executor.submit(() -> {

                    try {

                        return backupShoppingListData();
                    } catch (Exception e) {

                        LoggerUtil.serveException(logger, e);
                        MessageEditor.addMessage(new Message("global", Message.Type.error, "Backup des Modules \"Einkaufsliste\" fehlgeschlagen", e));
                        return null;
                    }
                });
                futureList.add(future);
            }

            //auf Abschluss aller Tasks warten und das Ergebnis abfragen
            List<Path> backupFiles = new ArrayList<>(10);
            for(Future<Path> future : futureList) {

                Path path = future.get();
                if (path != null) {

                    backupFiles.add(path);
                }
            }

            if(backupFiles.size() > 0) {

                MessageEditor.addMessage(new Message("global", Message.Type.success, "Backups erfolgreich erstellt"));
            }

            return backupFiles;
        } catch (Exception e) {

            LoggerUtil.serveException(logger, e);
            MessageEditor.addMessage(new Message("global", Message.Type.error, "Erstellen des Backups fehlgeschlagen", e));
            return null;
        }
    }

    /**
     * erstellt ein BackupCreator der Gobalen Daten
     */
    private Path backupGlobalData() throws Exception {

        List<String> collectionNames = listCollectionNames("global");

        Path zipFile = config.getDestinationDirectory().resolve("global").resolve(baseFilename + "global.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Automations Daten
     */
    private Path backupAutomationData() throws Exception {

        List<String> collectionNames = listCollectionNames("automation");

        Path zipFile = config.getDestinationDirectory().resolve("automation").resolve(baseFilename + "automation.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Verträge Daten
     */
    private Path backupContractData() throws Exception {

        List<String> collectionNames = listCollectionNames("contract");

        Path zipFile = config.getDestinationDirectory().resolve("contract").resolve(baseFilename + "contract.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Kontakt Daten
     */
    private Path backupContactData() throws Exception {

        List<String> collectionNames = listCollectionNames("contact");

        Path zipFile = config.getDestinationDirectory().resolve("contact").resolve(baseFilename + "contact.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Filme Daten
     */
    private Path backupMovielData() throws Exception {

        List<String> collectionNames = listCollectionNames("movie");

        Path zipFile = config.getDestinationDirectory().resolve("movie").resolve(baseFilename + "movie.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }

        //Dateien sichern
        Path upload = Paths.get("upload");
        zip.addDirectory(upload.resolve("cover"), "/upload/cover/", false, true);
        zip.addDirectory(upload.resolve("fskLogo"), "/upload/fskLogo/", false, true);

        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Netzwerk Daten
     */
    private Path backupNetworkData() throws Exception {

        List<String> collectionNames = listCollectionNames("network");

        Path zipFile = config.getDestinationDirectory().resolve("network").resolve(baseFilename + "network.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Rezepte Daten
     */
    private Path backupRecipeData() throws Exception {

        List<String> collectionNames = listCollectionNames("recipe");

        Path zipFile = config.getDestinationDirectory().resolve("recipe").resolve(baseFilename + "recipe.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * erstellt ein BackupCreator der Einkaufslisten Daten
     */
    private Path backupShoppingListData() throws Exception {

        List<String> collectionNames = listCollectionNames("shoppinglist");

        Path zipFile = config.getDestinationDirectory().resolve("shoppinglist").resolve(baseFilename + "shoppinglist.zip");
        Files.createDirectories(zipFile.getParent());
        ZipArchiveCreator zip = ZipArchiveCreator.create(zipFile);
        for (String collectionName : collectionNames) {

            dumpCollectionZipToFile(collectionName, zip, collectionName + ".json");
        }
        zip.close();
        return zipFile;
    }

    /**
     * gibt eine Liste aller Collection Namen die mit dem Präfix beginnen zurück
     *
     * @param prefix Präfix
     * @return Liste mit Collection Namen
     */
    private List<String> listCollectionNames(String prefix) {

        List<String> collectionNames = new ArrayList<>();
        database.listCollectionNames().forEach((Block<? super String>) collectionName -> {
            if(collectionName.startsWith(prefix)) {

                collectionNames.add(collectionName);
            }
        });
        return collectionNames;
    }

    /**
     * speichert alle Daten eine Collection in eine Json Datei
     *
     * @param collectionName Collection Name
     * @param zip Zip Tool
     * @param archiveFileName Dateiname im Archiv
     *
     */
    private void dumpCollectionZipToFile(String collectionName, ZipArchiveCreator zip, String archiveFileName) throws IOException {

        MongoCollection collection = database.getCollection(collectionName);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(zip.addFile(archiveFileName), StandardCharsets.UTF_8));
        writer.beginArray();
        FindIterable iterator = collection.find();
        iterator.forEach((Block<Document>) document -> {

            writeJsonObject(writer, document);
        });
        writer.endArray();
        writer.flush();
    }

    /**
     * schreibt die Daten eines Bson Objekte is die Json Datei
     *
     * @param writer JsonWriter
     * @param document Document
     */
    private void writeJsonObject(JsonWriter writer, Document document) {

        try {

            writer.beginObject();

            Set<String> keySet = document.keySet();
            for(String key : keySet) {

                if(document.get(key) instanceof ObjectId) {

                    writer.name(key).value("id://" + document.getObjectId(key).toString());
                } else if(document.get(key) instanceof Integer) {

                    writer.name(key).value(document.getInteger(key));
                } else if(document.get(key) instanceof Long) {

                    writer.name(key).value("long://" + document.getLong(key));
                } else if(document.get(key) instanceof Double) {

                    writer.name(key).value(document.getDouble(key));
                } else if(document.get(key) instanceof Boolean) {

                    writer.name(key).value(document.getBoolean(key));
                } else if(document.get(key) instanceof String) {

                    writer.name(key).value("string://" + document.getString(key));
                }  else if(document.get(key) instanceof Date) {

                    LocalDateTime localDateTime = DatabaseDateTimeUtil.dateToLocaleDateTime(document.getDate(key));
                    writer.name(key).value("date://" + DatabaseDateTimeUtil.getDatabaseDateTimeStr(localDateTime));
                }  else if(document.get(key) instanceof Document) {

                    writer.name(key);
                    writeJsonObject(writer, (Document) document.get(key));
                }  else if(document.get(key) == null) {

                    writer.name(key).value("null://");
                } else if(document.get(key) instanceof List) {

                    writer.name(key).beginArray();

                    try {

                        List<Document> documents = (List<Document>) document.get(key);
                        for (Document subDoc : documents) {

                            writeJsonObject(writer, subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<String> documents = (List<String>) document.get(key);
                        for (String subDoc : documents) {

                            writer.value("string://" + subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<Integer> documents = (List<Integer>) document.get(key);
                        for (Integer subDoc : documents) {

                            writer.value(subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<Double> documents = (List<Double>) document.get(key);
                        for (Double subDoc : documents) {

                            writer.value(subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<Long> documents = (List<Long>) document.get(key);
                        for (Long subDoc : documents) {

                            writer.value("long://" + subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<Boolean> documents = (List<Boolean>) document.get(key);
                        for (Boolean subDoc : documents) {

                            writer.value(subDoc);
                        }
                    } catch (ClassCastException e) {}
                    try {

                        List<Date> documents = (List<Date>) document.get(key);
                        for (Date subDoc : documents) {

                            LocalDateTime localDateTime = DatabaseDateTimeUtil.dateToLocaleDateTime(subDoc);
                            writer.value("date://" + DatabaseDateTimeUtil.getDatabaseDateTimeStr(localDateTime));
                        }
                    } catch (ClassCastException e) {}

                    writer.endArray();
                } else {

                    System.out.println("Unbekannter Typ: " + key + " -> " + document.get(key).getClass().getTypeName());
                }
            }

            writer.endObject();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
