package net.kleditzsch.apps.movie.view.admin.settings;

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
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        model.with("newestMoviesCount", se.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).getValue());
        model.with("bestMoviesCount", se.getIntegerSetting(Settings.MOVIE_BEST_MOVIES_COUNT).getValue());
        model.with("paginationUcp", se.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue());
        model.with("paginationAcp", se.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE).getValue());
        model.with("tmdbApiKey", se.getStringSetting(Settings.MOVIE_TMDB_API_KEY).getValue());

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
            SettingsEditor se = SmartHome.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            se.getIntegerSetting(Settings.MOVIE_NEWEST_MOVIES_COUNT).setValue(newestMoviesCount);
            se.getIntegerSetting(Settings.MOVIE_BEST_MOVIES_COUNT).setValue(bestMoviesCount);
            se.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).setValue(paginationUcp);
            se.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE).setValue(paginationAcp);
            se.getStringSetting(Settings.MOVIE_TMDB_API_KEY).setValue(tmdbApiKey);

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
