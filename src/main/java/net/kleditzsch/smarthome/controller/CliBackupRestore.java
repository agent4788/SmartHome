package net.kleditzsch.smarthome.controller;

import net.kleditzsch.smarthome.controller.backup.BackupRestore;
import net.kleditzsch.smarthome.model.backup.BackupFile;
import net.kleditzsch.smarthome.model.editor.BackupEditor;
import net.kleditzsch.smarthome.utility.cli.CliUtil;
import net.kleditzsch.smarthome.utility.formatter.DateTimeFormatUtil;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Sicherung auf der Kommandozeile wieder herstellen
 */
public class CliBackupRestore {

    /**
     * Backup wiederherstellen
     */
    public static void restoreBackup() {

        Logger logger = LoggerUtil.getLogger(CliBackupRestore.class);

        try {

            //Dateien laden
            Map<BackupEditor.Module, List<BackupFile>> backups = BackupEditor.listBackupFiles();

            //Modul auswählen
            Map<Integer, BackupEditor.Module> modules = new HashMap<>(10);
            int i = 0;

            System.out.println("------------------------- Modul --------------------------");
            for (BackupEditor.Module key : backups.keySet()) {

                i++;
                System.out.println((i < 10 ? "0" + i : i ) + ": " + key);
                modules.put(i, key);
            };

            Optional<Integer> moduleOptional = CliUtil.inputIntegerOption("Modul auswählen", 1, 1, i, 5);
            if(moduleOptional.isPresent()) {

                //Backup Datei auswählen
                Map<Integer, BackupFile> moduleBackups = new HashMap<>(25);
                int j = 0;

                if(backups.get(modules.get(moduleOptional.get())).size() > 0) {

                    BackupEditor.Module module = modules.get(moduleOptional.get());

                    System.out.println();
                    System.out.println("------------------------- Backup -------------------------");
                    for (BackupFile backup : backups.get(modules.get(moduleOptional.get()))) {

                        j++;
                        System.out.println((j < 10 ? "0" + j : j ) + ": " + DateTimeFormatUtil.format(backup.getCreationDateTime()) + " -> " + backup.getFileName());
                        moduleBackups.put(j, backup);
                    };

                    Optional<Integer> backupOptional = CliUtil.inputIntegerOption("Backup Datei auswählen", 1, 1, j, 5);
                    if(backupOptional.isPresent()) {

                        BackupFile backupFile = moduleBackups.get(backupOptional.get());

                        System.out.println();
                        Optional<Boolean> doRestoreOption = CliUtil.inputOnOffOption("Soll das Backup wiederhergestellt werden? (alle vorherigen Daten werden dabei gelöscht!)", "ja", "nein", false, 5);
                        if(doRestoreOption.isPresent()) {

                            if(doRestoreOption.get()) {

                                System.out.println();
                                System.out.println("Starte Wiederherstellung von " + backupFile.getFileName());

                                try {

                                    List<String> messages = BackupRestore.restoreModule(module, backupFile);
                                    if(messages.size() == 0) {

                                        System.out.println("Wiederherstellung erfolgreich beendet");
                                    } else {

                                        System.err.println("während der Wiederherstellung sind folgende Fehler aufgetreten");
                                        messages.forEach(message -> System.err.println("\t- " + message));
                                    }
                                } catch (Exception e) {

                                    System.out.println("Wiederherstellung fehlgeschlagen!");
                                    LoggerUtil.serveException(logger, "Wiederherstellung fehlgeschlagen", e);
                                }
                            } else {

                                System.out.println("Wiederherstellung abgebrochen (es wurden keine Daten verändert!)");
                            }
                        } else {

                            System.err.println("Fehlerhafte Eingaben");
                        }
                    } else {

                        System.err.println("Ungültige Datei Auswahl!");
                    }
                } else {

                    System.err.println("Keine Backup Dateien vorhanden!");
                }
            } else {

                System.err.println("Ungültige Modul Auswahl!");
            }
        } catch (IOException e) {

            LoggerUtil.serveException(logger, e);
        }
    }
}
