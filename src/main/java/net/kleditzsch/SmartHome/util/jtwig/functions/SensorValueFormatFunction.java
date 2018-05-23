package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.formatter.SensorValueFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class SensorValueFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "formatsensorvalue";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);
        Object arg1 = request.get(0);
        if(arg1 instanceof SensorValue) {

            return SensorValueFormatUtil.format((SensorValue) arg1);
        }
        return "unbekannter Sensorwert";
    }
}
