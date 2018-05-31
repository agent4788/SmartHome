package net.kleditzsch.SmartHome.controller.global.webserver;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * konfiguriert und startet den Jetty Webserver mit HTTP2 Support
 */
public class JettyServerStarter implements AutoCloseable {

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
     * Server Konfiguration
     */
    private HttpConfiguration config;

    /**
     * @param sslPort SSL Port
     * @param keyStore Keystore
     * @param keyStorePassword Passwort für den Keystore
     */
    public JettyServerStarter(int sslPort, URL keyStore, String keyStorePassword) {

        this.sslPort = sslPort;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;

        this.config = createHttpConfiguration();
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

        HttpConnectionFactory httpFactory = new HttpConnectionFactory(config);
        HTTP2ServerConnectionFactory http2Factory = new HTTP2ServerConnectionFactory(config);
        ALPNServerConnectionFactory alpn = createAlpnProtocolFactory(httpFactory);
        Server server = createServer(httpFactory, http2Factory, alpn);

        ContextHandlerCollection servletHandler = createServletHandlerWithServlet();
        HandlerWrapper gzipHandler = createGzipHandler();
        gzipHandler.setHandler(servletHandler);
        server.setHandler(gzipHandler);

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

    private Server createServer(HttpConnectionFactory httpConnectionFactory, HTTP2ServerConnectionFactory http2ConnectionFactory, ALPNServerConnectionFactory alpn) {

        Server server = new Server();
        server.setRequestLog(new AsyncNCSARequestLog());

        ServerConnector connector = new ServerConnector(server, prepareSsl(alpn), alpn, http2ConnectionFactory, httpConnectionFactory);
        connector.setPort(sslPort);
        server.addConnector(connector);

        return server;
    }

    private GzipHandler createGzipHandler() {

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setIncludedPaths("/*");
        gzipHandler.setMinGzipSize(0);
        gzipHandler.setIncludedMimeTypes("text/plain", "text/html");
        return gzipHandler;
    }

    private ContextHandlerCollection createServletHandlerWithServlet() {

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
        return contextHandlerCollection;
    }

    private ALPNServerConnectionFactory createAlpnProtocolFactory(HttpConnectionFactory httpConnectionFactory) {

        //NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(httpConnectionFactory.getProtocol());
        return alpn;
    }

    private SslConnectionFactory prepareSsl(ALPNServerConnectionFactory alpn) {

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStore.toExternalForm());
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setUseCipherSuitesOrder(true);
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());
        return ssl;
    }

    private HttpConfiguration createHttpConfiguration() {

        HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(sslPort);
        config.setSendXPoweredBy(false);
        config.setSendServerVersion(false);
        config.addCustomizer(new SecureRequestCustomizer());
        return config;
    }
}
