package net.kleditzsch.SmartHome.controller.global.webserver;

import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Funktion zum registrieren der Webseiten
 */
@FunctionalInterface
public interface WebsiteRegisterFunction {

    void register(ServletContextHandler contextHandler);
}
