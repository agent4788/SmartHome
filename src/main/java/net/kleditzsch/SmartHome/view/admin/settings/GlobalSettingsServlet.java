package net.kleditzsch.SmartHome.view.admin.settings;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

public class GlobalSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        model.with("enableAutoBackup", se.getBooleanSetting(Settings.BACKUP_ENABLE_AUTO_BACKUP).getValue());
        model.with("backupAutoCleanupDays", se.getIntegerSetting(Settings.BACKUP_AUTO_CLEANUP_DAYS).getValue());

        model.with("enableBackupSuccessMail", se.getBooleanSetting(Settings.BACKUP_AUTO_SUCCESS_MAIL).getValue());
        model.with("enableBackupErrorMail", se.getBooleanSetting(Settings.BACKUP_AUTO_ERROR_MAIL).getValue());

        model.with("enableBackupFtpUpload", se.getBooleanSetting(Settings.BACKUP_FTP_UPLOAD).getValue());
        model.with("ftpHost", se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_HOST).getValue());
        model.with("ftpPort", se.getIntegerSetting(Settings.BACKUP_FTP_UPLOAD_PORT).getValue());
        model.with("ftpSecureType", se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_SECURE_TYPE).getValue());
        model.with("ftpUser", se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_USER).getValue());
        model.with("ftpPassword", se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_PASSWORD).getValue());
        model.with("ftpUploadDir", se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_DIRECTORY).getValue());

        model.with("mailHost", se.getStringSetting(Settings.MAIL_HOST).getValue());
        model.with("mailPort", se.getIntegerSetting(Settings.MAIL_PORT).getValue());
        model.with("mailSecureType", se.getStringSetting(Settings.MAIL_SECURE_TYPE).getValue());
        model.with("mailUser", se.getStringSetting(Settings.MAIL_USER).getValue());
        model.with("mailPassword", se.getStringSetting(Settings.MAIL_PASSWORD).getValue());
        model.with("mailReceiverAddress", se.getStringSetting(Settings.MAIL_RECEIVER_ADDRESS).getValue());

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
            SettingsEditor se = SmartHome.getInstance().getSettings();
            ReentrantReadWriteLock.WriteLock lock = se.writeLock();
            lock.lock();

            //Einstellungen laden
            se.getBooleanSetting(Settings.BACKUP_ENABLE_AUTO_BACKUP).setValue(enableAutoBackup);
            se.getIntegerSetting(Settings.BACKUP_AUTO_CLEANUP_DAYS).setValue(backupAutoCleanupDays);

            se.getBooleanSetting(Settings.BACKUP_AUTO_SUCCESS_MAIL).setValue(enableBackupSuccessMail);
            se.getBooleanSetting(Settings.BACKUP_AUTO_ERROR_MAIL).setValue(enableBackupErrorMail);

            se.getBooleanSetting(Settings.BACKUP_FTP_UPLOAD).setValue(enableBackupFtpUpload);
            se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_HOST).setValue(ftpHost);
            se.getIntegerSetting(Settings.BACKUP_FTP_UPLOAD_PORT).setValue(ftpPort);
            se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_SECURE_TYPE).setValue(ftpSecureType);
            se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_USER).setValue(ftpUser);
            se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_PASSWORD).setValue(ftpPassword);
            se.getStringSetting(Settings.BACKUP_FTP_UPLOAD_DIRECTORY).setValue(ftpUploadDir);

            se.getStringSetting(Settings.MAIL_HOST).setValue(mailHost);
            se.getIntegerSetting(Settings.MAIL_PORT).setValue(mailPort);
            se.getStringSetting(Settings.MAIL_SECURE_TYPE).setValue(mailSecureType);
            se.getStringSetting(Settings.MAIL_USER).setValue(mailUser);
            se.getStringSetting(Settings.MAIL_PASSWORD).setValue(mailPassword);
            se.getStringSetting(Settings.MAIL_RECEIVER_ADDRESS).setValue(mailReceiverAddress);

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
