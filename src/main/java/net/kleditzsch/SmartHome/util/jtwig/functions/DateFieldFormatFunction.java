package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.formatter.DateTimeFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFieldFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "datefieldformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof LocalDate) {

            return ((LocalDate) argument).format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        } else if (argument instanceof LocalDateTime) {

            return ((LocalDateTime) argument).format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        }
        return "Formatierungsfehler";
    }
}
