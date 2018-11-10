package net.kleditzsch.SmartHome.view.global.admin.backup;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.global.backup.BackupConfiguration;
import net.kleditzsch.SmartHome.controller.global.backup.BackupCreator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;

public class GlobalRunBackupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int runCount = 0;
        BackupConfiguration config = BackupConfiguration.create(Paths.get("backup"));

        if(req.getParameter("all") != null && req.getParameter("all").trim().equals("on")) {

            config.setBackupGlobalDataEnabled(true);
            config.setBackupAutomationDataEnabled(true);
            config.setBackupCalendarDataEnabled(true);
            config.setBackupContactDataEnabled(true);
            config.setBackupMovieDataEnabled(true);
            config.setBackupMusicDataEnabled(true);
            config.setBackupPictureDataEnabled(true);
            config.setBackupRecipeDataEnabled(true);
            config.setBackupShoppingListDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("global") != null && req.getParameter("global").trim().equals("on")) {

            config.setBackupGlobalDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("automation") != null && req.getParameter("automation").trim().equals("on")) {

            config.setBackupAutomationDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("calendar") != null && req.getParameter("calendar").trim().equals("on")) {

            config.setBackupCalendarDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("contact") != null && req.getParameter("contact").trim().equals("on")) {

            config.setBackupContactDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("movie") != null && req.getParameter("movie").trim().equals("on")) {

            config.setBackupMovieDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("music") != null && req.getParameter("music").trim().equals("on")) {

            config.setBackupMusicDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("picture") != null && req.getParameter("picture").trim().equals("on")) {

            config.setBackupPictureDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("recipe") != null && req.getParameter("recipe").trim().equals("on")) {

            config.setBackupRecipeDataEnabled(true);
            runCount++;
        }
        if(req.getParameter("shoppinglist") != null && req.getParameter("shoppinglist").trim().equals("on")) {

            config.setBackupShoppingListDataEnabled(true);
            runCount++;
        }

        if(runCount > 0) {

            try {

                BackupCreator backup = BackupCreator.create(config, Application.getInstance().getDatabaseConnection());
                backup.executeBackup();

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Backups wurden erfolgreich erstellt");
                resp.sendRedirect("/admin/backup");
                return;
            } catch (Exception e) {

                e.printStackTrace();

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Es ist ein Fehler beim erstellen der Backups aufgetreten");
                resp.sendRedirect("/admin/backup");
                return;
            }
        }
        req.getSession().setAttribute("success", false);
        req.getSession().setAttribute("message", "Es wurde kein Modul zum Sichern ausgewählt");
        resp.sendRedirect("/admin/backup");
    }
}
