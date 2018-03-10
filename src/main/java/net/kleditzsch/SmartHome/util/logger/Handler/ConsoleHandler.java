package net.kleditzsch.SmartHome.util.logger.Handler;

import java.util.logging.*;

/**
 * gibt Log Meldungen auf die Konsole aus
 *
 * @author Oliver Kleditzsch
 */
public class ConsoleHandler extends Handler {

    @Override
    public void publish(LogRecord record) {

        if (getFormatter() == null) {
            setFormatter(new SimpleFormatter());
        }

        try {
            String message = getFormatter().format(record);
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                System.err.write(message.getBytes());
            } else {
                System.out.write(message.getBytes());
            }
        } catch (Exception exception) {

            reportError(null, exception, ErrorManager.FORMAT_FAILURE);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
