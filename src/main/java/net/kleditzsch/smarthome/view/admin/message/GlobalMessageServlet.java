package net.kleditzsch.smarthome.view.admin.message;

import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GlobalMessageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/message/message.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Filter
        Message.Type typeFilter = null;
        if(req.getParameter("filter") != null) {

            switch (req.getParameter("filter").trim()) {

                case "info":

                    typeFilter = Message.Type.info;
                    break;
                case "success":

                    typeFilter = Message.Type.success;
                    break;
                case "warning":

                    typeFilter = Message.Type.warning;
                    break;
                case "error":

                    typeFilter = Message.Type.error;
                    break;
            }
        }

        List<String> modules = MessageEditor.listModules();

        String choosedModul = "";
        String error = null;
        if(req.getParameter("module") != null && modules.contains(req.getParameter("module").trim())) {

            choosedModul = req.getParameter("module").trim();
        } else if(modules.size() > 0) {

            choosedModul = modules.get(0);
        } else {

            error = "Keine Meldungen vorhanden";
        }

        if(!choosedModul.isBlank()) {

            List<Message> messages;
            if(typeFilter != null) {

                messages = MessageEditor.listModuleMessages(choosedModul, typeFilter);
            } else {

                messages = MessageEditor.listModuleMessages(choosedModul);
            }
            ListPagination<Message> pagination = new ListPagination<>(messages, 25, index);
            if(typeFilter != null) {

                pagination.setBaseLink("/admin/message?module=" + choosedModul + "&filter=" + typeFilter.toString() + "&index=");
            } else {

                pagination.setBaseLink("/admin/message?module=" + choosedModul + "&index=");
            }
            model.with("pagination", pagination);
        }

        model.with("modules", modules);
        model.with("choosedModul", choosedModul);
        model.with("error", error);
        model.with("typeFilter", typeFilter);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
