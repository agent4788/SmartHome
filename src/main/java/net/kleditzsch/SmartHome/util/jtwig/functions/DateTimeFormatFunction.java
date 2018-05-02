package net.kleditzsch.SmartHome.util.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatFunction extends SimpleJtwigFunction {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public String name() {
        return "datetimeformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        LocalDateTime localDateTime = (LocalDateTime) request.getArguments().get(0);
        return localDateTime.format(formatter);
    }
}
