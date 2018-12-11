package net.kleditzsch.SmartHome.view.network.user.overview;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.network.devices.NetworkDeviceGroup;
import net.kleditzsch.SmartHome.model.network.editor.NetworkDeviceEditor;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class NetworkGroupFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/network/user/overview/groupform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        NetworkDeviceGroup networkDeviceGroup = null;

        if(req.getParameter("group") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("group").trim());

                Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(id);
                if(networkDeviceGroupOptional.isPresent()) {

                    networkDeviceGroup = networkDeviceGroupOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Gruppe wurde nicht gefunden");
            }
        } else {

            networkDeviceGroup = new NetworkDeviceGroup();
            networkDeviceGroup.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("networkDeviceGroup", networkDeviceGroup);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        ID networkDeviceGroupId = null;

        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            networkDeviceGroupId = form.getId("id", "Geräte Gruppen ID");
        }
        String name = form.getString("name", "Name", 3, 50);
        String description = form.optString("description", "Beschreibung", "", 3, 1000);

        if (form.isSuccessful()) {

            if(addElement) {

                //Neues Element erstellen
                NetworkDeviceGroup networkDeviceGroup = new NetworkDeviceGroup();
                networkDeviceGroup.setId(ID.create());
                networkDeviceGroup.setName(name);
                networkDeviceGroup.setDescription(description);

                NetworkDeviceEditor.addNetworkDeviceGroup(networkDeviceGroup);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Gerätegruppe wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/network/index?edit=1");
            } else {

                //Element bearbeiten
                Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(networkDeviceGroupId);
                if(networkDeviceGroupOptional.isPresent()) {

                    NetworkDeviceGroup networkDeviceGroup = networkDeviceGroupOptional.get();
                    networkDeviceGroup.setName(name);
                    networkDeviceGroup.setDescription(description);

                    NetworkDeviceEditor.updateNetworDeviceGroup(networkDeviceGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Gerätegruppe wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/network/index?edit=1");
                } else {

                    //Gruppe nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Gerätegruppe konnte nicht gefunden werden");
                    resp.sendRedirect("/network/index?edit=1");
                }
            }
        } else {

            //Fehlerhafte Eingaben
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/network/index?edit=1");
        }
    }
}
