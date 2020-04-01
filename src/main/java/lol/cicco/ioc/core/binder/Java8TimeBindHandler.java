package lol.cicco.ioc.core.binder;

import java.lang.reflect.Type;
import java.time.*;

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
        if(Year.class.equals(bindType)){
            return Year.of(Integer.parseInt(propertyValue));
        }
        if(Month.class.equals(bindType)){
            return Month.of(Integer.parseInt(propertyValue));
        }
        if(MonthDay.class.equals(bindType)){
            return MonthDay.parse(propertyValue);
        }
        if(YearMonth.class.equals(bindType)){
            return YearMonth.parse(propertyValue);
        }
        return null;
    }
}
