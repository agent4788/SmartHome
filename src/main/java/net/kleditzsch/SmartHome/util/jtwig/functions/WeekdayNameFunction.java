package net.kleditzsch.SmartHome.util.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.text.DateFormatSymbols;

public class WeekdayNameFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "weekdayname";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof Number) {

            int weekdayId = (int) argument;
            return new DateFormatSymbols().getWeekdays()[weekdayId];
        }
        return argument;
    }
}
