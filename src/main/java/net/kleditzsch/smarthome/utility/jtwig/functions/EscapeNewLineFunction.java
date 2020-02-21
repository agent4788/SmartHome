package net.kleditzsch.smarthome.utility.jtwig.functions;

import com.google.common.html.HtmlEscapers;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class EscapeNewLineFunction extends SimpleJtwigFunction {

    @Override
    public String name() {
        return "escapenl";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);

        Object argument = request.getArguments().get(0);
        if(argument != null && argument instanceof String) {

            String str = HtmlEscapers.htmlEscaper().escape((String) argument);
            str = str.replace("\r\n", "<br>");
            str = str.replace("\r", "<br>");
            str = str.replace("\n", "<br>");
            str = str.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            return str;
        }
        return "";
    }
}
