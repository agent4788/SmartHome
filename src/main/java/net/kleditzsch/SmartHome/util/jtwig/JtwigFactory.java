package net.kleditzsch.SmartHome.util.jtwig;

import net.kleditzsch.SmartHome.util.jtwig.functions.*;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

public abstract class JtwigFactory {

    public static JtwigTemplate fromClasspath(String location) {

        final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
                .configuration()
                    .functions()
                        .add(new FilesizeFunction())
                        .add(new TimeLineFunction())
                        .add(new DateFormatFunction())
                        .add(new TimeFormatFunction())
                        .add(new DateTimeFormatFunction())
                    .and()
                .build();
        return JtwigTemplate.classpathTemplate(location, configuration);
    }
}
