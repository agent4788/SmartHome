package net.kleditzsch.smarthome.utility.jtwig.functions;

import net.kleditzsch.smarthome.utility.formatter.DateTimeFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "timeformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof LocalTime) {

            return DateTimeFormatUtil.format((LocalTime) argument);
        } else if (argument instanceof LocalDateTime) {

            return DateTimeFormatUtil.format(((LocalDateTime) argument).toLocalTime());
        }
        return "Formatierungsfehler";
    }
}
