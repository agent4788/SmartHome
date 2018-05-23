package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.formatter.SensorValueFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class SensorValueTypeFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "formatsensortype";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);
        Object arg1 = request.get(0);
        if(arg1 instanceof SensorValue) {

            return SensorValueFormatUtil.formatType((SensorValue) arg1);
        }
        return "unbekannter Sensorwert";
    }
}