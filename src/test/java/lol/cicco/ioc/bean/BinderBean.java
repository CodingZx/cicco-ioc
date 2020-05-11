package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.module.initialize.InitializingBean;
import lombok.Getter;

import java.time.*;

@Registration
public class BinderBean implements InitializingBean {

    @Binder("binder.year")
    private Year year;
    @Binder("binder.month")
    private Month month;
    @Binder("binder.year.month")
    private YearMonth yearMonth;
    @Binder("binder.month.day")
    private MonthDay monthDay;

    @Binder("binder.localdatetime")
    private LocalDateTime localDateTime;

    @Getter
    @Binder(value = "test-fk-value", defaultValue = "10", refresh = true)
    private long value;

    @Getter
    @Binder(value = "test.enum", noValueToNull = true)
    private TestEnum testEnum;

    public void print() {
        System.out.println("year:" + year);
        System.out.println("month:" + month);
        System.out.println("yearMonth:" + yearMonth);
        System.out.println("monthDay:" + monthDay);
        System.out.println("localDateTime:" + localDateTime);
    }

    @Override
    public void afterPropertySet() throws Exception {
        System.out.println("initialize bean.......................");
    }
}
