package net.kleditzsch.SmartHome.view.movie.admin.actor;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
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
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieActorListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/actor/actorlist.html");
        JtwigModel model = JtwigModel.newModel();

        ActorEditor ae = ActorEditor.createAndLoad();
        List<Actor> actorList = new ArrayList<>(ae.getData());

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            actorList = actorList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //sortieren
        actorList.sort(Comparator.comparing(Element::getName));

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        int elementsAtPage = 25;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        ListPagination<Actor> pagination = new ListPagination<>(actorList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/movie/admin/actor?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/movie/admin/actor?index=");
        }
        model.with("pagination", pagination);

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
