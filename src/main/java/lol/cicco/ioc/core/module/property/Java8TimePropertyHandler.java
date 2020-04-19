package lol.cicco.ioc.core.module.property;

import java.time.*;
import java.util.Collection;
import java.util.LinkedList;

public class Java8TimePropertyHandler {

    private static final Collection<PropertyHandler<?>> handlers = new LinkedList<>();

    static {
        handlers.add(new GeneralPropertyHandler<LocalDate>(LocalDate.class) {
            @Override
            public LocalDate covertProperty(String propertyName, String propertyValue) {
                return LocalDate.parse(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<LocalDateTime>(LocalDateTime.class) {
            @Override
            public LocalDateTime covertProperty(String propertyName, String propertyValue) {
                return LocalDateTime.parse(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<LocalTime>(LocalTime.class) {
            @Override
            public LocalTime covertProperty(String propertyName, String propertyValue) {
                return LocalTime.parse(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Year>(Year.class) {
            @Override
            public Year covertProperty(String propertyName, String propertyValue) {
                return Year.of(Integer.parseInt(propertyValue));
            }
        });

        handlers.add(new GeneralPropertyHandler<Month>(Month.class) {
            @Override
            public Month covertProperty(String propertyName, String propertyValue) {
                return Month.of(Integer.parseInt(propertyValue));
            }
        });

        handlers.add(new GeneralPropertyHandler<MonthDay>(MonthDay.class) {
            @Override
            public MonthDay covertProperty(String propertyName, String propertyValue) {
                return MonthDay.parse(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<YearMonth>(YearMonth.class) {
            @Override
            public YearMonth covertProperty(String propertyName, String propertyValue) {
                return YearMonth.parse(propertyValue);
            }
        });
    }

    private Java8TimePropertyHandler() {

    }

    static Collection<PropertyHandler<?>> create() {
        return handlers;
    }
}
