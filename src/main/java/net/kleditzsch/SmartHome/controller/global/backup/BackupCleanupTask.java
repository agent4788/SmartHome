package net.kleditzsch.SmartHome.controller.global.backup;

import net.kleditzsch.SmartHome.model.global.backup.BackupFile;
import net.kleditzsch.SmartHome.model.global.editor.BackupEditor;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BackupCleanupTask implements Runnable {

    /**
     * Zeitintervall zum löschen der Backups (in Tagen)
     */
    private int cleanupDays = 10;

    /**
     * @param cleanupDays Zeitintervall zum löschen der Backups (in Tagen)
     */
    public BackupCleanupTask(int cleanupDays) {
        this.cleanupDays = cleanupDays;
    }

    @Override
    public void run() {

        Logger logger = LoggerUtil.getLogger(this);

        try {

            Map<BackupEditor.Module, List<BackupFile>> backupFiles = BackupEditor.listBackupFiles();
            backupFiles.forEach((module, backups) -> {

                backups.forEach(backupFile -> {

                    //Nur automatische Backups löschen
                    if(backupFile.getFileName().contains("auto")) {

                        LocalDateTime deleteTime = LocalDateTime.now().minusDays(cleanupDays);
                        LocalDateTime fileTime = backupFile.getCreationDateTime();
                        if(fileTime.isBefore(deleteTime)) {

                            try {

                                Files.delete(backupFile.getPath());
                            } catch (IOException e) {

                                LoggerUtil.serveException(logger, e);
                            }
                        }
                    }
                });
            });
        } catch (IOException e) {

            LoggerUtil.serveException(logger, e);
        }
    }
}
