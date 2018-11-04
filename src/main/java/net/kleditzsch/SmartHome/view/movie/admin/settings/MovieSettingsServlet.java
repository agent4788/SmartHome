package net.kleditzsch.SmartHome.view.movie.admin.settings;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
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
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        Optional<IntegerSetting> newestMoviesCountOptional = se.getIntegerSetting(SettingsEditor.MOVIE_NEWEST_MOVIES_COUNT);
        newestMoviesCountOptional.ifPresent(setting -> model.with("newestMoviesCount", setting.getValue()));
        Optional<IntegerSetting> bestMoviesCountOptional = se.getIntegerSetting(SettingsEditor.MOVIE_BEST_MOVIES_COUNT);
        bestMoviesCountOptional.ifPresent(setting -> model.with("bestMoviesCount", setting.getValue()));
        Optional<IntegerSetting> paginationUcpOptional = se.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE);
        paginationUcpOptional.ifPresent(setting -> model.with("paginationUcp", setting.getValue()));
        Optional<IntegerSetting> paginationAcpOptional = se.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE);
        paginationAcpOptional.ifPresent(setting -> model.with("paginationAcp", setting.getValue()));
        Optional<StringSetting> tmdbApiKeyOptional = se.getStringSetting(SettingsEditor.MOVIE_TMDB_API_KEY);
        tmdbApiKeyOptional.ifPresent(setting -> model.with("tmdbApiKey", setting.getValue()));

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
        final int newestMoviesCount = form.getInteger("newestMoviesCount", "Anzahl der neusten Filme", 0, 250);
        final int bestMoviesCount = form.getInteger("bestMoviesCount", "Anzahl der besten Filme", 0, 250);
        final int paginationUcp = form.getInteger("paginationUcp", "Anzahl der Filme pro Seite (UCP)", 0, 250);
        final int paginationAcp = form.getInteger("paginationAcp", "Anzahl der Filme pro Seite (ACP)", 0, 250);
        final String tmdbApiKey = form.getString("tmdbApiKey", "The Movie DB API Key", 0, 250);

        if (form.isSuccessful()) {

            //Einstellungen speichern
            SettingsEditor se = Application.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            Optional<IntegerSetting> newestMoviesCountOptional = se.getIntegerSetting(SettingsEditor.MOVIE_NEWEST_MOVIES_COUNT);
            newestMoviesCountOptional.ifPresent(setting -> setting.setValue(newestMoviesCount));
            Optional<IntegerSetting> bestMoviesCountOptional = se.getIntegerSetting(SettingsEditor.MOVIE_BEST_MOVIES_COUNT);
            bestMoviesCountOptional.ifPresent(setting -> setting.setValue(bestMoviesCount));
            Optional<IntegerSetting> paginationUcpOptional = se.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE);
            paginationUcpOptional.ifPresent(setting -> setting.setValue(paginationUcp));
            Optional<IntegerSetting> paginationAcpOptional = se.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE);
            paginationAcpOptional.ifPresent(setting -> setting.setValue(paginationAcp));
            Optional<StringSetting> tmdbApiKeyOptional = se.getStringSetting(SettingsEditor.MOVIE_TMDB_API_KEY);
            tmdbApiKeyOptional.ifPresent(setting -> setting.setValue(tmdbApiKey));

            lock.unlock();

            req.getSession().setAttribute("success", true);
            resp.sendRedirect("/movie/admin/settings");
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            resp.sendRedirect("/movie/admin/settings");
        }
        resp.sendRedirect("/movie/admin/settings");
    }
}
