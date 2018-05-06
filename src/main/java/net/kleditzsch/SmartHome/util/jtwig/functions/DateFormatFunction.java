package net.kleditzsch.SmartHome.util.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatFunction extends SimpleJtwigFunction {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public String name() {
        return "dateformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof LocalDate) {

            LocalDate localDateTime = (LocalDate) argument;
            return localDateTime.format(formatter);
        } else if (argument instanceof LocalDateTime) {

            LocalDateTime localDateTime = (LocalDateTime) argument;
            return localDateTime.format(formatter);
        }
        return "Formatierungsfehler";
    }
}
