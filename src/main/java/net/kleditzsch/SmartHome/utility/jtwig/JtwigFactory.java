package net.kleditzsch.SmartHome.utility.jtwig;

import net.kleditzsch.SmartHome.utility.jtwig.functions.*;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

public abstract class JtwigFactory {

    public static JtwigTemplate fromClasspath(String location) {

        final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
                .configuration()
                    .functions()
                        .add(new FilesizeFunction())
                        .add(new SecondFormatFunction())
                        .add(new DateFormatFunction())
                        .add(new TimeFormatFunction())
                        .add(new DateTimeFormatFunction())
                        .add(new TimeLineFunction())
                        .add(new NumberFormatFunction())
                        .add(new RandomStringFunction())
                        .add(new CollectionContainsFunction())
                        .add(new EqualsFunction())
                        .add(new SensorValueFormatFunction())
                        .add(new SensorValueTypeFormatFunction())
                        .add(new MonthNameFunction())
                        .add(new WeekdayNameFunction())
                        .add(new DateFieldFormatFunction())
                        .add(new MinuteFormatFunction())
                        .add(new EscapeNewLineFunction())
                    .and()
                .build();
        return JtwigTemplate.classpathTemplate(location, configuration);
    }
}
