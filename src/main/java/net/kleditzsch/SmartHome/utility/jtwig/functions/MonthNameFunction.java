package net.kleditzsch.SmartHome.utility.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.text.DateFormatSymbols;

public class MonthNameFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "monthname";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument instanceof Number) {

            int monthId = (int) argument;
            return new DateFormatSymbols().getMonths()[monthId -1];
        }
        return argument;
    }
}
