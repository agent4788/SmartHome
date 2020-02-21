package net.kleditzsch.SmartHome.utility.jtwig.functions;

import net.kleditzsch.SmartHome.utility.formatter.SensorValueFormatUtil;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
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
