package net.kleditzsch.SmartHome.model.editor;

import net.kleditzsch.SmartHome.model.backup.BackupFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Verwaltung der Backups
 */
public class BackupEditor {

    /**
     * Backup Module
     */
    public enum Module {

        Global,
        Automation,
        Contact,
        Contract,
        Movie,
        Network,
        Recipe,
        ShoppingList
    };

    /**
     * gibt eine Liste mit allen Backup Dateien aus (gruppiert nach Modulen)
     *
     * @return Liste aller Backup Dateien
     */
    public static Map<Module, List<BackupFile>> listBackupFiles() throws IOException {

        Map<Module, List<BackupFile>> map = new HashMap<>();
        Path backupDir = Paths.get("backup");

        //Dateien
        map.put(Module.Global, listModuleBackups(backupDir.resolve("global")));
        map.put(Module.Automation, listModuleBackups(backupDir.resolve("automation")));
        map.put(Module.Contact, listModuleBackups(backupDir.resolve("contact")));
        map.put(Module.Contract, listModuleBackups(backupDir.resolve("contract")));
        map.put(Module.Movie, listModuleBackups(backupDir.resolve("movie")));
        map.put(Module.Network, listModuleBackups(backupDir.resolve("network")));
        map.put(Module.Recipe, listModuleBackups(backupDir.resolve("recipe")));
        map.put(Module.ShoppingList, listModuleBackups(backupDir.resolve("shoppinglist")));

        return map;
    }

    /**
     * listet alle Backups eines Modul Ordners auf
     *
     * @param moduleBackupDir Ordner
     * @return Liste der Backups
     */
    private static List<BackupFile> listModuleBackups(Path moduleBackupDir) throws IOException {

        List<BackupFile> backups = new ArrayList<>();
        if(Files.exists(moduleBackupDir) && Files.isDirectory(moduleBackupDir)) {

            DirectoryStream<Path> globalDs = Files.newDirectoryStream(moduleBackupDir);
            globalDs.forEach(path -> {

                if(path.getFileName().toString().endsWith(".zip")) {

                    backups.add(new BackupFile(path));
                }
            });
        }
        return backups;
    }
}
