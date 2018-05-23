package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.formatter.DateTimeFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
