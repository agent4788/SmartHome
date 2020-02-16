package net.kleditzsch.apps.network.view.user;

import net.kleditzsch.apps.network.model.devices.NetworkDevice;
import net.kleditzsch.apps.network.model.devices.NetworkDeviceGroup;
import net.kleditzsch.apps.network.model.editor.NetworkDeviceEditor;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/network/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        //bearbeitungsmodus
        boolean edit = false;
        if(req.getParameter("edit") != null && req.getParameter("edit").trim().equals("1")) {

            edit = true;
        }
        model.with("edit", edit);

        //Daten laden
        List<NetworkDeviceGroup> networkDeviceGroups = NetworkDeviceEditor.listNetworkDeviceGroups();
        networkDeviceGroups.forEach(group -> {

            List<NetworkDevice> devices = group.getDevices().stream().sorted().collect(Collectors.toList());
            group.getDevices().clear();
            group.getDevices().addAll(devices);
        });
        model.with("networkDeviceGroups", networkDeviceGroups);

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
