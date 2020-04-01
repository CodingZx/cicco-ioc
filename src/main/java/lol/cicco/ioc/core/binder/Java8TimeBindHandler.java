package lol.cicco.ioc.core.binder;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Java8TimeBindHandler extends GeneralBindHandler<Object> {

    public Java8TimeBindHandler(Type type) {
        super(type);
    }

    @Override
    public Object covertProperty(String propertyName, String propertyValue) {
        if(LocalDate.class.equals(bindType)) {
            return LocalDate.parse(propertyValue);
        }
        if(LocalDateTime.class.equals(bindType)) {
            return LocalDateTime.parse(propertyValue);
        }
        if(LocalTime.class.equals(bindType)) {
            return LocalTime.parse(propertyValue);
        }
        return null;
    }
}
