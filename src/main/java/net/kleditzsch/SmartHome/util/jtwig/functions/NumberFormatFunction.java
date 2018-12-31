package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.formatter.NumberFormatUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.math.BigDecimal;

public class NumberFormatFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "numberformat";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(3);

        Object arg1 = request.getArguments().get(0);                                                //Wert
        Object arg2 = request.getArguments().size() > 1 ? request.getArguments().get(1) : null;     //Dezimalstellen
        Object arg3 = request.getArguments().size() > 2 ? request.getArguments().get(2) : null;     //Dezimalstellen nur bei Gleitpunktzahlen mit Kommastellen
        int decimals = arg2 != null ? ((BigDecimal) arg2).intValue() : 2;
        if(arg1 instanceof Integer || arg1 instanceof Long) {

            //Ganzzahl
            return NumberFormatUtil.numberFormat(arg1, 0);
        } else if (arg1 instanceof Double || arg1 instanceof Float) {

            //Gleitpunktzahl
            if(arg3 != null && ((Number) arg1).doubleValue() % 1.0 == 0) {
                return NumberFormatUtil.numberFormat(arg1, 0);
            }
            return NumberFormatUtil.numberFormat(arg1, decimals);
        }
        return 0;
    }
}
