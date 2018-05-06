package net.kleditzsch.SmartHome.util.jtwig.functions;

import net.kleditzsch.SmartHome.util.file.FileUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.util.regex.Matcher;

public class FilesizeFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "filesize";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);
        return FileUtil.formatFilezizeBinary(Long.parseLong(request.getArguments().get(0).toString()));
    }
}
