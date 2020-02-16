package net.kleditzsch.apps.movie.view.admin.disc;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.apps.movie.model.editor.DiscEditor;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieDiscListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/disc/disclist.html");
        JtwigModel model = JtwigModel.newModel();

        DiscEditor de = DiscEditor.createAndLoad();
        List<Disc> discList = de.getDiscsSorted();

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            discList = discList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        ListPagination<Disc> pagination = new ListPagination<>(discList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/movie/admin/disc?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/movie/admin/disc?index=");
        }
        model.with("pagination", pagination);
        model.with("maxOrderId", discList.stream().mapToInt(Disc::getOrderId).summaryStatistics().getMax());

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
