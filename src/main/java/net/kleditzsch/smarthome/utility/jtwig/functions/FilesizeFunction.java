package net.kleditzsch.smarthome.utility.jtwig.functions;

import net.kleditzsch.smarthome.utility.file.FileUtil;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

public class FilesizeFunction extends SimpleJtwigFunction {

    @Override
    public String name() {

        return "filesize";
    }

    @Override
    public Object execute(FunctionRequest request) {

        request.minimumNumberOfArguments(1).maximumNumberOfArguments(1);
        if(request.getArguments().get(0) != null) {

            return FileUtil.formatFilezizeBinary(Long.parseLong(request.getArguments().get(0).toString()));
        }
        return "0 B";
    }
}
