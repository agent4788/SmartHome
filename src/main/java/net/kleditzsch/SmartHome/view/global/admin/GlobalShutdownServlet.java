package net.kleditzsch.SmartHome.view.global.admin;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalShutdownServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getSession().setAttribute("success", true);
        req.getSession().setAttribute("message", "Der Server wird jetzt neu gestartet");
        resp.sendRedirect("/admin/index");

        try {
            Thread.sleep(500);

            Application.getInstance().stop();
            Runtime.getRuntime().exec("sudo shutdown now");
            System.exit(0);
        } catch (Throwable e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
        }
    }
}
