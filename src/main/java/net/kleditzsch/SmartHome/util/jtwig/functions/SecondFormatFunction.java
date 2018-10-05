package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.file.FileUtil;
import net.kleditzsch.SmartHome.util.time.TimeUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class SecondFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "secondformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        if (request.getArguments().get(0) instanceof Duration) {

            return TimeUtil.formatSeconds(((Duration) request.getArguments().get(0)).getSeconds(), true);
        } else if (request.getArguments().get(0) instanceof Integer) {

            return TimeUtil.formatSeconds(((int) request.getArguments().get(0)), false);
        } else if (request.getArguments().get(0) instanceof Long) {

            return TimeUtil.formatSeconds(((long) request.getArguments().get(0)), false);
        } else {

            return TimeUtil.formatSeconds(Long.parseLong(request.getArguments().get(0).toString()), true);
        }
    }
}
