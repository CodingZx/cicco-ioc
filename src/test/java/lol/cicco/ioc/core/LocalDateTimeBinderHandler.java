package lol.cicco.ioc.core;

import lol.cicco.ioc.core.binder.GeneralBindHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeBinderHandler extends GeneralBindHandler<LocalDateTime> {

    public LocalDateTimeBinderHandler() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime covertProperty(String propertyName, String propertyValue) {
        return LocalDateTime.parse(propertyValue, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
    }
}
