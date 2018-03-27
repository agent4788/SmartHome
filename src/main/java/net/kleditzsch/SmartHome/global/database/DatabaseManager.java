package net.kleditzsch.SmartHome.global.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Datenbankverwaltung
 */
public class DatabaseManager {

    //Redis Verbindungsdaten
    private String host = "localhost";
    private int port = 6379;
    private int timeout = 1;
    private String pass = "";
    private int db = -1;

    /**
     * Datenbank ClientData
     */
    private Jedis jedis;

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
            timeout = jsonObject.get("timeout").getAsInt();
            pass = jsonObject.get("pass").getAsString();
            db = jsonObject.get("db").getAsInt();

        } catch (IOException e) {

            throw new IllegalStateException("Die Datenbankkonfiguration konnte nicht geladen werden", e);
        }
    }

    /**
     * baut eine Verbindung zur Datenbank auf
     *
     * @return Datenbankverbindung
     */
    public Jedis connectDatabase() {

        //DB Verbinden
        jedis = new Jedis(host, port, timeout);
        if(pass.length() > 0) {

            jedis.auth(pass);
        }
        jedis.select(db);
        return jedis;
    }

    public Map<String, String> getServerInfo() {

        //Map erstellen
        Map<String, String> serverInfoMap = new HashMap<>();
        if(isConnected()) {

            //Datenbankinfo laden und einlesen
            String serverInfo = jedis.info();
            List<String> serverInfoParts = Arrays.asList(serverInfo.split("\n"));

            Pattern pattern = Pattern.compile("^([^:]+):(.+)$");
            serverInfoParts.forEach(e -> {

                Matcher matcher = pattern.matcher(e);
                if(matcher.find()) {

                    serverInfoMap.put(matcher.group(1).trim(), matcher.group(2).trim());
                }
            });
        }
        return serverInfoMap;
    }

    /**
     * gibt die aktuelle Datenbankverbindung zurück
     *
     * @return Datenbankverbindung
     */
    public Jedis getConnection() {

        if(jedis != null && jedis.isConnected()) {

            return jedis;
        }
        throw new IllegalStateException("keine Datenbankverbindung");
    }

    /**
     * prüft ob eine Datenbankverbindung besteht
     *
     * @return Datenbank Verbindung
     */
    public boolean isConnected() {

        return jedis != null && jedis.isConnected();
    }

    /**
     * beendet die Datenbankverbindung
     */
    public void disconnectDatabase() {

        if(jedis != null && jedis.isConnected()) {

            jedis.disconnect();
            jedis = null;
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
