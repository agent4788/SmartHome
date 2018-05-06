package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.time.TimeUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;

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
            return numberFormat(request.getArguments().get(0), 0, ",", " ");
        } else if (arg1 instanceof Double || arg1 instanceof Float) {

            //Gleitpunktzahl
            return numberFormat(request.getArguments().get(0), decimals, ",", " ");
        }
        return 0;
    }

    private String numberFormat (Object numberArg, int fractionDigits, String decimalSeparator, String groupingSeparator) {
        Object number = (numberArg == null) ? 0 : numberArg;

        DecimalFormat numberFormat = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbols = numberFormat.getDecimalFormatSymbols();
        numberFormat.setMaximumFractionDigits(fractionDigits);
        numberFormat.setMinimumFractionDigits(fractionDigits);

        if (decimalSeparator != null && !decimalSeparator.isEmpty())
            decimalFormatSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
        else
            decimalFormatSymbols.setDecimalSeparator('.');


        if (groupingSeparator != null && !groupingSeparator.isEmpty())
            decimalFormatSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
        else
            numberFormat.setGroupingUsed(false);

        numberFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        return numberFormat.format(number);
    }
}
