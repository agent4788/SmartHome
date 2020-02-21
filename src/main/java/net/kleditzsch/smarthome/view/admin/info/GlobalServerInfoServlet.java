package net.kleditzsch.smarthome.view.admin.info;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.utility.hardware.Hardware;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class GlobalServerInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/info/info.html");
        JtwigModel model = JtwigModel.newModel();

        //SystemInfo
        model.with("smartHomeVersion", SmartHome.VERSION);
        model.with("properties", System.getProperties());
        model.with("javaMemoryTotal", Runtime.getRuntime().totalMemory());
        model.with("javaMemoryMax", Runtime.getRuntime().maxMemory());
        model.with("javaMemoryFree", Runtime.getRuntime().freeMemory());
        model.with("javaMemoryUsed", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

        //Hardwareinfo
        model.with("hostname", Hardware.getHostname());
        Duration uptime = Hardware.getUptime();
        model.with("uptime", uptime);
        model.with("lastStartTime", Hardware.getLastStartTime(uptime));
        model.with("memoryInfo", Hardware.getMemoryInfo());
        model.with("coreTemperature", Hardware.getCoreTemperature());
        model.with("cpuInfo", Hardware.getCpuInfo());

        //Datenbank Info
        Map<String, Map<String, Object>> serverInfo = SmartHome.getInstance().getDatabaseManager().getServerInfo();
        Map<String, Map<String, Object>> collectionInfo = SmartHome.getInstance().getDatabaseManager().getCollectionInfo();

        model.with("host", serverInfo.get("host"));
        model.with("server", serverInfo.get("server"));
        model.with("db", serverInfo.get("db"));
        model.with("collections", collectionInfo);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
