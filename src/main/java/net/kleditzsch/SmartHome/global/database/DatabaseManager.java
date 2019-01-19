package net.kleditzsch.SmartHome.global.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.kleditzsch.SmartHome.global.database.exception.DatabaseException;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Datenbankverwaltung
 */
public class DatabaseManager {

    //Redis Verbindungsdaten
    private String host = "localhost";
    private int port = 27017;
    private String user = "";
    private String pass = "";
    private String db = "smartHome";

    /**
     * Datenbank Client
     */
    private MongoClient mongoClient = null;

    /**
     * Datenbak Konfigurations Datei
     */
    private Path dbConfigFile = Paths.get("db.config.json");

    /**
     * liest die Datenbank Konfiguration ein
     */
    public void readDatabaseConfigFile() {

        Path dbConfigFile = Paths.get("db.config.json");
        try(BufferedReader in = Files.newBufferedReader(dbConfigFile)) {

            JsonObject jsonObject = new JsonParser().parse(in).getAsJsonObject();
            host = jsonObject.get("host").getAsString();
            port = jsonObject.get("port").getAsInt();
            user = jsonObject.get("user").getAsString();
            pass = jsonObject.get("pass").getAsString();
            db = jsonObject.get("db").getAsString();

        } catch (IOException e) {

            throw new IllegalStateException("Die Datenbankkonfiguration konnte nicht geladen werden", e);
        }
    }

    /**
     * baut eine Verbindung zur Datenbank auf
     */
    public void connectDatabase() throws DatabaseException {

        //DB Verbinden
        try {

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
                        builder.readTimeout(1000, TimeUnit.MILLISECONDS);
                    })
                    .applyConnectionString(new ConnectionString("mongodb://" + host + ":" + port))
                    .build();

