package net.kleditzsch.SmartHome.utility.jtwig.functions;

import net.kleditzsch.SmartHome.utility.formatter.DateTimeFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDateTime;

public class DateTimeFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "datetimeformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        return DateTimeFormatUtil.format((LocalDateTime) request.getArguments().get(0));
    }
}
