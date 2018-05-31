package net.kleditzsch.SmartHome.controller.global;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.cli.CliUtil;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

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
public class CliConfigurator {

    private static Logger logger = LoggerUtil.getLogger(CliConfigurator.class);

    /**
     * bearbeitet die Datenbank Konfiguration
     */
    public static void startDatabaseConfig() {

        System.out.println("Datenbank Einstellungen");

        Path dbConfigFile = Paths.get("db.config.json");

        //Einstellungen laden
        JsonElement json = null;
        try {

            json = new JsonParser().parse(Files.newBufferedReader(dbConfigFile));
        } catch (IOException e) {

            json = new JsonParser().parse("{\"host\":\"127.0.0.1\",\"port\":6379,\"timeout\":1000,\"pass\":\"\",\"db\":0}");
        }

        JsonObject config = json.getAsJsonObject();

        try {

            //IP Adresse
            Optional<String> ip = CliUtil.inputIpAddressOption("Redis Server IP Adresse", config.get("host").getAsString(), 5);
            if(ip.isPresent()) {

                config.addProperty("host", ip.get());
            }

            //Port
            Optional<Integer> port = CliUtil.inputIntegerOption("Redis Server Port", config.get("port").getAsInt(), 0, 65535, 5);
            if(port.isPresent()) {

                config.addProperty("port", port.get());
            }

            //Timeout
            Optional<Integer> timeout = CliUtil.inputIntegerOption("Redis Server Timeout [in ms]", config.get("timeout").getAsInt(), 500, 10000, 5);
            if(timeout.isPresent()) {

                config.addProperty("timeout", timeout.get());
            }

            //Passwort
            Optional<String> pass = CliUtil.inputIpAddressOption("Redis Server Passwort", config.get("pass").getAsString(), 5);
            if(pass.isPresent()) {

                config.addProperty("pass", pass.get());
            }

            //Datenbank Index
            Optional<Integer> db = CliUtil.inputIntegerOption("Redis Server Datenbankindex", config.get("db").getAsInt(), 0, 100, 5);
            if(db.isPresent()) {

                config.addProperty("db", db.get());
            }
        } catch (IOException e) {

            //Debug Ausgabe
            LoggerUtil.serveException(logger, e);
        }

        //Einstellungen speichern
        BufferedWriter out;
        try {

            out = Files.newBufferedWriter(dbConfigFile, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            out.write(config.toString());
            out.close();
            logger.info("Einstellungen erfolgreich gespeichert");
        } catch (IOException e) {

            System.err.println("Die EInstellungen konnten nicht gespeichert werden");

            //Debug Ausgabe
            LoggerUtil.serveException(logger, e);
        }
    }

    /**
     * bearbeitet die Anwendungseinstellungen
     */
    public static void startApplicationConfig() {

        SettingsEditor settings = Application.getInstance().getSettings();
        ReentrantReadWriteLock.WriteLock lock = settings.writeLock();
        lock.lock();

        try {

            Optional<IntegerSetting> port = settings.getIntegerSetting(SettingsEditor.SERVER_PORT);
            if(port.isPresent()) {

                Optional<Integer> portOptional = CliUtil.inputIntegerOption("Port", port.get().getValue(), 1000, 65535, 5);
                if(portOptional.isPresent()) {

                    port.get().setValue(portOptional.get());
                }
            }

            Optional<IntegerSetting> securePort = settings.getIntegerSetting(SettingsEditor.SERVER_SECURE_PORT);
            if(securePort.isPresent()) {

                Optional<Integer> securePortOptional = CliUtil.inputIntegerOption("SSL Port", securePort.get().getValue(), 1000, 65535, 5);
                if(securePortOptional.isPresent()) {

                    securePort.get().setValue(securePortOptional.get());
                }
            }

            Optional<StringSetting> keystorePassword = settings.getStringSetting(SettingsEditor.SERVER_KEY_STORE_PASSWORD);
            if(keystorePassword.isPresent()) {

                Optional<String> password = CliUtil.inputStringOption("KeyStore Passwort", keystorePassword.get().getValue());
                if(password.isPresent()) {

                    keystorePassword.get().setValue(password.get());
                }
            }

            //Einstellungen speichern
            settings.dump();

            logger.info("Einstellungen erfolgreich gespeichert");
        } catch (IOException e) {

            System.err.println("Die Einstellungen konnten nicht gespeichert werden");

            //Debug Ausgabe
            LoggerUtil.serveException(logger, e);
        } finally {

            lock.unlock();
        }
    }
}
