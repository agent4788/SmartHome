package net.kleditzsch.smarthome.utility.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.math.BigDecimal;
import java.util.Random;

public class RandomStringFunction extends SimpleJtwigFunction {
    @Override
    public String name() {
        return "randomstr";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(0).maximumNumberOfArguments(1);

        int length = 32;
        if(request.getNumberOfArguments() > 0) {

            length = ((BigDecimal) request.getArguments().get(0)).intValue() - 1;
        }
        String availableFirstChars = "abcdefghijklmnopqrstuvwxyz";
        String availableSecondChars = "abcdefghijklmnopqrstuvwxyz1234567890";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        sb.append(availableFirstChars.charAt(random.nextInt(availableFirstChars.length())));
        for (int i = 0; i < length; i++) {
            sb.append(availableSecondChars.charAt(random.nextInt(availableSecondChars.length())));
        }

        return sb.toString();
    }
}
