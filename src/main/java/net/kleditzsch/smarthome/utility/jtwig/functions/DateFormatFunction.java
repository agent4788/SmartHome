package net.kleditzsch.smarthome.utility.jtwig.functions;

import net.kleditzsch.smarthome.utility.formatter.DateTimeFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "dateformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof LocalDate) {

            return DateTimeFormatUtil.format((LocalDate) argument);
        } else if (argument instanceof LocalDateTime) {

            return DateTimeFormatUtil.format(((LocalDateTime) argument).toLocalDate());
        }
        return "Formatierungsfehler";
    }
}
