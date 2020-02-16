package net.kleditzsch.SmartHome.view.admin.backup;

import net.kleditzsch.SmartHome.model.backup.BackupFile;
import net.kleditzsch.SmartHome.model.editor.BackupEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class GlobalDeleteBackupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(req.getParameter("hash") != null) {

            String hash = req.getParameter("hash").trim();
            Map<BackupEditor.Module, List<BackupFile>> backupMap = BackupEditor.listBackupFiles();
            for (Map.Entry<BackupEditor.Module, List<BackupFile>> entry : backupMap.entrySet()) {

                for (BackupFile backup : entry.getValue()) {

                    try {

                        if(backup.getHash().equalsIgnoreCase(hash) && Files.exists(backup.getPath())) {

                            Files.delete(backup.getPath());

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Das Backup \"" + backup.getFileName() + "\" wurde erfolgreich gelöscht");
                            resp.sendRedirect("/admin/backup");
                            return;
                        }
                    } catch (Exception e) {

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Das Backup konnte nicht gelöscht werden");
                        resp.sendRedirect("/admin/backup");
                        return;
                    }
                }
            }
        }
        req.getSession().setAttribute("success", true);
        req.getSession().setAttribute("message", "Das Backup konnte nicht gelöscht werden");
        resp.sendRedirect("/admin/backup");
    }
}
