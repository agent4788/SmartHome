package net.kleditzsch.smarthome.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.IntegerSetting;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.model.settings.StringSetting;
import net.kleditzsch.smarthome.utility.cli.CliUtil;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Kommandozeilen Konfiguration
 */
public class CliConfiguration {

    /**
     * Logger
     */
    private static Logger logger = LoggerUtil.getLogger(CliConfiguration.class);

    //Daten Objekt
    private static class Config {

        private String host = "127.0.0.1";
        private int port = 27017;
        private String user = "";
        private String pass = "";
        private String db = "smartHome";
    }

    /**
     * bearbeitet die Datenbank Konfiguration
     */
    public static void startDatabaseConfig() {

        System.out.println("Datenbank Einstellungen");

        Path dbConfigFile = Paths.get("db.config.json");

        //Einstellungen laden
        Config config;
        Gson gson = new Gson();;
        try {

            String configData = Files.readString(dbConfigFile);
            config = gson.fromJson(configData, Config.class);
        } catch (IOException e) {

            config = new Config();
        } catch (JsonSyntaxException e) {

            //Debug Ausgabe
            LoggerUtil.serveException(logger, e);
            return;
        }

        try {

            //IP Adresse
            Optional<String> ip = CliUtil.inputIpAddressOption("MongoDB IP Adresse", config.host, 5);
            if(ip.isPresent()) config.host = ip.get();

            //Port
            Optional<Integer> port = CliUtil.inputIntegerOption("MongoDB Port", config.port, 0, 65535, 5);
            if(port.isPresent()) config.port = port.get();

            //Passwort
            Optional<String> user = CliUtil.inputStringOption("MongoDB Benutzername", config.user);
            if(user.isPresent()) config.user = user.get();

            //Passwort
            Optional<String> pass = CliUtil.inputStringOption("MongoDB Passwort", config.pass);
            if(pass.isPresent()) config.pass = pass.get();

            //Datenbank Index
            Optional<String> db = CliUtil.inputStringOption("MongoDB Datenbank", config.db);
            if(db.isPresent()) config.db = db.get();

        } catch (IOException e) {

            //Debug Ausgabe
            LoggerUtil.serveException(logger, e);
        }

        //Einstellungen speichern
        BufferedWriter out;
        try {

            out = Files.newBufferedWriter(dbConfigFile, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            out.write(gson.toJson(config));
            out.close();
            logger.info("Einstellungen erfolgreich gespeichert");
        } catch (IOException e) {

            //Debug Ausgabe
            LoggerUtil.serveException(logger, "Die Einstellungen konnten nicht gespeichert werden", e);
        }
    }

    /**
     * bearbeitet die Anwendungseinstellungen
     */
    public static void startApplicationConfig() {

        SettingsEditor settings = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.WriteLock lock = settings.writeLock();
        lock.lock();

        try {

            IntegerSetting port = settings.getIntegerSetting(Settings.SERVER_PORT);
            Optional<Integer> portOptional = CliUtil.inputIntegerOption("Port", port.getValue(), 1000, 65535, 5);
            portOptional.ifPresent(port::setValue);

            IntegerSetting securePort = settings.getIntegerSetting(Settings.SERVER_SECURE_PORT);
            Optional<Integer> securePortOptional = CliUtil.inputIntegerOption("SSL Port", securePort.getValue(), 1000, 65535, 5);
            securePortOptional.ifPresent(securePort::setValue);

            StringSetting keystorePassword = settings.getStringSetting(Settings.SERVER_KEY_STORE_PASSWORD);
            Optional<String> password = CliUtil.inputStringOption("KeyStore Passwort", keystorePassword.getValue());
            password.ifPresent(keystorePassword::setValue);

            //Einstellungen speichern
            settings.dump();

            logger.info("Einstellungen erfolgreich gespeichert");
        } catch (Exception e) {

            //Debug Ausgabe
            LoggerUtil.serveException(logger, "Die Einstellungen konnten nicht gespeichert werden", e);
        } finally {

            lock.unlock();
        }
    }
}
