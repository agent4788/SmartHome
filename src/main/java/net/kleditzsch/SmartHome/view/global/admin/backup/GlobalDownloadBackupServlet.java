package net.kleditzsch.SmartHome.view.global.admin.backup;

import net.kleditzsch.SmartHome.model.global.backup.BackupFile;
import net.kleditzsch.SmartHome.model.global.editor.BackupEditor;
import org.eclipse.jetty.util.IO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class GlobalDownloadBackupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(req.getParameter("hash") != null) {

            String hash = req.getParameter("hash").trim();
            Map<BackupEditor.Module, List<BackupFile>> backupMap = BackupEditor.listBackupFiles();
            for (Map.Entry<BackupEditor.Module, List<BackupFile>> entry : backupMap.entrySet()) {

                for (BackupFile backup : entry.getValue()) {

                    try {

                        if(backup.getHash().equalsIgnoreCase(hash) && Files.exists(backup.getPath())) {

                            resp.setStatus(HttpServletResponse.SC_OK);
                            resp.setContentType("application/zip");
                            resp.setHeader("Content-Disposition", "attachment; filename=" + backup.getFileName());

                            InputStream in = new FileInputStream(backup.getPath().toFile());
                            in.transferTo(resp.getOutputStream());
                            return;
                        }
                    } catch (NoSuchAlgorithmException e) {

                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    } catch (IOException e) {

                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                }
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
