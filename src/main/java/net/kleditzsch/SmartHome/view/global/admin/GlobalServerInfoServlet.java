package net.kleditzsch.SmartHome.view.global.admin;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.util.hardware.Hardware;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.time.TimeUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class GlobalServerInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/global/admin/info.html");
        JtwigModel model = JtwigModel.newModel();

        //SystemInfo
        model.with("smartHomeVersion", Application.VERSION);
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
        Map<String, String> serverInfo = Application.getInstance().getDatabaseManager().getServerInfo();

        model.with("dbOS", serverInfo.get("os"));
        model.with("dbVersion", serverInfo.get("redis_version"));
        if(serverInfo.containsKey("redis_mode")) {

            model.with("dbMode", serverInfo.get("redis_mode"));
        } else if(serverInfo.containsKey("role")) {

            model.with("dbMode", serverInfo.get("role"));
        }
        model.with("dbUptime", serverInfo.get("uptime_in_seconds"));
        model.with("dbConfigFile", serverInfo.get("config_file"));
        model.with("dbTotalMemory", serverInfo.get("total_system_memory"));
        model.with("dbUsedMemory", serverInfo.get("used_memory"));
        model.with("dbMemoryPeak", serverInfo.get("used_memory_peak"));
        model.with("dbTotalCommands", Long.parseLong(serverInfo.get("total_commands_processed")));

        long lastSaveTime = Long.parseLong(serverInfo.get("rdb_last_save_time"));
        model.with("dbLastSaveTime", Duration.between(LocalDateTime.now(), TimeUtil.getLocalDateTimeOfEpochSeconds(lastSaveTime)));

        model.with("dbLastSaveState", serverInfo.get("rdb_last_bgsave_status"));
        model.with("dbTotalInputBytes", serverInfo.get("total_net_input_bytes"));
        model.with("dbTotalOutputBytes", serverInfo.get("total_net_output_bytes"));
        model.with("fullInfo", serverInfo);


        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
