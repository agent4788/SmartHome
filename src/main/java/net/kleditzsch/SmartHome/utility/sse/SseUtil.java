package net.kleditzsch.SmartHome.utility.sse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * SSE Hilfsklasse
 */
public class SseUtil implements AutoCloseable {

    /**
     * HTTP Response
     */
    private HttpServletResponse httpResponse;

    /**
     * Ausgabestrom
     */
    private PrintStream out;

    /**
     * Header versenden
     */
    private boolean headerSend = false;

    /**
     * Stautus "keine Daten" aktiviert
     */
    private boolean noContent = false;

    /**
     * @param httpResponse HTTP Antwortobjekt
     */
    public SseUtil(HttpServletResponse httpResponse) throws IOException {

        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;

        //Print stream erzeugen
        out = new PrintStream(this.httpResponse.getOutputStream(), false, StandardCharsets.UTF_8);
    }

    /**
     * meldet dem Browser das keine Daten zur verfügung stehen
     */
    public void sendNoContentHeader() {

        //HTTP Header setzen
        if(!headerSend) {

            this.httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            headerSend = true;
            noContent = true;
        }
    }

    /**
     * sendet eine SSE Nachricht
     *
     * @param response SSE Nachricht
     */
    public void sendResponse(SseResponse response) {

        //HTTP Header setzen
        if(!headerSend) {

            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/event-stream; charset=UTF-8");
            headerSend = true;
        }

        //Nachricht senden
        if(!noContent) {

            out.println("data: " + response.getData());
            response.getEvent().ifPresent(entry -> out.println("event: " + entry));
            response.getId().ifPresent(entry -> out.println("id: " + entry));
            response.getRetry().ifPresent(entry -> out.println("retry: " + entry));
            response.getComment().ifPresent(entry -> out.println(": " + entry));
            out.println();
            out.flush();
        } else {

            throw new IllegalStateException("Es wurde versucht eine Nachricht zu senden, obwohl ein No Content Header bereits versendet wurde!");
        }
    }

    /**
     * Schliest den Ausgabestrom
     */
    @Override
    public void close() throws Exception {

        if(out != null) {

            out.close();
        }
    }
}
