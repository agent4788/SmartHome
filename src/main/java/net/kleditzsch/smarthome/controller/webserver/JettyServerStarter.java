package net.kleditzsch.smarthome.controller.webserver;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * konfiguriert und startet den Jetty Webserver mit HTTP2 Support
 */
public class JettyServerStarter implements AutoCloseable {

    /**
     * Port für die verschlüsselte Verbindung
     */
    private int port;

    /**
     * Port für die verschlüsselte Verbindung
     */
    private int sslPort;

    /**
     * Keystore
     */
    private URL keyStore;

    /**
     * Passwort für den Keystore
     */
    private String keyStorePassword;

    /**
     * Server Objekt
     */
    private Server server;

    /**
     * Liste mit den Seiten Registrierungsfunktionen
     */
    private List<WebsiteRegisterFunction> registerFunctions = new ArrayList<>();

    /**
     * @param port Port
     * @param sslPort SSL Port
     * @param keyStore Keystore
     * @param keyStorePassword Passwort für den Keystore
     */
    public JettyServerStarter(int port, int sslPort, URL keyStore, String keyStorePassword) {

        this.port = port;
        this.sslPort = sslPort;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * gint die Liste mit den Seiten Registrierungsfunktionen zurück
     *
     * @return Liste mit den Seiten Registrierungsfunktionen
     */
    public List<WebsiteRegisterFunction> getRegisterFunctions() {

        return registerFunctions;
    }

    /**
     * Konfiguriert den Webserver
     */
    public void config() {

        //Webserver initalisieren
        Server server = new Server();

        //Unverschlüsselter Connector
        ServerConnector defaultConnector = new ServerConnector(server);
        defaultConnector.setPort(port);

        //Verschlüsselter Connector
        HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(sslPort);
        config.setSendXPoweredBy(false);
        config.setSendServerVersion(false);
        config.addCustomizer(new SecureRequestCustomizer());
        /*SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStore.toExternalForm());
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        ServerConnector sslConnector = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(config));
        sslConnector.setPort(sslPort);*/

        server.setConnectors(new Connector[] {defaultConnector/*, sslConnector*/});

        //Statische Inhalte
        ServletContextHandler staticHandler = new ServletContextHandler();
        staticHandler.setContextPath("/static/");
        if (Files.exists(Paths.get("./src/main/resources/webserver/static/"))) {

            staticHandler.setResourceBase("./src/main/resources/webserver/static/");
        } else {

            staticHandler.setResourceBase(this.getClass().getClassLoader().getResource("webserver/static").toExternalForm());
        }
        DefaultServlet defaultServlet = new DefaultServlet();
        ServletHolder holder = new ServletHolder("default", defaultServlet);
        staticHandler.addServlet(holder, "/*");

        //Dynamische Inhalte
        ServletContextHandler dynamicHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        dynamicHandler.setContextPath("/");

        //Seiten regestrieren
        for (WebsiteRegisterFunction registerFunction : registerFunctions) {

            registerFunction.register(dynamicHandler);
        }

        //Contexthandler dem Server bekannt machen
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        contextHandlerCollection.setHandlers(new Handler[] {staticHandler, dynamicHandler});
        server.setHandler(contextHandlerCollection);

        this.server = server;
    }

    /**
     * startet den Server
     */
    public void start() throws Exception {

        server.start();
    }

    /**
     * Synchronisiert den Server
     */
    public void join() throws InterruptedException {

        server.join();
    }

    /**
     * beendet den Server
     */
    @Override
    public void close() throws Exception {

        server.stop();
    }

    /**
     * beendet den Server
     */
    public void stop() throws Exception {

        server.stop();
    }
}
