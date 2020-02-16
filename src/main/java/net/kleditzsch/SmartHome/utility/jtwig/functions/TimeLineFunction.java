package net.kleditzsch.SmartHome.utility.jtwig.functions;

import net.kleditzsch.SmartHome.utility.datetime.TimeUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeLineFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "timeline";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(2);

        Object argument = request.getArguments().get(0);
        if (argument instanceof Duration) {

            return TimeUtil.formatDuration(((Duration) request.getArguments().get(0)), true);
        } else if (argument instanceof LocalDateTime) {

            return TimeUtil.formatDuration(Duration.between(LocalDateTime.now(), (LocalDateTime) argument), true);
        } else if (argument instanceof LocalDate) {

            return TimeUtil.formatDuration(Duration.between(LocalDate.now(), (LocalDate) argument), true);
        } else if (argument instanceof Integer || argument instanceof Long) {

            return TimeUtil.formatDuration(Duration.ofSeconds((long) argument), true);
        } else {

            if(argument == null && request.getNumberOfArguments() >= 2) {

                return request.getArguments().get(1);
            }
            return argument;
        }
    }
}
