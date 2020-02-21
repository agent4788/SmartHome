package net.kleditzsch.smarthome.view.admin;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.utility.logger.LoggerUtil;

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

            SmartHome.getInstance().stop();
            Runtime.getRuntime().exec("sudo reboot now");
            System.exit(0);
        } catch (Throwable e) {

            LoggerUtil.serveException(LoggerUtil.getLogger(this.getClass()), e);
        }
    }
}
