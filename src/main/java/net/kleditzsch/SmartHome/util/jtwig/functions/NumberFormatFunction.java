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

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(2);

        Object arg1 = request.getArguments().get(0);
        Object arg2 = request.getArguments().size() > 1 ? request.getArguments().get(1) : null;
        int decimals = arg2 != null ? ((BigDecimal) arg2).intValue() : 2;
        if(arg1 instanceof Integer || arg1 instanceof Long) {

            //Ganzzahl
            return NumberFormatUtil.numberFormat(arg1, 0);
        } else if (arg1 instanceof Double || arg1 instanceof Float) {

            //Gleitpunktzahl
            return NumberFormatUtil.numberFormat(arg1, decimals);
        }
        return 0;
    }
}
