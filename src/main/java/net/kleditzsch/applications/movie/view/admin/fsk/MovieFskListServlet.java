package net.kleditzsch.applications.movie.view.admin.fsk;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.ListPagination;
import net.kleditzsch.applications.movie.model.editor.FskEditor;
import net.kleditzsch.applications.movie.model.movie.meta.FSK;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieFskListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/fsk/fsklist.html");
        JtwigModel model = JtwigModel.newModel();

        FskEditor fe = FskEditor.createAndLoad();
        List<FSK> fskList = new ArrayList<>(fe.getData());

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            fskList = fskList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //sortieren
        fskList.sort(Comparator.comparing(FSK::getLevel));

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 20;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE).getValue();
        settingsLock.unlock();

        ListPagination<FSK> pagination = new ListPagination<>(fskList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/movie/admin/fsk?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/movie/admin/fsk?index=");
        }
        model.with("pagination", pagination);
        model.with("maxLevel", fskList.stream().mapToInt(FSK::getLevel).summaryStatistics().getMax());

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
