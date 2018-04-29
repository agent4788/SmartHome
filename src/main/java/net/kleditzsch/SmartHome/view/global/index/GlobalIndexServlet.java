package net.kleditzsch.SmartHome.view.global.index;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class GlobalIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigTemplate.classpathTemplate("/webserver/template/global/index/index.html");
        JtwigModel model = JtwigModel.newModel();



        //Template rendern
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
