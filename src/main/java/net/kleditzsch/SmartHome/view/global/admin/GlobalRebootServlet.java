package net.kleditzsch.SmartHome.view.global.admin;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalRebootServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getSession().setAttribute("success", true);
        req.getSession().setAttribute("message", "Der Server wird jetzt neu gestartet");
        resp.sendRedirect("/admin/index");

        try {
            Thread.sleep(500);

            Application.getInstance().stop();
            Runtime.getRuntime().exec("sudo reboot now");
            System.exit(0);
        } catch (Throwable e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
        }
    }
}
