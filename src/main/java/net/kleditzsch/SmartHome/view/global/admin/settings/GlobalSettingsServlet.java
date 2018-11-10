package net.kleditzsch.SmartHome.view.global.admin.settings;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

public class GlobalSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        Optional<BooleanSetting> enableAutoBackupOptional = se.getBooleanSetting(SettingsEditor.BACKUP_ENABLE_AUTO_BACKUP);
        enableAutoBackupOptional.ifPresent(setting -> model.with("enableAutoBackup", setting.getValue()));
        Optional<IntegerSetting> backupAutoCleanupDaysOptional = se.getIntegerSetting(SettingsEditor.BACKUP_AUTO_CLEANUP_DAYS);
        backupAutoCleanupDaysOptional.ifPresent(setting -> model.with("backupAutoCleanupDays", setting.getValue()));

        Optional<BooleanSetting> enableBackupSuccessMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_SUCCESS_MAIL);
        enableBackupSuccessMailOptional.ifPresent(setting -> model.with("enableBackupSuccessMail", setting.getValue()));
        Optional<BooleanSetting> enableBackupErrorMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_ERROR_MAIL);
        enableBackupErrorMailOptional.ifPresent(setting -> model.with("enableBackupErrorMail", setting.getValue()));

        Optional<BooleanSetting> enableBackupFtpUploadOptional = se.getBooleanSetting(SettingsEditor.BACKUP_FTP_UPLOAD);
        enableBackupFtpUploadOptional.ifPresent(setting -> model.with("enableBackupFtpUpload", setting.getValue()));
        Optional<StringSetting> ftpHostOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_HOST);
        ftpHostOptional.ifPresent(setting -> model.with("ftpHost", setting.getValue()));
        Optional<IntegerSetting> ftpPortOptional = se.getIntegerSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PORT);
        ftpPortOptional.ifPresent(setting -> model.with("ftpPort", setting.getValue()));
        Optional<StringSetting> ftpSecureTypeOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_SECURE_TYPE);
        ftpSecureTypeOptional.ifPresent(setting -> model.with("ftpSecureType", setting.getValue()));
        Optional<StringSetting> ftpUserOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_USER);
        ftpUserOptional.ifPresent(setting -> model.with("ftpUser", setting.getValue()));
        Optional<StringSetting> ftpPasswordOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PASSWORD);
        ftpPasswordOptional.ifPresent(setting -> model.with("ftpPassword", setting.getValue()));
        Optional<StringSetting> ftpUploadDirOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_DIRECTORY);
        ftpUploadDirOptional.ifPresent(setting -> model.with("ftpUploadDir", setting.getValue()));

        Optional<StringSetting> mailHostOptional = se.getStringSetting(SettingsEditor.MAIL_HOST);
        mailHostOptional.ifPresent(setting -> model.with("mailHost", setting.getValue()));
        Optional<IntegerSetting> mailPortOptional = se.getIntegerSetting(SettingsEditor.MAIL_PORT);
        mailPortOptional.ifPresent(setting -> model.with("mailPort", setting.getValue()));
        Optional<StringSetting> mailSecureTypeOptional = se.getStringSetting(SettingsEditor.MAIL_SECURE_TYPE);
        mailSecureTypeOptional.ifPresent(setting -> model.with("mailSecureType", setting.getValue()));
        Optional<StringSetting> mailUserOptional = se.getStringSetting(SettingsEditor.MAIL_USER);
        mailUserOptional.ifPresent(setting -> model.with("mailUser", setting.getValue()));
        Optional<StringSetting> mailPasswordOptional = se.getStringSetting(SettingsEditor.MAIL_PASSWORD);
        mailPasswordOptional.ifPresent(setting -> model.with("mailPassword", setting.getValue()));
        Optional<StringSetting> mailReceiverAddressOptional = se.getStringSetting(SettingsEditor.MAIL_RECEIVER_ADDRESS);
        mailReceiverAddressOptional.ifPresent(setting -> model.with("mailReceiverAddress", setting.getValue()));

        //Meldung
        if(req.getSession().getAttribute("success") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            req.getSession().removeAttribute("success");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);

        final boolean enableAutoBackup = form.optBoolean("enableAutoBackup", "Automatisches Backup", false);
        final int backupAutoCleanupDays = form.getInteger("backupAutoCleanupDays", "Löschzeit der automatischen Backups", 1, 365);
        final boolean enableBackupSuccessMail = form.optBoolean("enableBackupSuccessMail", "Mail bei erfolgreichem Backup versenden", false);
        final boolean enableBackupErrorMail = form.optBoolean("enableBackupErrorMail", "Mail bei fehlgeschlagenem Backup versenden", false);
        final boolean enableBackupFtpUpload = form.optBoolean("enableBackupFtpUpload", "FTP Upload aktivieren", false);

        final String ftpHost = form.optString("ftpHost", "FTP Host", "", 3, 255);
        final int ftpPort = form.getInteger("ftpPort", "FTP Port", 0, 65535);
        final String ftpSecureType = form.getString("ftpSecureType", "FTP Verschlüsselung", Arrays.asList("NONE", "TLS"));
        final String ftpUser = form.optString("ftpUser", "FTP User", "", 1, 100);
        final String ftpPassword = form.optString("ftpPassword", "FTP Passwort", "", 1, 100);
        final String ftpUploadDir = form.optString("ftpUploadDir", "FTP Upload Ordner", "", Pattern.compile("^(/[a-zA-Z_+-]+)+$"));

        final String mailHost = form.optString("mailHost", "Mail Host", "", 3, 255);
        final int mailPort = form.getInteger("mailPort", "Mail Port", 0, 65535);
        final String mailSecureType = form.getString("mailSecureType", "Mail Verschlüsselung", Arrays.asList("NONE", "TLS", "SSL"));
        final String mailUser = form.getString("mailUser", "Mail User", 0, 100);
        final String mailPassword = form.optString("mailPassword", "Mail Passwort", "", 1, 100);
        final String mailReceiverAddress = form.optString("mailReceiverAddress", "Mail Empfänger", "", Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"));

        if (form.isSuccessful()) {

            //Einstellungen speichern
            SettingsEditor se = Application.getInstance().getSettings();
            ReentrantReadWriteLock.WriteLock lock = se.writeLock();
            lock.lock();

            //Einstellungen laden
            Optional<BooleanSetting> enableAutoBackupOptional = se.getBooleanSetting(SettingsEditor.BACKUP_ENABLE_AUTO_BACKUP);
            enableAutoBackupOptional.ifPresent(setting -> setting.setValue(enableAutoBackup));
            Optional<IntegerSetting> backupAutoCleanupDaysOptional = se.getIntegerSetting(SettingsEditor.BACKUP_AUTO_CLEANUP_DAYS);
            backupAutoCleanupDaysOptional.ifPresent(setting -> setting.setValue(backupAutoCleanupDays));

            Optional<BooleanSetting> enableBackupSuccessMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_SUCCESS_MAIL);
            enableBackupSuccessMailOptional.ifPresent(setting -> setting.setValue(enableBackupSuccessMail));
            Optional<BooleanSetting> enableBackupErrorMailOptional = se.getBooleanSetting(SettingsEditor.BACKUP_AUTO_ERROR_MAIL);
            enableBackupErrorMailOptional.ifPresent(setting -> setting.setValue(enableBackupErrorMail));

            Optional<BooleanSetting> enableBackupFtpUploadOptional = se.getBooleanSetting(SettingsEditor.BACKUP_FTP_UPLOAD);
            enableBackupFtpUploadOptional.ifPresent(setting -> setting.setValue(enableBackupFtpUpload));
            Optional<StringSetting> ftpHostOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_HOST);
            ftpHostOptional.ifPresent(setting -> setting.setValue(ftpHost));
            Optional<IntegerSetting> ftpPortOptional = se.getIntegerSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PORT);
            ftpPortOptional.ifPresent(setting -> setting.setValue(ftpPort));
            Optional<StringSetting> ftpSecureTypeOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_SECURE_TYPE);
            ftpSecureTypeOptional.ifPresent(setting -> setting.setValue(ftpSecureType));
            Optional<StringSetting> ftpUserOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_USER);
            ftpUserOptional.ifPresent(setting -> setting.setValue(ftpUser));
            Optional<StringSetting> ftpPasswordOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_PASSWORD);
            ftpPasswordOptional.ifPresent(setting -> setting.setValue(ftpPassword));
            Optional<StringSetting> ftpUploadDirOptional = se.getStringSetting(SettingsEditor.BACKUP_FTP_UPLOAD_DIRECTORY);
            ftpUploadDirOptional.ifPresent(setting -> setting.setValue(ftpUploadDir));

            Optional<StringSetting> mailHostOptional = se.getStringSetting(SettingsEditor.MAIL_HOST);
            mailHostOptional.ifPresent(setting -> setting.setValue(mailHost));
            Optional<IntegerSetting> mailPortOptional = se.getIntegerSetting(SettingsEditor.MAIL_PORT);
            mailPortOptional.ifPresent(setting -> setting.setValue(mailPort));
            Optional<StringSetting> mailSecureTypeOptional = se.getStringSetting(SettingsEditor.MAIL_SECURE_TYPE);
            mailSecureTypeOptional.ifPresent(setting -> setting.setValue(mailSecureType));
            Optional<StringSetting> mailUserOptional = se.getStringSetting(SettingsEditor.MAIL_USER);
            mailUserOptional.ifPresent(setting -> setting.setValue(mailUser));
            Optional<StringSetting> mailPasswordOptional = se.getStringSetting(SettingsEditor.MAIL_PASSWORD);
            mailPasswordOptional.ifPresent(setting -> setting.setValue(mailPassword));
            Optional<StringSetting> mailReceiverAddressOptional = se.getStringSetting(SettingsEditor.MAIL_RECEIVER_ADDRESS);
            mailReceiverAddressOptional.ifPresent(setting -> setting.setValue(mailReceiverAddress));

            lock.unlock();

            req.getSession().setAttribute("success", true);
            resp.sendRedirect("/admin/settings");
        } else {

            form.getErrorMessages().forEach((s, s2) -> System.out.println(s + " -> " + s2));

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            resp.sendRedirect("/admin/settings");
        }
    }
}
