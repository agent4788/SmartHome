package net.kleditzsch.SmartHome.utility.jtwig.functions;

import net.kleditzsch.SmartHome.utility.datetime.TimeUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class MinuteFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "minuteformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        if (request.getArguments().get(0) instanceof Integer) {

            return TimeUtil.formatSeconds(((int) request.getArguments().get(0)) * 60 + 1, false);
        } else if (request.getArguments().get(0) instanceof Long) {

            return TimeUtil.formatSeconds(((long) request.getArguments().get(0)) * 60 + 1, false);
        } else {

            return TimeUtil.formatSeconds(Long.parseLong(request.getArguments().get(0).toString()) * 60 + 1, true);
        }
    }
}
