package net.kleditzsch.SmartHome.controller.global.backup;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.formatter.DateTimeFormatUtil;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import net.kleditzsch.SmartHome.util.time.TimeUtil;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class BackupTask implements Runnable {

    @Override
    public void run() {

        Logger logger = LoggerUtil.getLogger(this);

        //Einstellungen laden
        SettingsEditor se = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        boolean enableBackupSuccessMail = false;
        boolean enableBackupErrorMail = false;
        boolean enableBackupFtpUpload = false;
        String ftpHost = "";
        int ftpPort = 465;
        String ftpSecureType = "";
        String ftpUser = "";
        String ftpPassword = "";
        String ftpUploadDir = "";
        String mailHost = "";
        int mailPort = 465;
        String mailSecureType = "";
        String mailUser = "";
        String mailPassword = "";
        String mailReceiverAddress = "";

        Optional<BooleanSetting> enableBackupSuccessMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_SUCCESS_MAIL);
        if(enableBackupSuccessMailOptional.isPresent()) {
            enableBackupSuccessMail = enableBackupSuccessMailOptional.get().getValue();
        }
        Optional<BooleanSetting> enableBackupErrorMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_ERROR_MAIL);
        if(enableBackupErrorMailOptional.isPresent()) {
            enableBackupErrorMail = enableBackupErrorMailOptional.get().getValue();
        }

        Optional<BooleanSetting> enableBackupFtpUploadOptional = se.getBooleanSetting(SettingsEditor.BACKUP_FTP_UPLOAD);
        if(enableBackupFtpUploadOptional.isPresent()) {
            enableBackupFtpUpload = enableBackupFtpUploadOptional.get().getValue();
        }
        Optional<StringSetting> ftpHostOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_HOST);
        if(ftpHostOptional.isPresent()) {
            ftpHost = ftpHostOptional.get().getValue();
        }
        Optional<IntegerSetting> ftpPortOptional = se.getIntegerSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PORT);
        if(ftpPortOptional.isPresent()) {
            ftpPort = ftpPortOptional.get().getValue();
        }
        Optional<StringSetting> ftpSecureTypeOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_SECURE_TYPE);
        if(ftpSecureTypeOptional.isPresent()) {
            ftpSecureType = ftpSecureTypeOptional.get().getValue();
        }
        Optional<StringSetting> ftpUserOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_USER);
        if(ftpUserOptional.isPresent()) {
            ftpUser = ftpUserOptional.get().getValue();
        }
        Optional<StringSetting> ftpPasswordOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PASSWORD);
        if(ftpPasswordOptional.isPresent()) {
            ftpPassword = ftpPasswordOptional.get().getValue();
        }
        Optional<StringSetting> ftpUploadDirOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_DIRECTORY);
        if(ftpUploadDirOptional.isPresent()) {
            ftpUploadDir = ftpUploadDirOptional.get().getValue();
        }

        Optional<StringSetting> mailHostOptional = se.getStringSetting(SettingsEditor.MAIL_HOST);
        if(mailHostOptional.isPresent()) {
            mailHost = mailHostOptional.get().getValue();
        }
        Optional<IntegerSetting> mailPortOptional = se.getIntegerSetting(SettingsEditor.MAIL_PORT);
        if(mailPortOptional.isPresent()) {
            mailPort = mailPortOptional.get().getValue();
        }
        Optional<StringSetting> mailSecureTypeOptional = se.getStringSetting(SettingsEditor.MAIL_SECURE_TYPE);
        if(mailSecureTypeOptional.isPresent()) {

            mailSecureType = mailSecureTypeOptional.get().getValue();
        }
        Optional<StringSetting> mailUserOptional = se.getStringSetting(SettingsEditor.MAIL_USER);
        if(mailUserOptional.isPresent()) {
            mailUser = mailUserOptional.get().getValue();
        }
        Optional<StringSetting> mailPasswordOptional = se.getStringSetting(SettingsEditor.MAIL_PASSWORD);
        if(mailPasswordOptional.isPresent()) {
            mailPassword = mailPasswordOptional.get().getValue();
        }
        Optional<StringSetting> mailReceiverAddressOptional = se.getStringSetting(SettingsEditor.MAIL_RECEIVER_ADDRESS);
        if(mailReceiverAddressOptional.isPresent()) {
            mailReceiverAddress = mailReceiverAddressOptional.get().getValue();
        }

        lock.unlock();

        BackupConfiguration config = BackupConfiguration.create(Paths.get("backup"));
        config.setAutoBackup(true);
        config.setBackupGlobalDataEnabled(true);
        config.setBackupAutomationDataEnabled(true);
        config.setBackupContractDataEnabled(true);
        config.setBackupContactDataEnabled(true);
        config.setBackupMovieDataEnabled(true);
        config.setBackupNetworkDataEnabled(true);
        config.setBackupRecipeDataEnabled(true);
        config.setBackupShoppingListDataEnabled(true);

        LocalDateTime start = LocalDateTime.now();

        BackupCreator backup = BackupCreator.create(config, Application.getInstance().getDatabaseConnection());
        List<Path> backupFiles = backup.executeBackup();
        if(backupFiles.size() == 8) {

            //Zeitmessung
            Duration backupTime = Duration.between(start, LocalDateTime.now());

            //erfolgreich -> FTP Upload
            logger.info("Backup Erfolgreich durchgeführt");
            MessageEditor.addMessage(new Message("global", Message.Type.success, "Automatisches Backup erfolgreich durchgeführt"));

            //FTP Upload
            boolean ftpUploadSuccess = true;
            try {

                if(enableBackupFtpUpload) {

                    if(ftpSecureType.equalsIgnoreCase("tls")) {

                        FTPSClient ftps = new FTPSClient();
                        ftps.connect(ftpHost, ftpPort);
                        ftps.login(ftpUser, ftpPassword);
                        ftps.setFileType(FTP.BINARY_FILE_TYPE);

                        for (Path backupFile : backupFiles) {

                            if(Files.exists(backupFile)) {

                                String remoteDirName = (ftpUploadDir.endsWith("/") ? ftpUploadDir : ftpUploadDir) + "/" + backupFile.getParent().getFileName() + "/";
                                String remoteFileName = (ftpUploadDir.endsWith("/") ? ftpUploadDir : ftpUploadDir) + "/" + backupFile.getParent().getFileName() + "/" + backupFile.getFileName();
                                try (FileInputStream fis = new FileInputStream(backupFile.toFile())) {

                                    ftps.mkd(remoteDirName);
                                    boolean success = ftps.storeFile(remoteFileName, fis);
                                    if(!success) {

                                        ftpUploadSuccess = false;
                                    }
                                }
                            }
                        }

                        ftps.disconnect();
                    } else {

                        FTPClient ftp = new FTPClient();
                        ftp.connect(ftpHost, ftpPort);
                        ftp.login(ftpUser, ftpPassword);
                        ftp.setFileType(FTP.BINARY_FILE_TYPE);

                        for (Path backupFile : backupFiles) {

                            if(Files.exists(backupFile)) {

                                String remoteDirName = (ftpUploadDir.endsWith("/") ? ftpUploadDir : ftpUploadDir) + "/" + backupFile.getParent() + "/";
                                String remoteFileName = (ftpUploadDir.endsWith("/") ? ftpUploadDir : ftpUploadDir) + "/" + backupFile.getParent() + "/" + backupFile.getFileName();
                                try (FileInputStream fis = new FileInputStream(backupFile.toFile())) {

                                    ftp.mkd(remoteDirName);
                                    boolean success = ftp.storeFile(remoteFileName, fis);
                                    if(!success) {

                                        ftpUploadSuccess = false;
                                    }
                                }
                            }
                        }

                        ftp.disconnect();
                    }
                }
            } catch (IOException e) {

                LoggerUtil.serveException(logger, "FTP Upload fehlgeschlagen", e);
                MessageEditor.addMessage(new Message("global", Message.Type.warning, "FTP Upload fehlgeschlagen", e));
            }

            //Mail
            try {

                if(enableBackupSuccessMail) {

                    Email mail = new SimpleEmail();
                    mail.setHostName(mailHost);
                    mail.setSmtpPort(mailPort);
                    mail.setAuthenticator(new DefaultAuthenticator(mailUser, mailPassword));
                    if(mailSecureType.equalsIgnoreCase("ssl")) {

                        mail.setSSLOnConnect(true);
                    } else if(mailSecureType.equalsIgnoreCase("tls")) {

                        mail.setStartTLSEnabled(true);
                    }
                    mail.setFrom("noreply@smarthome.home");
                    mail.setSubject("SmartHome Backup erfolgreich");
                    String message = "Hallo,\n" +
                            "\n" +
                            "Das tägliche Backup der SmartHome Daten wurde erfolgreich abgeschlossen.\n" +
                            "Dauer: " + (backupTime.getSeconds() > 1 ? backupTime.getSeconds() + " Sekunden" : "1 Sekunde" ) + "\n" +
                            "FTP Upload: " + (enableBackupFtpUpload  ? (ftpUploadSuccess ? "erfolgreich" : "fehlgeschlagen") : "deaktiviert" ) + "\n" +
                            "\n" +
                            "SmartHome Version: " + Application.VERSION + "\n";
                    mail.setMsg(message);
                    mail.addTo(mailReceiverAddress);
                    mail.send();
                }
            } catch (EmailException e) {

                LoggerUtil.serveException(logger, "Mail Versand fehlgeschlagen", e);
                MessageEditor.addMessage(new Message("global", Message.Type.warning, "Mail Versand fehlgeschlagen", e));
            }
        } else {

            //Fehler -> Mail verschicken wenn aktiviert
            logger.warning("Das Backup wurde mit einem Fehler abgebrochen");
            MessageEditor.addMessage(new Message("global", Message.Type.error, "Das Backup wurde mit einem Fehler abgebrochen"));

            //Mail
            try {

                if(enableBackupErrorMail) {

                    Email mail = new SimpleEmail();
                    mail.setHostName(mailHost);
                    mail.setSmtpPort(mailPort);
                    mail.setAuthenticator(new DefaultAuthenticator(mailUser, mailPassword));
                    if(mailSecureType.equalsIgnoreCase("ssl")) {

                        mail.setSSLOnConnect(true);
                    } else if(mailSecureType.equalsIgnoreCase("tls")) {

                        mail.setStartTLSEnabled(true);
                    }
                    mail.setFrom("noreply@smarthome.home");
                    mail.setSubject("SmartHome Backup fehlgeschlagen");
                    String message = "Hallo,\n" +
                            "\n" +
                            "Das tägliche Backup der SmartHome Daten ist Fehlgeschlagen!.\n" +
                            "\n" +
                            "SmartHome Version: " + Application.VERSION +
                            "\n";
                    mail.setMsg(message);
                    mail.addTo(mailReceiverAddress);
                    mail.send();
                }
            } catch (EmailException e) {

                LoggerUtil.serveException(logger, "Mail Versand fehlgeschlagen", e);
                MessageEditor.addMessage(new Message("global", Message.Type.warning, "Mail Versand fehlgeschlagen", e));
            }
        }
    }
}
