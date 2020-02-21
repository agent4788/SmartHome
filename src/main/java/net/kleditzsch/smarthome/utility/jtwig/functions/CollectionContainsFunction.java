package net.kleditzsch.smarthome.utility.jtwig.functions;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.util.Collection;

public class CollectionContainsFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "contains";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(2).maximumNumberOfArguments(2);

        Object argument1 = request.getArguments().get(0);
        Object argument2 = request.getArguments().get(1);
        if(argument1 instanceof Collection) {

            Collection collection = (Collection) argument1;
            return collection.contains(argument2);
        }
        return false;
    }
}
