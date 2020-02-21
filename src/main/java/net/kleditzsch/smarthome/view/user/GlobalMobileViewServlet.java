package net.kleditzsch.smarthome.view.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalMobileViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(req.getParameter("mobile") != null) {

            req.getSession().setAttribute("mobileView", "1");
        } else {

            req.getSession().setAttribute("mobileView", "0");
        }
    }
}
