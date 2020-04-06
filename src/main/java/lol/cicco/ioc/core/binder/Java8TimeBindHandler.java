package lol.cicco.ioc.core.binder;

import java.time.*;
import java.util.Collection;
import java.util.LinkedList;

public class Java8TimeBindHandler {

    private static final Collection<BindHandler<?>> handlers = new LinkedList<>();

    static {
        synchronized (Java8TimeBindHandler.class) {
            handlers.add(new GeneralBindHandler<LocalDate>(LocalDate.class) {
                @Override
                public LocalDate covertProperty(String propertyName, String propertyValue) {
                    return LocalDate.parse(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<LocalDateTime>(LocalDateTime.class) {
                @Override
                public LocalDateTime covertProperty(String propertyName, String propertyValue) {
                    return LocalDateTime.parse(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<LocalTime>(LocalTime.class) {
                @Override
                public LocalTime covertProperty(String propertyName, String propertyValue) {
                    return LocalTime.parse(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Year>(Year.class) {
                @Override
                public Year covertProperty(String propertyName, String propertyValue) {
                    return Year.of(Integer.parseInt(propertyValue));
                }
            });

            handlers.add(new GeneralBindHandler<Month>(Month.class) {
                @Override
                public Month covertProperty(String propertyName, String propertyValue) {
                    return Month.of(Integer.parseInt(propertyValue));
                }
            });

            handlers.add(new GeneralBindHandler<MonthDay>(MonthDay.class) {
                @Override
                public MonthDay covertProperty(String propertyName, String propertyValue) {
                    return MonthDay.parse(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<YearMonth>(YearMonth.class) {
                @Override
                public YearMonth covertProperty(String propertyName, String propertyValue) {
                    return YearMonth.parse(propertyValue);
                }
            });
        }
    }

    private Java8TimeBindHandler() {

    }

    static Collection<BindHandler<?>> create() {
        return handlers;
    }
}
