package net.kleditzsch.SmartHome.utility.jtwig.functions;

import net.kleditzsch.SmartHome.model.base.ID;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class EqualsFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "equals";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(2).maximumNumberOfArguments(2);

        Object argument1 = request.getArguments().get(0);
        Object argument2 = request.getArguments().get(1);
        if(argument1 != null && argument2 != null) {

            if(argument1 instanceof ID && argument2 instanceof ID) {

                return ((ID) argument1).equals((ID) argument2);
            } else {

                return argument1.equals(argument2);
            }
        }
        return false;
    }
}