            mongoClient = MongoClients.create(settings);
        } catch (MongoException e) {

            throw new DatabaseException(e);
        }
    }

    /**
     * gibt die Allgemeinen Informationen zum Datenbankstatus aus
     *
     * @return Datenbankstatus
     */
    public Map<String, Map<String, Object>> getServerInfo() {

        MongoDatabase db = getDatabase();
        Map<String, Map<String, Object>> serverInfoMap = new HashMap<>();

        Map<String, Object> host = new HashMap<>();
        Document hostInfoResults = db.runCommand(new Document("hostInfo", 1));
        host.put("systemCurrentTime", DatabaseDateTimeUtil.dateToLocalDateTime(((Document) hostInfoResults.get("system")).getDate("currentTime")).toString());
        host.put("systemHostname", ((Document) hostInfoResults.get("system")).getString("hostname"));
        host.put("systemCpuAddrSize", ((Document) hostInfoResults.get("system")).getInteger("cpuAddrSize"));
        host.put("systemMemSizeMB", ((Document) hostInfoResults.get("system")).getInteger("memSizeMB"));
        host.put("systemNumCores", ((Document) hostInfoResults.get("system")).getInteger("numCores"));
        host.put("systemCpuArch", ((Document) hostInfoResults.get("system")).getString("cpuArch"));
        host.put("osType", ((Document) hostInfoResults.get("os")).getString("type"));
        host.put("osName", ((Document) hostInfoResults.get("os")).getString("name"));
        host.put("osVersion", ((Document) hostInfoResults.get("os")).getString("version"));
        serverInfoMap.put("host", host);

        Map<String, Object> global = new HashMap<>();
        Document serverStatsResults = db.runCommand(new Document("serverStatus", 1));
        global.put("version", serverStatsResults.getString("version"));
        global.put("uptime", serverStatsResults.getDouble("uptime").longValue());
        global.put("uptimeMillis", serverStatsResults.getLong("uptimeMillis"));
        global.put("localTime", DatabaseDateTimeUtil.dateToLocalDateTime(serverStatsResults.getDate("localTime")));
        global.put("connectionsCurrent", ((Document) serverStatsResults.get("connections")).getInteger("current"));
        global.put("connectionsAvailable", ((Document) serverStatsResults.get("connections")).getInteger("available"));
        global.put("connectionsTotalCreated", ((Document) serverStatsResults.get("connections")).getInteger("totalCreated"));
        global.put("networkBytesIn", ((Document) serverStatsResults.get("network")).getLong("bytesIn"));
        global.put("networkBytesOut", ((Document) serverStatsResults.get("network")).getLong("bytesOut"));
        global.put("networkNumRequests", ((Document) serverStatsResults.get("network")).getLong("numRequests"));
        serverInfoMap.put("server", global);

        Map<String, Object> dbStats = new HashMap<>();
        Document dbStatsResults = db.runCommand(new Document("dbStats", 1));
        dbStats.put("db", dbStatsResults.getString("db"));
        dbStats.put("collections", dbStatsResults.getInteger("collections"));
        dbStats.put("views", dbStatsResults.getInteger("views"));
        dbStats.put("objects", dbStatsResults.getInteger("objects"));
        dbStats.put("avgObjSize", dbStatsResults.getDouble("avgObjSize").longValue());
        dbStats.put("dataSize", dbStatsResults.getDouble("dataSize").longValue());
        dbStats.put("storageSize", dbStatsResults.getDouble("storageSize").longValue());
        serverInfoMap.put("db", dbStats);

        return serverInfoMap;
    }

    /**
     * gibt Informationen zum Status der Collections zurück
     *
     * @return Collection Status
     */
    public Map<String, Map<String, Object>> getCollectionInfo() {

        MongoDatabase db = getDatabase();
        Map<String, Map<String, Object>> collectionInfoMap = new HashMap<>();

        db.listCollectionNames().forEach((Block<String>) collectionName -> {

            Map<String, Object> collectionInfo = new HashMap<>();
            Document collectionInfoResults = db.runCommand(new Document("collStats", collectionName));
            collectionInfo.put("name", collectionName);
            collectionInfo.put("ns", collectionInfoResults.getString("ns"));
            collectionInfo.put("size", collectionInfoResults.getInteger("size"));
            collectionInfo.put("count", collectionInfoResults.getInteger("count"));
            collectionInfo.put("avgObjSize", collectionInfoResults.getInteger("avgObjSize"));
            collectionInfo.put("storageSize", collectionInfoResults.getInteger("storageSize"));
            collectionInfoMap.put(collectionName, collectionInfo);
        });

        return collectionInfoMap;
    }

    /**
     * gibt eine Liste mit den Collection Namen zurück
     *
     * @return Collection Namen
     */
    public List<String> getCollectionNames() {

        MongoDatabase db = getDatabase();
        List<String> collectionNames = new ArrayList<>();

        db.listCollectionNames().forEach((Block<String>) collectionName -> {

            collectionNames.add(collectionName);
        });

        return collectionNames;
    }

    /**
     * gibt die aktuelle Datenbankverbindung zurück
     *
     * @return Datenbankverbindung
     */
    public MongoClient getConnection() {

        if(mongoClient != null) {

            return mongoClient;
        }
        throw new IllegalStateException("keine Datenbankverbindung");
    }

    /**
     * gibt die aktuelle Datenbankverbindung zurück
     *
     * @return Datenbankverbindung
     */
    public MongoDatabase getDatabase() {

        if(mongoClient != null) {

            return mongoClient.getDatabase(db);
        }
        throw new IllegalStateException("keine Datenbankverbindung");
    }

    /**
     * gibt das Objekt der angeforderten Collection zurück
     *
     * @param collectionName Name der angeforderten Collection
     * @return Objekt der angeforderten Collection
     */
    public MongoCollection getCollection(String collectionName) {

        if(mongoClient != null) {

            return mongoClient.getDatabase(db).getCollection(collectionName);
        }
        throw new IllegalStateException("keine Datenbankverbindung");
    }

    /**
     * beendet die Datenbankverbindung
     */
    public void disconnectDatabase() {

        if(mongoClient != null) {

            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * gibt die Datenbank Konfigurations Datei zurück
     *
     * @return Datenbank Konfigurations Datei
     */
    public Path getDbConfigFile() {
        return dbConfigFile;
    }
}
