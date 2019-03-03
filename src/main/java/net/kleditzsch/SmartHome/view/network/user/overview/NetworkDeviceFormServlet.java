package net.kleditzsch.SmartHome.view.network.user.overview;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.network.devices.NetworkDevice;
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
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class NetworkDeviceFormServlet extends HttpServlet {

    static List<String> linkSpeed = Arrays.asList(
            "10 Mbit/s",
            "11 Mbit/s",
            "54 Mbit/s",
            "100 Mbit/s",
            "150 Mbit/s",
            "300 Mbit/s",
            "433 Mbit/s",
            "450 Mbit/s",
            "500 Mbit/s",
            "600 Mbit/s",
            "867 Mbit/s",
            "1 Gbit/s",
            "1.2 Gbit/s",
            "1.3 Gbit/s",
            "2.5 Gbit/s",
            "5 Gbit/s",
            "10 Gbit/s",
            "25 Gbit/s",
            "40 Gbit/s",
            "50 Gbit/s",
            "100 Gbit/s",
            "200 Gbit/s",
            "400 Gbit/s",
            "500 Gbit/s"
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/network/user/overview/deviceform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        NetworkDeviceGroup networkDeviceGroup = null;
        NetworkDevice networkDevice = null;

        if(req.getParameter("group") != null && req.getParameter("devid") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("group").trim());

                Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(id);
                if(networkDeviceGroupOptional.isPresent()) {

                    networkDeviceGroup = networkDeviceGroupOptional.get();

                    ID devId = ID.of(req.getParameter("devid").trim());
                    Optional<NetworkDevice> networkDeviceOptional = networkDeviceGroup.getDevices().stream().filter(dev -> dev.getId().equals(devId)).findFirst();
                    if(networkDeviceOptional.isPresent()) {

                        networkDevice = networkDeviceOptional.get();
                    } else {

                        //Element nicht gefunden
                        throw new Exception();
                    }
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Gerät wurde nicht gefunden");
            }
        } else {

            ID id = ID.of(req.getParameter("group").trim());

            Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(id);
            if(networkDeviceGroupOptional.isPresent()) {

                networkDeviceGroup = networkDeviceGroupOptional.get();
                networkDevice = new NetworkDevice();
                networkDevice.setId(ID.create());
            } else {

                model.with("error", "Das Gerät wurde nicht gefunden");
            }
        }
        model.with("addElement", addElement);
        model.with("networkDeviceGroup", networkDeviceGroup);
        model.with("networkDevice", networkDevice);
        model.with("linkSpeed", this.linkSpeed);

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
        ID networkDeviceId = null;

        boolean addElement = form.getBoolean("addElement", "neues Element");
        networkDeviceGroupId = form.getId("id", "Geräte Gruppen ID");
        if(!addElement) {
            networkDeviceId = form.getId("devid", "Geräte ID");
        }
        String name = form.getString("name", "Name", 3, 50);
        String description = form.optString("description", "Beschreibung", "", 3, 1000);
        String ipAddress = form.optString("ipaddress", "IP Adresse", "", Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"));
        String ain = form.optString("ain", "Ain", "", Pattern.compile("^[\\d\\s]{10,25}$"));
        String mac = form.optString("mac", "MAC Adresse", "", Pattern.compile("^[a-fA-F0-9]{2}\\:[a-fA-F0-9]{2}\\:[a-fA-F0-9]{2}\\:[a-fA-F0-9]{2}\\:[a-fA-F0-9]{2}\\:[a-fA-F0-9]{2}$"));
        NetworkDevice.LinkType linkType = form.getEnum("linktype", "Verbindung", NetworkDevice.LinkType.class);
        String linkSpeed = form.getString("linkspeed", "Verbindungsgeschwindigkeit", this.linkSpeed);
        String hostname = form.optString("hostname", "Hostname", "", 3, 100);

        mac = mac.toUpperCase();

        final ID finalNetworkDeviceId = networkDeviceId;
        Optional<NetworkDeviceGroup> networkDeviceGroupOptional = NetworkDeviceEditor.getNetworkDeviceGroup(networkDeviceGroupId);
        if (form.isSuccessful() && networkDeviceGroupOptional.isPresent()) {

            NetworkDeviceGroup networkDeviceGroup = networkDeviceGroupOptional.get();
            if(addElement) {

                //Neues Element erstellen
                NetworkDevice networkDevice = new NetworkDevice();
                networkDevice.setId(ID.create());
                networkDevice.setName(name);
                networkDevice.setDescription(description);
                networkDevice.setIpAddress(ipAddress.toString());
                networkDevice.setAin(ain);
                networkDevice.setMacAddress(mac);
                networkDevice.setLinkType(linkType);
                networkDevice.setLinkSpeed(linkSpeed);
                if(!hostname.isBlank()) {

                    networkDevice.setHostName(hostname);
                } else if(!ipAddress.isBlank()) {

                    networkDevice.setHostName(InetAddress.getByName(ipAddress).getHostName());
                }

                networkDeviceGroup.getDevices().add(networkDevice);
                NetworkDeviceEditor.updateNetworDeviceGroup(networkDeviceGroup);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Gerät wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/network/index?edit=1");
            } else {

                //Element bearbeiten
                Optional<NetworkDevice> networkDeviceOptional = networkDeviceGroup.getDevices().stream().filter(dev -> dev.getId().equals(finalNetworkDeviceId)).findFirst();
                if(networkDeviceOptional.isPresent()) {

                    NetworkDevice networkDevice = networkDeviceOptional.get();
                    networkDevice.setName(name);
                    networkDevice.setDescription(description);
                    networkDevice.setIpAddress(ipAddress);
                    networkDevice.setAin(ain);
                    networkDevice.setMacAddress(mac);
                    networkDevice.setLinkType(linkType);
                    networkDevice.setLinkSpeed(linkSpeed);
                    if(!hostname.isBlank()) {

                        networkDevice.setHostName(hostname);
                    } else if(!ipAddress.isBlank()) {

                        networkDevice.setHostName(InetAddress.getByName(ipAddress).getHostName());
                    }

                    NetworkDeviceEditor.updateNetworDeviceGroup(networkDeviceGroup);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Gerät wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/network/index?edit=1");
                } else {

                    //Gruppe nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Gerät konnte nicht gefunden werden");
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
