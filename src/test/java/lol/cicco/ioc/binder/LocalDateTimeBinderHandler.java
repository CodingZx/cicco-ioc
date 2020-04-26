package lol.cicco.ioc.binder;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.property.GeneralPropertyHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Registration
public class LocalDateTimeBinderHandler extends GeneralPropertyHandler<LocalDateTime> {

    public LocalDateTimeBinderHandler() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime covertNonNullProperty(String propertyName, String propertyValue) {
        return LocalDateTime.parse(propertyValue, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
    }
}
