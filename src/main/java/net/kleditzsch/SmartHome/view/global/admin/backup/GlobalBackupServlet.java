package net.kleditzsch.SmartHome.view.global.admin.backup;

import net.kleditzsch.SmartHome.model.global.backup.BackupFile;
import net.kleditzsch.SmartHome.model.global.editor.BackupEditor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalBackupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/backup/backup.html");
        JtwigModel model = JtwigModel.newModel();

        Map<BackupEditor.Module, List<BackupFile>> backups = BackupEditor.listBackupFiles();
        model.with("globalBackups", backups.get(BackupEditor.Module.Global).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("automationBackups", backups.get(BackupEditor.Module.Automation).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("contactBackups", backups.get(BackupEditor.Module.Contact).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("contractBackups", backups.get(BackupEditor.Module.Contract).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("movieBackups", backups.get(BackupEditor.Module.Movie).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("networkBackups", backups.get(BackupEditor.Module.Network).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("recipeBackups", backups.get(BackupEditor.Module.Recipe).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("shoppingListBackups", backups.get(BackupEditor.Module.ShoppingList).stream().filter(file -> !file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));

        model.with("autoGlobalBackups", backups.get(BackupEditor.Module.Global).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoAutomationBackups", backups.get(BackupEditor.Module.Automation).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoContactBackups", backups.get(BackupEditor.Module.Contact).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoContractBackups", backups.get(BackupEditor.Module.Contract).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoMovieBackups", backups.get(BackupEditor.Module.Movie).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoNetworkBackups", backups.get(BackupEditor.Module.Network).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoRecipeBackups", backups.get(BackupEditor.Module.Recipe).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));
        model.with("autoShoppingListBackups", backups.get(BackupEditor.Module.ShoppingList).stream().filter(file -> file.getFileName().contains("auto_")).sorted(Comparator.comparing(BackupFile::getCreationDateTime)).collect(Collectors.toList()));

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
