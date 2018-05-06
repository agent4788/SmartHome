package net.kleditzsch.SmartHome.util.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatFunction extends SimpleJtwigFunction {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public String name() {
        return "timeformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof LocalTime) {

            LocalTime localDateTime = (LocalTime) argument;
            return localDateTime.format(formatter);
        } else if (argument instanceof LocalDateTime) {

            LocalDateTime localDateTime = (LocalDateTime) argument;
            return localDateTime.format(formatter);
        }
        return "Formatierungsfehler";
    }
}
