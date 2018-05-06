package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.time.TimeUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.Duration;

public class TimeLineFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "timeline";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        if (request.getArguments().get(0) instanceof Duration) {

            return TimeUtil.formatDuration(((Duration) request.getArguments().get(0)), false);
        } else if (request.getArguments().get(0) instanceof Integer || request.getArguments().get(0) instanceof Long) {

            return TimeUtil.formatDuration(Duration.ofSeconds((long) request.getArguments().get(0)), false);
        } else {

            return request.getArguments().get(0);
        }
    }
}
